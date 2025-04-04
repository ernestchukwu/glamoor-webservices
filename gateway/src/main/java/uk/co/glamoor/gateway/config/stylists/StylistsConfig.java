package uk.co.glamoor.gateway.config.stylists;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "stylists-app")
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
