package net.alek.fractalviewer.transfer.request.type;

import java.util.function.Consumer;

public class RequestSync<R extends Record> {
    private final R data;
    private final Throwable error;

    public RequestSync(R data, Throwable error) {
        this.data = data;
        this.error = error;
    }

    public boolean isSuccess() {
        return error == null;
    }

    public R get() {
        if (error != null) {
            if (error instanceof RuntimeException) throw (RuntimeException) error;
            if (error instanceof Error) throw (Error) error;
            throw new RuntimeException(error);
        }
        return data;
    }

    public Throwable getError() {
        return error;
    }

    public RequestSync<R> exceptionally(Consumer<Throwable> onError) {
        if (error != null) {
            onError.accept(error);
            return new RequestSync<>(data, null);
        }
        return this;
    }
}