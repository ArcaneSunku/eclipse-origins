package dev.atomixsoft.solar_eclipse.core.event.interfaces;

import dev.atomixsoft.solar_eclipse.core.event.Event;

public interface EventConsumer <T extends Event> {
    void accept(T event);
}
