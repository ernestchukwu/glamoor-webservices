package uk.co.glamoor.bookings.mapper;

import uk.co.glamoor.bookings.dto.response.StylistResponse;
import uk.co.glamoor.bookings.model.*;

public class StylistMapper {

    public static Stylist toStylist(StylistResponse stylistResponse) {
        Stylist stylist = new Stylist();

        stylist.setId(stylistResponse.getId());
        stylist.setFirstName(stylistResponse.getFirstName());
        stylist.setLastName(stylistResponse.getLastName());
        stylist.setAlias(stylistResponse.getAlias());
        stylist.setBrand(stylistResponse.getBrand());
        stylist.setLogo(stylistResponse.getLogo());
        stylist.setEmail(stylistResponse.getEmail());
        stylist.setAddress(toAddress(stylistResponse.getAddress()));
        stylist.setLocation(toLocation(stylistResponse.getLocation()));
        stylist.setTimeZone(stylistResponse.getTimeZone());
        stylist.setPhone(toPhone(stylistResponse.getPhone()));
        stylist.setCurrency(toCurrency(stylistResponse.getCurrency()));
        stylist.setVat(stylistResponse.getVat());
        stylist.setMinAdvanceBookingTimeMinutes(stylistResponse.getMinAdvanceBookingTimeMinutes());
        stylist.setBanner(stylistResponse.getBanner());

        return stylist;
    }

    private static Address toAddress(StylistResponse.Address stylistResponseAddress) {
        Address address = new Address();

        address.setAddress1(stylistResponseAddress.getAddress1());
        address.setAddress2(stylistResponseAddress.getAddress2());
        address.setPostcode(stylistResponseAddress.getPostcode());
        address.setCity(toCity(stylistResponseAddress.getCity()));

        return address;
    }

    public static City toCity(StylistResponse.City stylistResponseCity) {
        City city = new City();

        city.setId(stylistResponseCity.getId());
        city.setName(stylistResponseCity.getName());

        return city;
    }

    private static Location toLocation(StylistResponse.Location stylistResponseLocation) {
        Location location = new Location();

        location.setType(stylistResponseLocation.getType());
        location.setCoordinates(stylistResponseLocation.getCoordinates());

        return location;
    }

    public static Phone toPhone(StylistResponse.Phone stylistResponsePhone) {
        Phone phone = new Phone();

        phone.setNumber(stylistResponsePhone.getNumber());
        phone.setCountryCode(stylistResponsePhone.getCountryCode());
        phone.setCountryISOCode(stylistResponsePhone.getCountryISOCode());

        return phone;
    }

    private static Currency toCurrency(StylistResponse.Currency stylistResponseCurrency) {
        Currency currency = new Currency();

        currency.setName(stylistResponseCurrency.getName());
        currency.setIso(stylistResponseCurrency.getIso());
        currency.setSymbol(stylistResponseCurrency.getSymbol());

        return currency;
    }

}
