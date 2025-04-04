package uk.co.glamoor.bookings.mapper;

import uk.co.glamoor.bookings.dto.response.ServiceProviderResponse;
import uk.co.glamoor.bookings.model.Booking;
import uk.co.glamoor.bookings.model.Phone;
import uk.co.glamoor.bookings.model.ServiceProvider;

import java.util.List;

public class ServiceProviderMapper {

    public static Booking.ServiceProvider toBookingServiceProvider(ServiceProvider serviceProvider) {
        Booking.ServiceProvider bookingServiceProvider = new Booking.ServiceProvider();

        bookingServiceProvider.setId(serviceProvider.getId());
        bookingServiceProvider.setEmail(serviceProvider.getEmail());
        bookingServiceProvider.setFirstName(serviceProvider.getFirstName());
        bookingServiceProvider.setLastName(serviceProvider.getLastName());
        bookingServiceProvider.setProfilePicture(serviceProvider.getPhoto());
        bookingServiceProvider.setPhone(serviceProvider.getPhone());

        return bookingServiceProvider;
    }

    public static ServiceProvider toServiceProvider(ServiceProviderResponse serviceProviderResponse) {
        ServiceProvider serviceProvider = new ServiceProvider();

        serviceProvider.setId(serviceProviderResponse.getId());
        serviceProvider.setStylistId(serviceProviderResponse.getStylistId());
        serviceProvider.setDoesHomeService(serviceProviderResponse.getDoesHomeService());
        serviceProvider.setFirstName(serviceProviderResponse.getFirstName());
        serviceProvider.setLastName(serviceProviderResponse.getLastName());
        serviceProvider.setEmail(serviceProviderResponse.getEmail());
        serviceProvider.setPhone(StylistMapper.toPhone(serviceProviderResponse.getPhone()));
        serviceProvider.setPhoto(serviceProviderResponse.getPhoto());
        serviceProvider.setServices(serviceProviderResponse.getServices());

        return serviceProvider;
    }

}
