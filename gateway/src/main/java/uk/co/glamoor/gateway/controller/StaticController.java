package uk.co.glamoor.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import uk.co.glamoor.gateway.config.AppConfig;
import uk.co.glamoor.gateway.config.ClientConfig;
import uk.co.glamoor.gateway.config.bookings.BookingsConfig;
import uk.co.glamoor.gateway.config.stylists.StylistsConfig;
import uk.co.glamoor.gateway.model.AppSettings;
import uk.co.glamoor.gateway.model.AppSettingsStatus;
import uk.co.glamoor.gateway.service.FileService;

import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class StaticController {

	private final FileService fileService;
	private final ClientConfig clientConfig;
	private final AppConfig appConfig;
	private final BookingsConfig bookingsConfig;
	private final StylistsConfig stylistsConfig;

	@Value("${static.images.path:/app/static-resources/images/}")
	private String imagesPath;

	public StaticController(FileService fileService, ClientConfig clientConfig, AppConfig appConfig, BookingsConfig bookingsConfig, StylistsConfig stylistsConfig) {
        this.fileService = fileService;
        this.clientConfig = clientConfig;
        this.appConfig = appConfig;
        this.bookingsConfig = bookingsConfig;
        this.stylistsConfig = stylistsConfig;
    }

	@GetMapping("/settings")
	public Mono<ResponseEntity<AppSettings>> getSettings(
			@RequestParam String version,
			@RequestParam String platform) {

		return Mono.fromCallable(() -> {
					AppSettings appSettings = new AppSettings();
					appSettings.applyClientSettings(clientConfig.getClientConfig(platform, version));
					appSettings.applyAppConfig(appConfig);
					appSettings.applyStylistsConfig(stylistsConfig);
					appSettings.applyBookingsConfig(bookingsConfig);
					return appSettings;
				})
				.map(ResponseEntity::ok)
				.onErrorResume(e -> {
					return Mono.just(ResponseEntity.internalServerError().build());
				});
	}

	@GetMapping("/images/{category}/{fileName:.+}")
	public Mono<ResponseEntity<?>> getImage(
			@PathVariable String category,
			@PathVariable String fileName) {

		return Mono.fromCallable(() -> {
					Path filePath = Paths.get(imagesPath, category, fileName).normalize();
					Resource resource = new UrlResource(filePath.toUri());

					if (!resource.exists() || !resource.isReadable()) {
						return ResponseEntity.notFound().build();
					}

					String contentType = Files.probeContentType(filePath);
					if (contentType == null) {
						contentType = "application/octet-stream";
					}

					return ResponseEntity.ok()
							.contentType(MediaType.parseMediaType(contentType))
							.body(resource);
				})
				.onErrorResume(e -> {
					return Mono.just(ResponseEntity.status(500).build());
				});
	}
	
	@GetMapping("/settings/status")
	public Mono<ResponseEntity<AppSettingsStatus>> getSettingsStatus(
			@RequestParam String version,
			@RequestParam String platform) {
		
		return clientConfig.getAppSettingsStatus(platform, version)
				.map(ResponseEntity::ok);
	}

	@GetMapping("/terms-of-use")
	public Mono<ResponseEntity<String>> getTermsOfUse() {
		
		return fileService.getTermsOfUse()
				.map(ResponseEntity::ok);
	}
	
	@GetMapping("/privacy-policy")
	public Mono<ResponseEntity<String>> getPrivacyPolicy() {
		
		return fileService.getPrivacyPolicy()
				.map(ResponseEntity::ok);
	}
}
