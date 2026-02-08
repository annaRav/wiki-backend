# Gateway API Setup (Ingress 2.0)

Gateway API - это современная замена Ingress с более мощными возможностями routing.

## Установка в Minikube

### 1. Установить Gateway API CRDs

```bash
kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.2.0/standard-install.yaml
```

### 2. Установить Nginx Gateway Controller

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes-sigs/gateway-api/main/examples/implementations/nginx/nginx-gateway-controller.yaml
```

Или используйте другой контроллер:
- **Envoy Gateway** (рекомендуется для production): https://gateway.envoyproxy.io
- **Istio Gateway**: https://istio.io/latest/docs/tasks/traffic-management/ingress/gateway-api/
- **Traefik**: https://doc.traefik.io/traefik/routing/providers/kubernetes-gateway/

### 3. Проверить что CRDs установлены

```bash
kubectl get gatewayclass
kubectl get gateway -A
```

### 4. Развернуть приложение

```bash
# Удалить старый deployment (если есть)
kubectl delete namespace axis

# Развернуть с Gateway API
skaffold dev
```

### 5. Проверить Gateway и Routes

```bash
# Gateway
kubectl get gateway -n axis
kubectl describe gateway axis-gateway -n axis

# HTTPRoutes
kubectl get httproute -n axis
```

Вы должны увидеть:
```
NAME            CLASS   ADDRESS        PROGRAMMED   AGE
axis-gateway    nginx   192.168.49.2   True         1m

NAME                  HOSTNAMES   AGE
goal-route            ["*"]       1m
notification-route    ["*"]       1m
media-route           ["*"]       1m
health-route          ["*"]       1m
```

### 6. Доступ к сервисам

Все запросы идут через Gateway на порту 8080 (Skaffold port-forward):

```bash
# Goal service
curl http://localhost:8080/api/goals

# Notification service
curl http://localhost:8080/api/notifications

# Media service
curl http://localhost:8080/api/media

# Health check
curl http://localhost:8080/actuator/health
```

## Преимущества Gateway API

### 1. Более мощный routing

```yaml
# Header-based routing
matches:
  - headers:
    - name: "X-Version"
      value: "v2"

# Query parameter routing
matches:
  - queryParams:
    - name: "env"
      value: "staging"

# Method-based routing
matches:
  - method: "POST"
```

### 2. Traffic splitting (A/B testing, Canary)

```yaml
backendRefs:
  - name: axis-goal-v1
    port: 8081
    weight: 90
  - name: axis-goal-v2
    port: 8081
    weight: 10  # 10% трафика на v2
```

### 3. Request/Response modifications

```yaml
filters:
  - type: RequestHeaderModifier
    requestHeaderModifier:
      add:
        - name: X-Custom-Header
          value: "my-value"
  - type: URLRewrite
    urlRewrite:
      path:
        type: ReplacePrefixMatch
        replacePrefixMatch: /v2/
```

### 4. Timeout и retry policies

```yaml
rules:
  - matches:
    - path:
        type: PathPrefix
        value: /api/goals
    timeouts:
      request: 30s
    backendRefs:
    - name: axis-goal
      port: 8081
```

## Сравнение с Ingress

| Feature | Ingress | Gateway API |
|---------|---------|-------------|
| Path routing | ✅ | ✅ |
| Header routing | ❌ (annotations) | ✅ (native) |
| Query param routing | ❌ | ✅ |
| Traffic splitting | ❌ | ✅ |
| Request modification | ❌ (annotations) | ✅ (native) |
| Multi-protocol | ❌ | ✅ (HTTP, gRPC, TCP, UDP) |
| Role separation | ❌ | ✅ (Class/Gateway/Route) |
| GA Status | ✅ (since 2019) | ✅ (since 2023) |

## Troubleshooting

### Gateway не получает ADDRESS

```bash
# Проверить статус Gateway
kubectl describe gateway axis-gateway -n axis

# Проверить Gateway Controller
kubectl get pods -n gateway-system
kubectl logs -n gateway-system -l app=nginx-gateway-controller
```

### HTTPRoute не работает

```bash
# Проверить статус маршрута
kubectl describe httproute goal-route -n axis

# Посмотреть события
kubectl get events -n axis
```

### 404 Not Found

Проверьте что сервисы запущены:
```bash
kubectl get svc -n axis
kubectl get pods -n axis
```

## Migration Path

Если позже захотите вернуться к простому Ingress:
```bash
# Удалить Gateway API ресурсы
kubectl delete -f k8s/base/gateway-api.yaml

# Применить старый Ingress
kubectl apply -f k8s/base/ingress.yaml
```

## Рекомендации

- ✅ **Используйте Gateway API** для новых проектов
- ✅ **Лучше** чем аннотации Ingress
- ✅ **Стабильный** (GA v1.0)
- ✅ **Будущее** Kubernetes networking
- ✅ **Не нужен service mesh** для advanced routing