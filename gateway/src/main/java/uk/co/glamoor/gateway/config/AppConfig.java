package uk.co.glamoor.gateway.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app")
@Data
@RefreshScope
public class AppConfig {

    private String appName;
    private String appUrl;

    private Images images;
    private String supportEmailAddress;
    private boolean showPostLikeCount;
    private int defaultMaxDistance;
    private double defaultLongitude;
    private double defaultLatitude;
    private int futureBookingDaysExtent;
    private Map<String, String> deleteAccountReasons;
    private String staticResourcesPath;
    private int timeSlotReservationDurationSeconds;
    private int timeSlotHeartbeatCadenceSeconds;

    @Data
    public static class Images {
        private String defaultFormat;
        private Directories directories;
    }

    @Data
    public static class Directories {

        private String posts;
        private String gallery;
        private String bookingMessages;
        private String serviceCategories;
        private String serviceSpecification;
        private String stylistBanners;
        private String stylistProfilePictures;
        private String userProfilePictures;
        private String reviewPictures;
        private String providerProfilePictures;

    }

}
