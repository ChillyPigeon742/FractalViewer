package net.alek.fractalviewer.transfer.request.type;

import net.alek.fractalviewer.data.asset.Shaders;
import net.alek.fractalviewer.data.model.AppData;
import net.alek.fractalviewer.transfer.request.RequestBus;
import net.alek.fractalviewer.transfer.request.payload.FractalDataPayload;
import net.alek.fractalviewer.transfer.request.payload.ShaderProgramPayload;
import net.alek.fractalviewer.transfer.request.payload.WindowDataPayload;

import java.util.concurrent.CompletableFuture;

public enum Request {
    GET_APPDATA(AppData.class),
    GET_WINDOW_DATA(WindowDataPayload.class),
    GET_SHADER_SOURCE(Shaders.class),
    GET_SHADER_PROGRAM(ShaderProgramPayload.class),
    GET_FRACTAL_DATA(FractalDataPayload.class);

    private final Class<? extends Record> responseClass;
    private static final RequestBus BUS = new RequestBus();

    Request(Class<? extends Record> responseClass) {
        if (!responseClass.isRecord()) {
            throw new IllegalArgumentException("Response class must be a record: " + responseClass.getName());
        }
        this.responseClass = responseClass;
    }

    public void handle(java.util.function.Supplier<? extends Record> handler) {
        BUS.handle(this, handler);
    }

    public <R extends Record> RequestFuture<R> request() {
        return request(0, 0);
    }

    public <R extends Record> RequestFuture<R> request(int retries, int timeoutSeconds) {
        CompletableFuture<?> future = BUS.requestAsync(this, retries, timeoutSeconds);
        return new RequestFuture<>((CompletableFuture<R>) future);
    }

    public Class<? extends Record> getResponseClass() {
        return responseClass;
    }

    public static void shutdown() {
        BUS.shutdown();
    }
}