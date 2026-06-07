package dev.atomixsoft.solar_eclipse.server.net.services;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityIdGenerator {

    private final AtomicInteger m_NextId;

    public EntityIdGenerator() {
        m_NextId = new AtomicInteger(1);
    }

    public int generateId() {
        return m_NextId.getAndIncrement();
    }

}
