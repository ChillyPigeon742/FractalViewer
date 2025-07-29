package net.alek.fractalviewer.transfer.request.type;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class RequestFuture<R extends Record> {
    private final CompletableFuture<R> future;

    public RequestFuture(CompletableFuture<R> future) {
        this.future = future;
    }

    public <T extends R> RequestFuture<R> then(Consumer<T> onSuccess) {
        CompletableFuture<R> newFuture = future.thenApply(res -> {
            onSuccess.accept((T) res);
            return res;
        });
        return new RequestFuture<>(newFuture);
    }

    public RequestFuture<R> exceptionally(Consumer<Throwable> onError) {
        CompletableFuture<R> newFuture = future.exceptionally(ex -> {
            onError.accept(ex);
            return null;
        });
        return new RequestFuture<>(newFuture);
    }

    public R get() {
        try {
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            if (cause instanceof Error) throw (Error) cause;
            throw new RuntimeException(cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request interrupted", e);
        }
    }

    public RequestSync<R> await() {
        try {
            R res = future.get();
            return new RequestSync<>(res, null);
        } catch (ExecutionException e) {
            return new RequestSync<>(null, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new RequestSync<>(null, e);
        }
    }
}