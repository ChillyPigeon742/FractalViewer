package net.alek.fractalviewer.transfer.event.type;

import net.alek.fractalviewer.transfer.event.EventBus;
import net.alek.fractalviewer.transfer.event.payload.CloseAppPayload;
import net.alek.fractalviewer.transfer.event.payload.LogPayload;

import java.util.function.Consumer;

public enum Event {
    START_APP(null),
    LOG(LogPayload.class),
    INIT_GUI(null),
    GUI_READY(null),
    LOAD_GAME(null),
    UNLOAD_GAME(null),
    CLOSE_APP(CloseAppPayload.class);

    private final Class<? extends Record> payloadType;
    private static final EventBus BUS = new EventBus();

    Event(Class<? extends Record> payloadType) {
        this.payloadType = payloadType;
    }

    public <T extends Record> Class<T> getPayloadType() {
        return (Class<T>) payloadType;
    }

    public <T extends Record> void subscribe(SubscribeMethod mode, Consumer<T> handler) {
        BUS.subscribe(this, mode, handler);
    }

    public <T extends Record> void publish(T payload) {
        BUS.publish(this, payload);
    }

    public static void shutdown() {
        BUS.shutdown();
    }
}