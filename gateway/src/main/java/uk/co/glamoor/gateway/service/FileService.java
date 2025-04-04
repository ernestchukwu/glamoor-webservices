package uk.co.glamoor.gateway.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FileService {

    @Value("${static.images.path:/app/static-resources/files/}")
    private String filesPath;

    public Mono<String> getTermsOfUse() {
        return readFile(filesPath + "terms-of-use.html");
    }

    public Mono<String> getPrivacyPolicy() {
        return readFile(filesPath + "privacy-policy.html");
    }

    private Mono<String> readFile(String path) {
        return Mono.fromCallable(() -> {
                    Path filePath = Paths.get(path);
                    return Files.readString(filePath);
                })
                .onErrorMap(e -> new RuntimeException("Failed to read the file: " + path, e));
    }
}
