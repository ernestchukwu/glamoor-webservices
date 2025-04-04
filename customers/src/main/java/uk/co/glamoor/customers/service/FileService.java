package uk.co.glamoor.customers.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileService {

    @Value("${app.staticResourcesPath:/app/static-resources/}")
    private String staticResourcePath;

    public Mono<String> saveStaticFile(FilePart file, String fileName, String path) {
            return Mono.fromCallable(() -> {
                Path directoryPath = Paths.get(staticResourcePath, path);
                System.out.println(directoryPath);
                try {
                    if (!Files.exists(directoryPath)) {
                        Files.createDirectories(directoryPath);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error saving file", e);
                }


                file.transferTo(Paths.get(directoryPath.toString(), fileName)).block();
                return fileName;
            })
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnError(e -> System.err.println("Error saving file: " + e.getMessage()));
    }


    public Mono<Void> deleteExistingProfilePicture(String customerId, String directory) {


        return Mono.fromCallable(() -> {
                    Path dirPath = Paths.get(staticResourcePath, directory);

                    if (!Files.exists(dirPath)) {
                        return List.<Path>of();
                    }

                    try (Stream<Path> files = Files.walk(dirPath)) {
                        return files
                                .filter(p -> p.getFileName().toString().startsWith(customerId + "_"))
                                .toList();
                    } catch (IOException e) {
                        throw new RuntimeException("Error walking directory", e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .flatMap(file -> Mono.fromRunnable(() -> {
                    System.out.println("got here");
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        throw new RuntimeException("Error deleting file: " + file, e);
                    }
                }))
                .then();
    }



    public String getFileExtension(FilePart file) {
        String originalFilename = file.filename();
        if (!originalFilename.contains(".")) {
            throw new IllegalArgumentException("Invalid file: No extension found");
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
