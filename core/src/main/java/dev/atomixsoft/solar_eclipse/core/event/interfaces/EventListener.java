package dev.atomixsoft.solar_eclipse.core.event.interfaces;

import dev.atomixsoft.solar_eclipse.core.event.Event;

public interface EventListener <T extends Event> {
    void handleEvent(T event);
}
