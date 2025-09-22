package org.smarteye.backend.ws;

/** Константы каналов подписки STOMP. */
public final class WsTopics {
    private WsTopics() {}

    /** Системные сообщения, пинги, сервисные уведомления. */
    public static final String SYSTEM = "/topic/system";

    /** Обновления по измерениям (создание/статусы/масса и т.п.). */
    public static final String MEASUREMENTS = "/topic/measurements";

    /** Новые показания весов BEFORE/AFTER. */
    public static final String WEIGHTS = "/topic/weights";
}
