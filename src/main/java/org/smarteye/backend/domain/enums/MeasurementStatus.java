package org.smarteye.backend.domain.enums;

/** Жизненный цикл измерения. */
public enum MeasurementStatus {
    CREATED,         // создано (start)
    IN_PROGRESS,     // идет съём/ожидание данных
    PENDING_REVIEW,  // требуется решение оператора
    FINISHED,        // завершено (файл/метрики получены, принято)
    REJECTED,        // отклонено оператором
    ERROR            // ошибка процесса (разрыв позиции, нет данных и т.п.)
}
