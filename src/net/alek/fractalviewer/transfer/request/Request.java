package net.alek.fractalviewer.transfer.request;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public enum Request {
    GET_APPDATA(null);

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

    public static class RequestFuture<R extends Record> {
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

    public static class RequestSync<R extends Record> {
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
}