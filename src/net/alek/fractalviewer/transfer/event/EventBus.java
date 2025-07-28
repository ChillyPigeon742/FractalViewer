package net.alek.fractalviewer.transfer.event;

import net.alek.fractalviewer.transfer.event.type.Event;
import net.alek.fractalviewer.transfer.event.type.SubscribeMethod;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class EventBus {

    private static final ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });
    private record EventKey(Event event, Class<? extends Record> payloadType) {}

    private static class Subscriber<T extends Record> {
        final SubscribeMethod mode;
        final Consumer<T> handler;

        Subscriber(SubscribeMethod mode, Consumer<T> handler) {
            this.mode = mode;
            this.handler = handler;
        }
    }

    private final Map<EventKey, List<Subscriber<?>>> subscribers = new ConcurrentHashMap<>();

    public <T extends Record> void subscribe(Event event, SubscribeMethod mode, Consumer<T> handler) {
        Class<T> payloadType = event.getPayloadType();
        EventKey key = new EventKey(event, payloadType);
        subscribers
                .computeIfAbsent(key, k -> new CopyOnWriteArrayList<>())
                .add(new Subscriber<>(mode, handler));
    }

    public <T extends Record> void publish(Event event, T payload) {
        if (event == null) throw new IllegalArgumentException("Event cannot be null");

        Class<T> payloadType = event.getPayloadType();
        if (payload != null && !payloadType.isInstance(payload)) {
            throw new IllegalArgumentException("Payload type mismatch for event " + event);
        }

        EventKey key = new EventKey(event, payloadType);
        List<Subscriber<?>> handlers = subscribers.get(key);

        if (handlers != null) {
            for (Subscriber<?> subscriber : handlers) {
                deliver((Subscriber<T>) subscriber, payload);
            }
        }
    }

    private <T extends Record> void deliver(Subscriber<T> subscriber, T payload) {
        if (subscriber.mode == SubscribeMethod.ASYNC) {
            executor.submit(() -> subscriber.handler.accept(payload));
        } else {
            subscriber.handler.accept(payload);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}