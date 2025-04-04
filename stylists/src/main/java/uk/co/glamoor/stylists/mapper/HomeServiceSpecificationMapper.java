package uk.co.glamoor.stylists.mapper;

import uk.co.glamoor.stylists.dto.response.bookings.BookingsServiceHomeServiceSpecificationResponse;
import uk.co.glamoor.stylists.model.HomeServiceSpecification;

public class HomeServiceSpecificationMapper {

    public static BookingsServiceHomeServiceSpecificationResponse toBookingsServiceHomeServiceSpecificationResponse(
            HomeServiceSpecification homeServiceSpecification) {

        BookingsServiceHomeServiceSpecificationResponse bookingsHomeServiceSpecification = new BookingsServiceHomeServiceSpecificationResponse();

        bookingsHomeServiceSpecification.setId(homeServiceSpecification.getId());
        bookingsHomeServiceSpecification.setServiceCharge(homeServiceSpecification.getServiceCharge());
        bookingsHomeServiceSpecification.setServiceDuration(homeServiceSpecification.getServiceDuration());
        bookingsHomeServiceSpecification.setCity(StylistMapper.toBookingsServiceStylistResponseCity(
                homeServiceSpecification.getCity()));

        return bookingsHomeServiceSpecification;

    }
}
