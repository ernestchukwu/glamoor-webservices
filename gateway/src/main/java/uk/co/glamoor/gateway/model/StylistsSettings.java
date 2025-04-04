package uk.co.glamoor.gateway.model;

import lombok.Data;

@Data
public class StylistsSettings {

    private int stylistsRequestBatchSizeForHomeView;
    private int stylistsRequestBatchSize;
    private int serviceSpecificationRequestBatchSize;
    private int serviceSpecificationRequestBatchSizeMini;

    private int serviceRequestBatchSize;
    private int serviceCategoryRequestBatchSize;
    private int addonRequestBatchSize;
}
