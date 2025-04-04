package uk.co.glamoor.gateway.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import uk.co.glamoor.gateway.model.AppSettingsStatus;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "client")
@Data
@Getter
@Setter
@RefreshScope
public class ClientConfig {

    private List<ClientSettings> android;
    private List<ClientSettings> ios;
    private List<ClientSettings> web;

    @Data
    @Getter
    @Setter
    public static class ClientSettings {
        private String version;
        private int settingsVersion;
        private boolean mandatoryUpdate;
        private String latestVersion;
        private double defaultMapZoom;
        private SignInOptions signInOptions;
    }

    @Data
    @Getter
    @Setter
    public static class SignInOptions {
        private boolean google;
        private boolean facebook;
        private boolean apple;
        private boolean emailAndPassword;
    }

    public Mono<AppSettingsStatus> getAppSettingsStatus(String platform, String version) {

        return Mono.fromCallable(() -> {
            ClientSettings clientSettings = getClientConfig(platform, version);

            AppSettingsStatus appSettingsStatus = new AppSettingsStatus();

            appSettingsStatus.setSettingsVersion(clientSettings.getSettingsVersion());
            appSettingsStatus.setVersion(clientSettings.getVersion());
            appSettingsStatus.setMandatoryUpdate(clientSettings.isMandatoryUpdate());
            appSettingsStatus.setLatestVersion(clientSettings.getLatestVersion());

            return appSettingsStatus;
        });

    }

    public ClientSettings getClientConfig(String platform, String version) {

        List<ClientSettings> clientSettings = switch (platform) {
            case "android" -> android;
            case "ios" -> ios;
            case "web" -> web;
            default -> null;
        };

        if (clientSettings == null) throw new RuntimeException("Settings for version '"+
                version+", " + platform +"' not found.");

        for (ClientSettings clientSettings1 : clientSettings) {
            if (clientSettings1.getVersion().equals(version)) {
                return clientSettings1;
            }
        }
        throw new RuntimeException("Settings for version '"+
                version+", " + platform +"' not found.");
    }
}
