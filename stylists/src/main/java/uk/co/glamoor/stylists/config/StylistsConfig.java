package uk.co.glamoor.stylists.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stylists-app")
@Data
@RefreshScope
public class StylistsConfig {

    private int stylistsRequestBatchSizeForHomeView;
    private int stylistsRequestBatchSize;
    private int serviceSpecificationRequestBatchSize;
    private int serviceSpecificationRequestBatchSizeMini;

    private int serviceRequestBatchSize;
    private int serviceCategoryRequestBatchSize;
    private int addonRequestBatchSize;
}
