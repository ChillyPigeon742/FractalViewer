package net.alek.fractalviewer.transfer.request;

import net.alek.fractalviewer.transfer.request.type.Request;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class RequestBus {
    private final ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setName("RequestBus-Executor");
        return t;
    });

    private final Map<Request, Supplier<? extends Record>> handlers = new ConcurrentHashMap<>();

    public void handle(Request request, Supplier<? extends Record> handler) {
        handlers.put(request, handler);
    }

    public <R extends Record> CompletableFuture<R> requestAsync(Request request, int retries, int timeoutSeconds) {
        Supplier<? extends Record> rawHandler = handlers.get(request);
        if (rawHandler == null) {
            CompletableFuture<R> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("No handler registered for " + request));
            return failed;
        }
        Supplier<R> handler = (Supplier<R>) rawHandler;

        CompletableFuture<R> resultFuture = new CompletableFuture<>();
        doAttempt(request, handler, retries, timeoutSeconds, resultFuture);
        return resultFuture;
    }

    private <R extends Record> void doAttempt(Request request, Supplier<R> handler, int retriesLeft, int timeoutSeconds, CompletableFuture<R> resultFuture) {
        CompletableFuture<R> future = CompletableFuture.supplyAsync(() -> {
            R res = handler.get();

            if (!request.getResponseClass().isInstance(res)) {
                throw new CompletionException(new ClassCastException(
                        "Response for " + request + " must be of type " + request.getResponseClass().getName()));
            }

            return res;
        }, executor);

        if (timeoutSeconds > 0) {
            future = future.orTimeout(timeoutSeconds, TimeUnit.SECONDS);
        }

        future.whenComplete((res, err) -> {
            if (resultFuture.isDone()) return;

            if (err == null) {
                resultFuture.complete(res);
            } else {
                if (retriesLeft > 0) {
                    doAttempt(request, handler, retriesLeft - 1, timeoutSeconds, resultFuture);
                } else {
                    resultFuture.completeExceptionally(err);
                }
            }
        });
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}