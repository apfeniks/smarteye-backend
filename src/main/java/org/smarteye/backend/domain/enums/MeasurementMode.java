package org.smarteye.backend.domain.enums;

/** Режим запуска/работы измерения. */
public enum MeasurementMode {
    AUTO,      // автоматический режим (обычная работа линии)
    SILENT,    // тихий режим (без участия оператора, решения потом)
    OPERATOR   // операторский режим (ручной триггер/решение)
}
