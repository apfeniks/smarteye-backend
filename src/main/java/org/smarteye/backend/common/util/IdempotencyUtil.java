package org.smarteye.backend.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * Утилита для идемпотентности: извлекает/генерирует Idempotency-Key.
 */
public final class IdempotencyUtil {

    public static final String HEADER = "Idempotency-Key";

    private IdempotencyUtil() {}

    /**
     * Возвращает значение заголовка Idempotency-Key или генерирует UUID.
     */
    public static String getOrGenerate(HttpServletRequest request) {
        String key = request.getHeader(HEADER);
        return StringUtils.isNotBlank(key) ? key.trim() : UUID.randomUUID().toString();
    }
}
