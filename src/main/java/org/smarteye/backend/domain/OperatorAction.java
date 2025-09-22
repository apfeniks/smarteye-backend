package org.smarteye.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Действие оператора по результату измерения (принять/отклонить/повторить и т.п.).
 */
@Entity
@Table(name = "operator_actions",
        indexes = {
                @Index(name = "ix_operator_actions_measurement_id", columnList = "measurement_id"),
                @Index(name = "ix_operator_actions_user_id", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Измерение, по которому принято решение. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_id", nullable = false)
    private Measurement measurement;

    /** Пользователь (оператор), выполнивший действие. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Тип действия: например, ACCEPT / REJECT / REWORK / COMMENT.
     * Оставляем строкой, чтобы не ограничивать фиксированным перечислением.
     */
    @Column(name = "action_type", nullable = false, length = 32)
    private String actionType;

    /** Комментарий оператора (опционально). */
    @Column(name = "comment", length = 1024)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
