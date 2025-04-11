package uk.co.glamoor.bookings.mapper;

import uk.co.glamoor.bookings.dto.messaging.BookingMessage;
import uk.co.glamoor.bookings.dto.request.BookingRequest;
import uk.co.glamoor.bookings.dto.response.BookingDetailedResponse;
import uk.co.glamoor.bookings.dto.response.BookingSummaryResponse;
import uk.co.glamoor.bookings.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
	
	public static BookingMessage toBookingMessage(Booking booking) {

		if (booking == null) return null;
    	
		BookingMessage message = new BookingMessage();
		
		message.setBookingId(booking.getId());
        message.setCustomer(toUser(booking.getCustomer()));
        message.setStylist(toUser(booking.getStylist()));
        message.setServiceProvider(toUser(booking.getServiceProvider()));
        message.setTime(booking.getTime());
        message.setTimeZone(booking.getTimeZone());
        message.setReferenceNumber(booking.getBookingReference());
        message.setAddress(booking.getAddressString());
        message.setCancellationPolicy(booking.getCancellationPolicy());

        for (Booking.StylistServiceSpecification specification : booking.getServiceSpecifications()) {
            BookingMessage.StylistServiceSpecification spec = new BookingMessage.StylistServiceSpecification();

            spec.setService(specification.getService().getName() + " - " + specification.getOption().getDescription());
            spec.setDuration(specification.getOption().getDurationMinutes());
            spec.setPrice(specification.getOption().getPrice());

            for (Booking.AddonSpecification addonSpecification : specification.getAddonSpecifications()) {
                BookingMessage.StylistServiceSpecification addonSpec = new BookingMessage.StylistServiceSpecification();
                spec.setService(addonSpecification.getAddon().getName() + " - " + addonSpecification.getOption().getDescription());
                spec.setDuration(addonSpecification.getOption().getDurationMinutes());
                spec.setPrice(addonSpecification.getOption().getPrice());
                spec.getAddons().add(addonSpec);
            }

            message.getStylistServiceSpecifications().add(spec);
        }


        return message;
	}

    public static BookingDetailedResponse toBookingDetailedResponse(Booking booking) {
        BookingDetailedResponse response = new BookingDetailedResponse();

        response.setId(booking.getId());
        response.setStylistBanner(booking.getStylist().getBanner());
        response.setStylistId(booking.getStylist().getId());
        response.setStylistName(booking.getStylist().getFirstName() + " " + booking.getStylist().getLastName());
        response.setStylistLogo(booking.getStylist().getLogo());
        response.setServiceProviderName(booking.getServiceProvider().getFirstName() + " " + booking.getServiceProvider().getLastName());
        response.setServiceProviderProfilePicture(booking.getServiceProvider().getProfilePicture());

        response.setUnreadMessagesCount(booking.getCustomerUnreadMessagesCount());
        response.setIsoTime(booking.getTime() != null ? booking.getTime().toString() : null);
        response.setStatus(booking.getStatus());
        response.setServiceSpecifications(
                booking.getServiceSpecifications().stream()
                        .map(BookingMapper::convertToServiceSpecificationResponse)
                        .collect(Collectors.toList())
        );
        response.setBookingReference(booking.getBookingReference());
        response.setAddress(booking.getAddress());
        response.setHomeService(booking.isHomeService());
        response.setHasBeenReviewed(booking.isHasBeenReviewed());
        response.setPaymentStatus(booking.getPaymentStatus());
        response.setCurrency(booking.getCurrency() != null ? booking.getCurrency().getSymbol() : null);
        if (booking.getDiscount() != null) {
            response.setDiscountAmount(booking.getDiscount().getAmount());
            response.setDiscountType(booking.getDiscount().getDiscountType().toString());
        }
        response.setCancellationPolicy(booking.getCancellationPolicy());
        response.setNotes(booking.getNotes());
        response.setLocation(booking.getLocation().getCoordinates());
        response.setHomeServiceSpecification(
                booking.getHomeServiceSpecification() != null ?
                        convertToHomeServiceSpecificationResponse(booking.getHomeServiceSpecification()) : null
        );

        return response;
    }

    private static BookingDetailedResponse.ServiceSpecification convertToServiceSpecificationResponse(Booking.StylistServiceSpecification serviceSpec) {
        BookingDetailedResponse.ServiceSpecification response = new BookingDetailedResponse.ServiceSpecification();
        response.setName(serviceSpec.getService().getName());
        response.setPrice(serviceSpec.getOption().getPrice());
        response.setDuration(serviceSpec.getOption().getDurationMinutes());
        response.setImage(serviceSpec.getImage());
        return response;
    }

    private static BookingDetailedResponse.HomeServiceSpecification convertToHomeServiceSpecificationResponse(HomeServiceSpecification homeServiceSpec) {
        BookingDetailedResponse.HomeServiceSpecification response = new BookingDetailedResponse.HomeServiceSpecification();
        response.setCity(homeServiceSpec.getCity().getName());
        response.setPrice(homeServiceSpec.getServiceCharge());
        return response;
    }

    private static BookingMessage.User toUser(Booking.Stylist stylist) {
        BookingMessage.User user = new BookingMessage.User();

        user.setId(stylist.getId());
        user.setFirstName(stylist.getFirstName());
        user.setLastName(stylist.getLastName());
        user.setEmail(stylist.getEmail());
        user.setPhone(stylist.getPhone().getFullNumber());

        return user;
    }

    private static BookingMessage.User toUser(Booking.Customer customer) {
        BookingMessage.User user = new BookingMessage.User();

        user.setId(customer.getId());
        user.setFirstName(customer.getFirstName());
        user.setLastName(customer.getLastName());
        user.setEmail(customer.getEmail());
        user.setPhone(customer.getPhone().getFullNumber());

        return user;
    }

    private static BookingMessage.User toUser(Booking.ServiceProvider serviceProvider) {
        BookingMessage.User user = new BookingMessage.User();

        user.setId(serviceProvider.getId());
        user.setFirstName(serviceProvider.getFirstName());
        user.setLastName(serviceProvider.getLastName());
        user.setEmail(serviceProvider.getEmail());
        user.setPhone(serviceProvider.getPhone().getFullNumber());

        return user;
    }

    public static Address toAddress(BookingRequest.Address bookingRequestAddress) {
        Address address = new Address();
        address.setAddress1(bookingRequestAddress.getAddress1());
        address.setAddress2(bookingRequestAddress.getAddress2());
        address.setPostcode(bookingRequestAddress.getPostcode());
        return address;
    }

    public static Booking.Customer toBookingCustomer(Customer customer) {
        Booking.Customer bookingCustomer = new Booking.Customer();

        bookingCustomer.setId(customer.getId());
        bookingCustomer.setFirstName(customer.getFirstName());
        bookingCustomer.setLastName(customer.getLastName());
        bookingCustomer.setEmail(customer.getEmail());
        bookingCustomer.setPhone(customer.getPhone());
        bookingCustomer.setProfilePicture(customer.getProfilePicture());
        bookingCustomer.setPaymentCustomerId(customer.getPaymentCustomerId());

        return bookingCustomer;
    }

    public static Booking.Stylist toBookingStylist(Stylist stylist) {
        Booking.Stylist bookingStylist = new Booking.Stylist();

        bookingStylist.setId(stylist.getId());
        bookingStylist.setFirstName(stylist.getFirstName());
        bookingStylist.setLastName(stylist.getLastName());
        bookingStylist.setEmail(stylist.getEmail());
        bookingStylist.setPhone(stylist.getPhone());
        bookingStylist.setAlias(stylist.getAlias());
        bookingStylist.setBrand(stylist.getBrand());
        bookingStylist.setLogo(stylist.getLogo());
        bookingStylist.setBanner(stylist.getBanner());
        bookingStylist.setVat(stylist.getVat());

        return bookingStylist;
    }

    public static Location toLocation(List<Double> bookingRequestLocation) {
        Location location = new Location();
        location.setCoordinates(bookingRequestLocation);
        return location;
    }

    public static BookingSummaryResponse toBookingSummaryResponse(Booking booking) {
        BookingSummaryResponse response = new BookingSummaryResponse();
        response.setId(booking.getId());
        response.setStylistName(booking.getStylist().getDisplayName());
        response.setIsoTime(booking.getTime().toString());
        response.setStylistLogo(booking.getStylist().getLogo());
        response.setStatus(booking.getStatus());
        response.setUnreadMessagesCount(booking.getCustomerUnreadMessagesCount());
        response.setServiceNames(booking.getServiceSpecifications().stream()
                .map(spec -> spec.getService().getName())
                .collect(Collectors.toList()));
        return response;
    }
}
