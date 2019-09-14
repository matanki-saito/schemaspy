package org.schemaspy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schemaspy.cli.AsyncTaskRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SimpleController {
    private final AsyncTaskRunner schemaSpyRunner;

    private CompletableFuture<String> currentFeature;

    @GetMapping("/run")
    public void run(
            @RequestParam(value = "type", required = false, defaultValue = "mysql") String databaseType,
            @RequestParam(value = "host") String host,
            @RequestParam(value = "database") String databaseName,
            @RequestParam(value = "user") String userName,
            @RequestParam(value = "pass") String password,
            @RequestParam(value = "timezone", required = false, defaultValue = "JST") String timezone,
            @RequestParam(value = "out", required = false, defaultValue = "/output") String outputPath
    ) {
        final String[] args = {
                "-t", databaseType,
                "-host", host,
                "-db", databaseName,
                "-u", userName,
                "-p", password,
                "-o", outputPath,
                "-dp", "ext-lib/mysql-connector-java-8.0.17.jar",
                "-connprops", String.format("serverTimezone\\=%s", timezone),
                "-s", databaseName
        };

        if (currentFeature == null || currentFeature.isDone()) {
            currentFeature = schemaSpyRunner.asyncRun(args);
        } else {
            log.info("working");
        }
    }

    @GetMapping("/runnable")
    public boolean check() {
        return currentFeature == null || currentFeature.isDone();
    }
}
