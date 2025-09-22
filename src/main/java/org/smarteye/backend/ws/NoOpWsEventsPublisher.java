package org.smarteye.backend.ws;

import org.springframework.stereotype.Component;

@Component
public class NoOpWsEventsPublisher implements WsEventsPublisher {
    @Override public void measurement(Object payload) { /* no-op */ }
    @Override public void weight(Object payload)      { /* no-op */ }
    @Override public void system(String message)      { /* no-op */ }
}
