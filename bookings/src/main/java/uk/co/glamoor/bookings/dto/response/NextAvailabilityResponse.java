package uk.co.glamoor.bookings.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class NextAvailabilityResponse {
    private LocalDate date;
    private List<MonthlyAvailabilityResponse> monthlyAvailabilities;
}
