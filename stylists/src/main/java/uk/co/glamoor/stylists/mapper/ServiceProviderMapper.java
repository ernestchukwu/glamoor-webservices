package uk.co.glamoor.stylists.mapper;

import uk.co.glamoor.stylists.dto.response.bookings.BookingsServiceProviderResponse;
import uk.co.glamoor.stylists.model.ServiceProvider;

public class ServiceProviderMapper {

    public static BookingsServiceProviderResponse toBookingsServiceServiceProviderResponse(ServiceProvider serviceProvider) {

        BookingsServiceProviderResponse bookingsServiceProviderResponse = new BookingsServiceProviderResponse();

        bookingsServiceProviderResponse.setId(serviceProvider.getId());
        bookingsServiceProviderResponse.setStylistId(serviceProvider.getStylistId());
        bookingsServiceProviderResponse.setDoesHomeService(serviceProvider.getDoesHomeService());
        bookingsServiceProviderResponse.setFirstName(serviceProvider.getFirstName());
        bookingsServiceProviderResponse.setLastName(serviceProvider.getLastName());
        bookingsServiceProviderResponse.setEmail(serviceProvider.getEmail());
        bookingsServiceProviderResponse.setPhone(StylistMapper.toBookingsServiceStylistResponsePhone(
                serviceProvider.getPhone()));
        bookingsServiceProviderResponse.setPhoto(serviceProvider.getPhoto());
        bookingsServiceProviderResponse.setServices(serviceProvider.getServices());

        return bookingsServiceProviderResponse;
    }
}
