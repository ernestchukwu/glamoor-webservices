package uk.co.glamoor.gateway.model;

import lombok.Data;

@Data
public class BookingsSettings {

    private int bookingsRequestBatchSize;
    private int messagesRequestBatchSize;
}
