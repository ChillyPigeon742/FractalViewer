package net.alek.fractalviewer.transfer.event.type;

import net.alek.fractalviewer.transfer.event.EventBus;
import net.alek.fractalviewer.transfer.event.payload.CloseAppPayload;
import net.alek.fractalviewer.transfer.event.payload.DrawDataPayload;
import net.alek.fractalviewer.transfer.event.payload.LogPayload;

import java.util.function.Consumer;

public enum Event {
    START_APP(null),
    LOG(LogPayload.class),
    INIT_GUI(null),
    LOAD_SHADER_SOURCE(null),
    COMPILE_SHADERS(null),
    GENERATE_FRACTAL_DATA(null),
    UPLOAD_FRACTAL_DATA(null),
    REFRESH_DRAW_DATA(DrawDataPayload.class),
    INITIALIZE_DRAW_CYCLE(null),
    MARK_DRAW_DIRTY(null),
    UNLOAD_SHADER_SOURCE(null),
    CLEANUP_FRACTAL_DATA(null),
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

    public <T extends Record> Awaitable publish(T payload) {
        return BUS.publish(this, payload);
    }

    public Awaitable publish() {
        return BUS.publish(this, null);
    }

    public static void shutdown() {
        BUS.shutdown();
    }
}