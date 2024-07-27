package dev.atomixsoft.solar_eclipse.core.event;

import dev.atomixsoft.solar_eclipse.core.event.interfaces.EventConsumer;
import dev.atomixsoft.solar_eclipse.core.event.interfaces.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public class EventBus {

    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> m_Listeners;
    private final Map<Class<? extends Event>, List<EventConsumer<? extends Event>>> m_Consumers;
    private final ExecutorService m_Executor;

    public EventBus() {
        m_Listeners = new ConcurrentHashMap<>();
        m_Consumers = new ConcurrentHashMap<>();
        m_Executor = Executors.newCachedThreadPool();
    }

    /**
     * Registers an {@link EventListener} with the EventBus, allowing it to receive events.
     *
     * @param eventType the type of class we'll be registering with
     * @param listener the listener we want to listen for these events
     * @param <T> reflection of event type class
     */
    public <T extends Event> void register(Class<T> eventType, EventListener<T> listener) {
        m_Listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }


    /**
     * Registers an {@link EventConsumer} with the EventBus, allowing it to receive events.
     *
     * @param eventType the type of class we'll be registering with
     * @param consumer the consumer we want to listen for these events
     * @param <T> reflection of event type class
     */
    public <T extends Event> void register(Class<T> eventType, EventConsumer<T> consumer) {
        m_Consumers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(consumer);
    }

    /**
     * Post an Event, letting listeners and consumers decide how they handle them.
     *
     * @param event the event you want to post
     * @param <T> the reflection of the event's class
     */
    public <T extends Event> void post(T event) {
        if(event.handled) return;

        m_Executor.submit(() -> {
            Class<?> eventType = event.getClass();

            List<EventListener<? extends Event>> listeners = m_Listeners.get(eventType);
            if(listeners != null) {
                for(EventListener<? extends Event> listener : listeners)
                    ((EventListener<T>) listener).handleEvent(event);
            }

            List<EventConsumer<? extends Event>> consumers = m_Consumers.get(eventType);
            if(consumers != null) {
                for(EventConsumer<? extends Event> consumer : consumers)
                    ((EventConsumer<T>) consumer).accept(event);
            }
        });
    }

    /**
     * Shuts the ExecutorService down so we aren't wasting resources.
     */
    public void shutdown() {
        m_Executor.shutdown();
        try {
            if (!m_Executor.awaitTermination(60, TimeUnit.SECONDS))
                m_Executor.shutdownNow();
        } catch (InterruptedException e) {
            m_Executor.shutdownNow();
        }
    }

}
