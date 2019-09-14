package org.schemaspy.cli;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface AsyncTaskRunner {
    @Async
    CompletableFuture<String> asyncRun(String... args);
}
