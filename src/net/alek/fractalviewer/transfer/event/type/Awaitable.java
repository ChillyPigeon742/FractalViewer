package net.alek.fractalviewer.transfer.event.type;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Awaitable {
    private final List<CompletableFuture<Void>> futures;

    public Awaitable(List<CompletableFuture<Void>> futures) {
        this.futures = futures;
    }

    public void await() {
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}