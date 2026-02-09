# Quarkus Development Workflow

## Overview

The project uses a simplified 2-mode architecture with a single `skaffold.yaml`:

- **Dev** (`skaffold dev`): Builds JVM images locally in Minikube, deploys all infra + services, auto-rebuilds on changes
- **Prod** (`skaffold run -p prod`): Pulls native images from GitHub Container Registry, no local builds

---

## Kustomize Structure

```
k8s/
├── base/                      # Base manifests (all services + infrastructure)
└── overlays/
    ├── dev/                   # For local development (1 file)
    │   └── kustomization.yaml # Adds environment label only, no patches
    └── prod/                  # For production (2 files)
        ├── kustomization.yaml # Swaps images to ghcr.io
        └── prod-patches.yaml  # Native resource limits (64Mi/128Mi)
```

---

## Modes

### 1. Local Development (Recommended)

Single command to build and deploy everything:

```bash
# Start Minikube (if not running)
minikube start

# Point Docker CLI to Minikube's Docker daemon
eval $(minikube docker-env)

# Build JVM images, deploy all infra + services, port-forward
skaffold dev
```

This will:
- Run `./gradlew :axis-*:build -x test` for each service
- Build 3 JVM Docker images
- Deploy all infrastructure (PostgreSQL, Keycloak, MongoDB, RabbitMQ, Redis)
- Deploy all services (axis-goal, axis-media, axis-notification)
- Set up port-forwarding for all services
- Watch for file changes and auto-rebuild affected services

**Access Points:**
- API Gateway: http://localhost:8080
- Keycloak: http://localhost:8180
- RabbitMQ Management: http://localhost:15672
- PostgreSQL: localhost:5433
- MongoDB: localhost:27017
- Redis: localhost:6379

---

### 2. Testing Production Images

Verify native images from GitHub Container Registry without local builds:

```bash
# Deploy native images from GHCR
skaffold run -p prod

# Delete deployment
skaffold delete -p prod
```

**When to use:**
- Verify images before release
- Test CI/CD pipeline output
- Confirm native images work correctly

---

## Performance Comparison

| Configuration | Memory (req/limit) | Startup (liveness/readiness) | Image Size |
|--------------|-------------------|------------------------------|------------|
| **Dev (JVM)** | 256Mi / 512Mi | 45s / 30s | ~200MB |
| **Prod (Native)** | 64Mi / 128Mi | 15s / 10s | ~50MB |

---

## CI/CD: GitHub Actions

### Workflow for Building Native Images

```yaml
name: Build and Push Native Images

on:
  push:
    branches: [ master ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up GraalVM
        uses: graalvm/setup-graalvm@v1
        with:
          java-version: '21'
          distribution: 'graalvm'

      - name: Build native images
        run: |
          ./gradlew :axis-goal:build -Dquarkus.package.type=native \
            -Dquarkus.native.container-build=true
          ./gradlew :axis-media:build -Dquarkus.package.type=native \
            -Dquarkus.native.container-build=true
          ./gradlew :axis-notification:build -Dquarkus.package.type=native \
            -Dquarkus.native.container-build=true

      - name: Login to GitHub Container Registry
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build and push Docker images
        run: |
          docker build -f axis-goal/src/main/docker/Dockerfile.native \
            -t ghcr.io/${{ github.repository_owner }}/axis-goal:latest .
          docker push ghcr.io/${{ github.repository_owner }}/axis-goal:latest
          # Repeat for other services
```

---

## Troubleshooting

### Problem: Skaffold build fails

**Solution:**
```bash
# Clean Gradle build cache
./gradlew clean

# Verify Docker is pointing to Minikube
eval $(minikube docker-env)

# Retry
skaffold dev
```

### Problem: Images from GitHub won't pull

**Solution:**
```bash
# Check access to GitHub Container Registry
docker login ghcr.io -u YOUR_USERNAME

# Verify images are public or imagePullSecret is configured
```

### Problem: Service not reachable via Ingress

**Solution:**
```bash
# Check all pods are running
kubectl get pods -n axis

# Check Ingress configuration
kubectl describe ingress -n axis

# Verify port-forwarding
kubectl port-forward -n axis svc/axis-gateway-nginx 8080:80
```

---

## Recommended Team Workflow

1. **Daily development:**
   - `eval $(minikube docker-env) && skaffold dev`
   - Code changes auto-rebuild and redeploy

2. **Before push to main:**
   - Run tests locally
   - Verify all services work via localhost:8080

3. **After merge to main:**
   - CI/CD builds native images
   - Push to GitHub Container Registry
   - Test with `skaffold run -p prod`

4. **Release:**
   - Images already in GHCR
   - Deploy with the desired tag

---

## Additional Resources

- [Quarkus Guides](https://quarkus.io/guides/)
- [Skaffold Documentation](https://skaffold.dev/docs/)
- [Kustomize Patches](https://kubectl.docs.kubernetes.io/references/kustomize/patches/)
