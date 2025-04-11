package uk.co.glamoor.stylists.mapper;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import uk.co.glamoor.stylists.dto.StylistDTO;
import uk.co.glamoor.stylists.dto.response.StylistSummaryResponse;
import uk.co.glamoor.stylists.model.*;
import uk.co.glamoor.stylists.dto.response.bookings.BookingsServiceStylistResponse;

public class StylistMapper {

	public static StylistSummaryResponse toStylistSummaryResponse(Stylist stylist) {
		StylistSummaryResponse response = new StylistSummaryResponse();

		response.setId(stylist.getId());
		response.setBanner(stylist.getBanner());
		response.setLogo(stylist.getLogo());
		response.setRating(stylist.getRating());
		response.setVerified(stylist.getVerified());
		response.setName(stylist.getDisplayName());
		response.setLiked(stylist.getFavourite());
		response.setLocality(stylist.getLocality());
		response.setLocation(toStylistSummaryResponseLocation(stylist.getLocation()));
		response.setOffersHomeService(!stylist.getHomeServiceSpecifications().isEmpty());
		response.setServiceCategoryIds(stylist.getServiceCategories());

		return response;
	}

	private static StylistSummaryResponse.Location toStylistSummaryResponseLocation(Location location) {
		StylistSummaryResponse.Location response = new StylistSummaryResponse.Location();

		response.setLatitude(location.getCoordinates().get(1));
		response.setLongitude(location.getCoordinates().get(0));

		return response;
	}

	public static Stylist toStylist(StylistDTO dto) {

		Stylist stylist = new Stylist();

		stylist.setId(dto.getId());
		stylist.setBrand(dto.getBrand());
		stylist.setFirstName(dto.getFirstName());
		stylist.setLastName(dto.getLastName());
		stylist.setLogo(dto.getLogo());
		stylist.setEmail(dto.getEmail());
		stylist.setUid(dto.getUid());
		stylist.setAccountProvider(dto.getAccountProvider());
		stylist.setEmailVerified(dto.getEmailVerified());
		stylist.setAlias(dto.getAlias());
		stylist.setLocality(dto.getLocality());
		stylist.setBusiness(dto.getBusiness());
		stylist.setPhone(dto.getPhone());
		stylist.setPhoneVerified(dto.getPhoneVerified());
		stylist.setRating(dto.getRating());
		stylist.setRatings(dto.getRatings());
		stylist.setTerms(dto.getTerms());
		stylist.setAbout(dto.getAbout());
		stylist.setVat(dto.getVat());
		stylist.setCurrency(dto.getCurrency());
		stylist.setHomeServiceSpecifications(dto.getHomeServiceSpecifications());
		stylist.setAddress(dto.getAddress());
		stylist.setLocation(dto.getLocation());
		stylist.setBanner(dto.getBanner());
		stylist.setServiceProviders(dto.getServiceProviders());
		stylist.setFavourite(dto.getFavourite());

		return stylist;
	}
	
	public static StylistDTO toDto(Stylist stylist, MapperType mapperType) {
		
		StylistDTO dto = new StylistDTO();
		
		dto.setId(stylist.getId());
		dto.setBrand(stylist.getBrand());
		dto.setFirstName(stylist.getFirstName());
		dto.setLastName(stylist.getLastName());
		dto.setBanner(stylist.getBanner());
		dto.setLogo(stylist.getLogo());
		dto.setAlias(stylist.getAlias());
		dto.setLocation(stylist.getLocation());
		dto.setLocality(stylist.getLocality());
		dto.setCurrency(stylist.getCurrency());
		dto.setBusiness(stylist.getBusiness());
		dto.setVerified(stylist.getVerified());
		dto.setVat(stylist.getVat());
		dto.setRating(stylist.getRating());
		dto.setRatings(stylist.getRatings());
		dto.setHomeServiceAvailable(!stylist.getHomeServiceSpecifications().isEmpty());
		dto.setFavourite(stylist.getFavourite());
		dto.setServiceCategories(stylist.getServiceCategories());

		if (mapperType == MapperType.MIN) {
			return dto;
		}

		dto.setServiceSpecifications(stylist.getServiceSpecifications()
				.stream().map(spec -> ServiceSpecificationMapper.toDto(spec, mapperType))
				.collect(Collectors.toList()));
		dto.setPhone(stylist.getPhone());
		dto.setEmail(stylist.getEmail());
		dto.setAbout(stylist.getAbout());
		dto.setAddress(stylist.getAddress());
		dto.setPhoneVerified(stylist.getPhoneVerified());
		dto.setEmailVerified(stylist.getEmailVerified());
		dto.setUid(stylist.getUid());
		dto.setAccountProvider(stylist.getAccountProvider());
		dto.setTerms(stylist.getTerms());
		dto.setServiceProviders(stylist.getServiceProviders());
		dto.setHomeServiceSpecifications(stylist.getHomeServiceSpecifications());
		
		return dto;
	}
	
	public static BookingsServiceStylistResponse toBookingsServiceStylist(Stylist stylist) {
		
	    BookingsServiceStylistResponse bookingsServiceStylistResponse = new BookingsServiceStylistResponse();
	    
	    bookingsServiceStylistResponse.setId(stylist.getId());
	    bookingsServiceStylistResponse.setFirstName(stylist.getFirstName());
		bookingsServiceStylistResponse.setLastName(stylist.getLastName());
	    bookingsServiceStylistResponse.setAlias(stylist.getAlias());
		bookingsServiceStylistResponse.setBrand(stylist.getBrand());
	    bookingsServiceStylistResponse.setLogo(stylist.getLogo());
	    bookingsServiceStylistResponse.setEmail(stylist.getEmail());
	    bookingsServiceStylistResponse.setAddress(toBookingsServiceStylistResponseAddress(stylist.getAddress()));
		bookingsServiceStylistResponse.setLocation(toBookingsServiceStylistResponseLocation(stylist.getLocation()));
		bookingsServiceStylistResponse.setTimeZone(stylist.getTimeZone());
	    bookingsServiceStylistResponse.setPhone(toBookingsServiceStylistResponsePhone(stylist.getPhone()));
	    bookingsServiceStylistResponse.setVat(stylist.getVat());
		bookingsServiceStylistResponse.setCurrency(toBookingsServiceStylistResponseCurrency(stylist.getCurrency()));
	    bookingsServiceStylistResponse.setCancellationPolicy(
	    		stylist.getCancellationPolicy());
		bookingsServiceStylistResponse.setMinAdvanceBookingTimeMinutes(
				stylist.getMinAdvanceBookingTimeMinutes());
		bookingsServiceStylistResponse.setBanner(stylist.getBanner());

	    return bookingsServiceStylistResponse;
	}

	private static BookingsServiceStylistResponse.Address toBookingsServiceStylistResponseAddress(Address address) {
		BookingsServiceStylistResponse.Address bookingsServiceAddress = new BookingsServiceStylistResponse.Address();

		bookingsServiceAddress.setAddress1(address.getAddress1());
		bookingsServiceAddress.setAddress2(address.getAddress2());
		bookingsServiceAddress.setPostcode(address.getPostcode());
		bookingsServiceAddress.setCity(toBookingsServiceStylistResponseCity(address.getCity()));

		return bookingsServiceAddress;
	}

	public static BookingsServiceStylistResponse.City toBookingsServiceStylistResponseCity(City city) {
		BookingsServiceStylistResponse.City bookingsServiceCity = new BookingsServiceStylistResponse.City();

		bookingsServiceCity.setId(city.getId());
		bookingsServiceCity.setName(city.getName());

		return bookingsServiceCity;
	}

	private static BookingsServiceStylistResponse.Location toBookingsServiceStylistResponseLocation(Location location) {
		BookingsServiceStylistResponse.Location bookingsServiceLocation = new BookingsServiceStylistResponse.Location();

		bookingsServiceLocation.setType(location.getType());
		bookingsServiceLocation.setCoordinates(location.getCoordinates());

		return bookingsServiceLocation;
	}

	public static BookingsServiceStylistResponse.Phone toBookingsServiceStylistResponsePhone(Phone phone) {
		BookingsServiceStylistResponse.Phone bookingsServicePhone = new BookingsServiceStylistResponse.Phone();

		bookingsServicePhone.setNumber(phone.getNumber());
		bookingsServicePhone.setCountryCode(phone.getCountryCode());
		bookingsServicePhone.setCountryISOCode(phone.getCountryISOCode());

		return bookingsServicePhone;
	}

	private static BookingsServiceStylistResponse.Currency toBookingsServiceStylistResponseCurrency(Currency currency) {
		BookingsServiceStylistResponse.Currency bookingsServiceCurrency = new BookingsServiceStylistResponse.Currency();

		bookingsServiceCurrency.setName(currency.getName());
		bookingsServiceCurrency.setIso(currency.getIso());
		bookingsServiceCurrency.setSymbol(currency.getSymbol());

		return bookingsServiceCurrency;
	}
	
	public static Stylist applyUpdates(Stylist stylist, Stylist update) {
        if (update.getBrand() != null) {
            stylist.setBrand(update.getBrand());
        }
        if (update.getFirstName() != null) {
            stylist.setFirstName(update.getFirstName());
        }
        if (update.getLastName() != null) {
            stylist.setLastName(update.getLastName());
        }
        if (update.getLogo() != null) {
            stylist.setLogo(update.getLogo());
        }
        if (update.getEmail() != null) {
            stylist.setEmail(update.getEmail());
        }
        if (update.getAlias() != null) {
            stylist.setAlias(update.getAlias());
        }
        if (update.getBusiness() != null) {
            stylist.setBusiness(update.getBusiness());
        }
        if (update.getPhone() != null) {
            stylist.setPhone(update.getPhone());
        }
        if (update.getAbout() != null) {
            stylist.setAbout(update.getAbout());
        }
        if (update.getVat() != null) {
            stylist.setVat(update.getVat());
        }
        if (update.getCurrency() != null) {
            stylist.setCurrency(update.getCurrency());
        }
        if (update.getAddress() != null) {
            stylist.setAddress(update.getAddress());
        }
        if (update.getLocation() != null) {
            stylist.setLocation(update.getLocation());
        }
        if (update.getBanner() != null) {
            stylist.setBanner(update.getBanner());
        }
        if (update.getCancellationPolicy() != null) {
            stylist.setCancellationPolicy(
            		update.getCancellationPolicy());
        }
		if (update.getMinAdvanceBookingTimeMinutes() != null) {
			stylist.setMinAdvanceBookingTimeMinutes(
					update.getMinAdvanceBookingTimeMinutes());
		}
        stylist.setTimeUpdated(LocalDateTime.now());
        
        return stylist;
    }
}
