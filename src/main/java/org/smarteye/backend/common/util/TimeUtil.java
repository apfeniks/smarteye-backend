package org.smarteye.backend.common.util;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/** Вспомогательные методы по работе со временем (UTC). */
@UtilityClass
public class TimeUtil {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /** Текущее время в UTC. */
    public static OffsetDateTime nowUtc() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    /** Разбор строки в ISO-формате (если null/пусто — вернёт null). */
    public static OffsetDateTime parseIsoOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return OffsetDateTime.parse(s, ISO);
    }

    /** Форматирование во вложенный ISO-строковый вид (если null — пустая строка). */
    public static String toIso(OffsetDateTime t) {
        return t == null ? "" : ISO.format(t);
    }
}
