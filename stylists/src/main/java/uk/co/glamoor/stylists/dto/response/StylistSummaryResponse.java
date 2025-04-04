package uk.co.glamoor.stylists.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class StylistSummaryResponse {

    private String id;
    private String banner;
    private String logo;
    private Double rating;
    private String name;
    private Boolean liked;
    private Boolean verified;
    private Location location;
    private String locality;
    private Boolean offersHomeService;
    List<String> serviceCategoryIds;

    @Data
    public static class Location {
        private Double longitude;
        private Double latitude;
    }
}
