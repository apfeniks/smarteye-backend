package org.smarteye.backend.ws;

public interface WsEventsPublisher {
    void measurement(Object payload);
    void weight(Object payload);
    void system(String message);
}