package com.axis.goal.service.pg;

import com.axis.common.exception.ResourceNotFoundException;
import com.axis.common.security.SecurityUtils;
import com.axis.goal.mapper.GoalTypeMapper;
import com.axis.goal.model.dto.GoalTypeRequest;
import com.axis.goal.model.dto.GoalTypeResponse;
import com.axis.goal.model.entity.GoalType;
import com.axis.goal.repository.GoalTypeRepository;
import com.axis.goal.service.GoalTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalTypeServicePg implements GoalTypeService {

    private final GoalTypeRepository goalTypeRepository;
    private final GoalTypeMapper goalTypeMapper;

    @Override
    @Transactional
    public GoalTypeResponse create(GoalTypeRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Створення нового типу цілі '{}' для користувача: {}", request.title(), userId);

        GoalType goalType = goalTypeMapper.toEntity(request);
        goalType.setUserId(userId);

        if (goalType.getCustomFields() != null) {
            goalType.getCustomFields().forEach(field -> field.setGoalType(goalType));
        }

        GoalType saved = goalTypeRepository.save(goalType);
        log.info("Тип цілі створено з ID: {} для користувача: {}", saved.getId(), userId);

        return goalTypeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public GoalTypeResponse update(UUID id, GoalTypeRequest request) {
        UUID userId = getCurrentUserId();
        log.debug("Оновлення типу цілі: {} для користувача: {}", id, userId);

        GoalType existingType = goalTypeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", id));

        goalTypeMapper.updateEntity(request, existingType);

        existingType.setUserId(userId);
        if (existingType.getCustomFields() != null) {
            existingType.getCustomFields().forEach(field -> field.setGoalType(existingType));
        }

        log.info("Тип цілі оновлено: {} для користувача: {}", id, userId);
        return goalTypeMapper.toResponse(existingType);
    }

    @Override
    public GoalTypeResponse findById(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Пошук типу цілі: {} для користувача: {}", id, userId);

        return goalTypeRepository.findByIdAndUserId(id, userId)
                .map(goalTypeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("GoalType", id));
    }

    @Override
    public Page<GoalTypeResponse> findAll(Pageable pageable) {
        UUID userId = getCurrentUserId();
        log.debug("Отримання всіх типів цілей для користувача: {}", userId);

        return goalTypeRepository.findByUserId(userId, pageable)
                .map(goalTypeMapper::toResponse);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("Видалення типу цілі: {} для користувача: {}", id, userId);

        if (!goalTypeRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("GoalType", id);
        }

        goalTypeRepository.deleteByIdAndUserId(id, userId);
        log.info("Тип цілі видалено: {} разом із пов'язаними даними для користувача: {}", id, userId);
    }

    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserIdAsUUID()
                .orElseThrow(() -> new IllegalStateException("Користувач не авторизований"));
    }
}
