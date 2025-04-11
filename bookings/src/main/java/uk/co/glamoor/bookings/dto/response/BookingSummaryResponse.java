package uk.co.glamoor.bookings.dto.response;

import lombok.Data;
import uk.co.glamoor.bookings.enums.BookingStatus;

import java.util.List;

@Data
public class BookingSummaryResponse {
    private String id;
    private String stylistName;
    private String isoTime;
    private String stylistLogo;
    private int unreadMessagesCount = 0;
    private BookingStatus status;
    private List<String> serviceNames;
}
