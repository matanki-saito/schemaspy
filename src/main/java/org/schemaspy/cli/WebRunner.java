package org.schemaspy.cli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebRunner implements AsyncTaskRunner {
    private final SchemaSpyRunner schemaSpyRunner;

    @Async
    @Override
    public CompletableFuture<String> asyncRun(String... args) {
        schemaSpyRunner.run(args);
        return CompletableFuture.completedFuture("success");
    }
}
