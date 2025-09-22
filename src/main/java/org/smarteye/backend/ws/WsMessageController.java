package org.smarteye.backend.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * STOMP контроллер:
 * - клиент отправляет на /app/ping
 * - ответ транслируется подписчикам /topic/system
 */
@Controller
@RequiredArgsConstructor
public class WsMessageController {

    @MessageMapping("/ping")
    @SendTo(WsTopics.SYSTEM)
    public Map<String, Object> ping(Map<String, Object> in) {
        return Map.of(
                "type", "PING",
                "ts", OffsetDateTime.now().toString(),
                "echo", in
        );
    }
}
