package org.schemaspy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.schemaspy.cli.AsyncTaskRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SimpleController {
    private final AsyncTaskRunner schemaSpyRunner;

    private CompletableFuture<String> currentFeature;

    private final ResourceLoader resourceLoader;

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

    // https://stackoverflow.com/questions/35680932/download-a-file-from-spring-boot-rest-service
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam(value = "type", required = false, defaultValue = "mysql") String databaseType,
                                             @RequestParam(value = "host") String host,
                                             @RequestParam(value = "database") String databaseName,
                                             @RequestParam(value = "user") String userName,
                                             @RequestParam(value = "pass") String password,
                                             @RequestParam(value = "timezone", required = false, defaultValue = "JST") String timezone)
            throws IOException {

        if (currentFeature != null && !currentFeature.isDone()) {
            log.info("working");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var tmpDirPath = Files.createTempDirectory("homu").toFile();

        final String[] args = {
                "-t", databaseType,
                "-host", host,
                "-db", databaseName,
                "-u", userName,
                "-p", password,
                "-o", tmpDirPath.getPath(),
                "-dp", "ext-lib/mysql-connector-java-8.0.17.jar",
                "-connprops", String.format("serverTimezone\\=%s", timezone),
                "-s", databaseName
        };

        currentFeature = schemaSpyRunner.asyncRun(args);
        currentFeature.join();

        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                String.format(
                        "attachment; filename*=utf-8''%s",
                        URLEncoder.encode("schemaspy.zip", StandardCharsets.UTF_8)
                )
        );

        var zipFile = Files.createTempFile("homu", "geso").toFile();
        ZipUtil.pack(tmpDirPath, zipFile);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
