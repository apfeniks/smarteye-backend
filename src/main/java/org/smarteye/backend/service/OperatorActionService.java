package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.common.exception.NotFoundException;
import org.smarteye.backend.domain.OperatorAction;
import org.smarteye.backend.domain.User;
import org.smarteye.backend.repository.OperatorActionRepository;
import org.smarteye.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Действия оператора по измерениям (ACCEPT/REJECT/REWORK/COMMENT...).
 * Обновление статуса Measurement делается через MeasurementService.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OperatorActionService {

    private final OperatorActionRepository operatorActionRepository;
    private final UserRepository userRepository;
    private final MeasurementService measurementService;

    @Transactional(readOnly = true)
    public OperatorAction getOrThrow(Long id) {
        return operatorActionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Operator action not found: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<OperatorAction> listByMeasurement(Long measurementId) {
        return operatorActionRepository.findAllByMeasurementId(measurementId);
    }

    @Transactional(readOnly = true)
    public List<OperatorAction> listByUser(Long userId) {
        return operatorActionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    /** Создать действие оператора и при необходимости сменить статус измерения. */
    public OperatorAction create(Long measurementId, Long userId, String actionType, String comment) {
        var measurement = measurementService.getOrThrow(measurementId);
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found: id=" + userId));
        }

        OperatorAction oa = OperatorAction.builder()
                .measurement(measurement)
                .user(user)
                .actionType(actionType)
                .comment(comment)
                .build();

        var saved = operatorActionRepository.save(oa);

        // Простейшая автоматизация статусов на основе actionType
        switch (actionType) {
            case "ACCEPT" -> measurementService.markFinished(measurementId);
            case "REJECT" -> measurementService.markRejected(measurementId, "REJECTED_BY_OPERATOR");
            case "REWORK" -> measurementService.markError(measurementId, "REWORK_REQUESTED");
            default -> { /* COMMENT / прочие — без смены статуса */ }
        }

        return saved;
    }

    public void delete(Long id) {
        operatorActionRepository.deleteById(id);
    }
}
