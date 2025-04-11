package uk.co.glamoor.stylists.mapper;

import uk.co.glamoor.stylists.dto.AddonDTO;
import uk.co.glamoor.stylists.dto.AddonSpecificationDTO;
import uk.co.glamoor.stylists.dto.GlamoorServiceDTO;
import uk.co.glamoor.stylists.dto.StylistServiceSpecificationDTO;
import uk.co.glamoor.stylists.dto.response.bookings.BookingsServiceStylistServiceSpecificationResponse;
import uk.co.glamoor.stylists.model.*;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ServiceSpecificationMapper {

    public static StylistServiceSpecificationDTO toDto(
            StylistServiceSpecification specification, MapperType mapperType) {

        StylistServiceSpecificationDTO dto = new StylistServiceSpecificationDTO();

        dto.setId(specification.getId());
        dto.setService(toDto(specification.getService(), mapperType));

        if (mapperType == MapperType.MIN) {
            dto.getOptions().add(specification.getOptions().stream().sorted(
                    Comparator.comparing(ServiceSpecification.ServiceSpecificationOption::getPrice))
                    .toList().get(0));

            return dto;
        }

        dto.setOptions(specification.getOptions());
        dto.setImage(specification.getImage());
        dto.setHomeServiceAvailable(specification.getHomeServiceAvailable());
        dto.setHomeServiceAdditionalPrice(specification.getHomeServiceAdditionalPrice());
        dto.setDepositPaymentPercent(specification.getDepositPaymentPercent());

        if (mapperType == MapperType.MEDIUM) {
            return dto;
        }

        dto.setNote(specification.getNote());
        dto.setTerms(specification.getTerms());
        dto.setAddonSpecifications(specification.getAddonSpecifications()
                .stream().map(ServiceSpecificationMapper::toDto).collect(Collectors.toList()));
        dto.setHomeServicePrice(specification.getHomeServiceAdditionalPrice());

        return dto;
    }

    public static AddonSpecificationDTO toDto(AddonSpecification specification) {
        AddonSpecificationDTO dto = new AddonSpecificationDTO();

        dto.setId(specification.getId());
        dto.setImage(specification.getImage());
        dto.setNote(specification.getNote());
        dto.setHomeServiceAvailable(specification.getHomeServiceAvailable());
        dto.setHomeServiceAdditionalPrice(specification.getHomeServiceAdditionalPrice());
        dto.setOptions(specification.getOptions());
        dto.setTerms(specification.getTerms());
        dto.setAddon(toDTO(specification.getAddon()));

        return dto;
    }

    private static AddonDTO toDTO(Addon addon) {
        AddonDTO dto = new AddonDTO();

        dto.setId(addon.getId());
        dto.setName(addon.getName());
        dto.setDescription(addon.getDescription());

        return dto;
    }

    private static GlamoorServiceDTO toDto(GlamoorService service, MapperType mapperType) {
        GlamoorServiceDTO dto = new GlamoorServiceDTO();

        dto.setId(service.getId());
        dto.setName(service.getName());

        if (mapperType == MapperType.MIN || mapperType == MapperType.MEDIUM) return dto;

        dto.setCategoryId(service.getCategoryId());
        dto.setDescription(service.getDescription());

        return dto;
    }



    public static BookingsServiceStylistServiceSpecificationResponse toBookingsServiceStylistServiceSpecificationResponse(
            StylistServiceSpecification stylistServiceSpecification) {

        BookingsServiceStylistServiceSpecificationResponse bookingsStylistServiceSpecification = new BookingsServiceStylistServiceSpecificationResponse();

        bookingsStylistServiceSpecification.setId(stylistServiceSpecification.getId());
        bookingsStylistServiceSpecification.setService(toBookingsService(stylistServiceSpecification.getService()));
        bookingsStylistServiceSpecification.setMinAdvanceBookingTimeMinutes(stylistServiceSpecification.getMinAdvanceBookingTimeMinutes());
        bookingsStylistServiceSpecification.setAddonSpecifications(stylistServiceSpecification.getAddonSpecifications()
                .stream().map(ServiceSpecificationMapper::toAddonSpecification).toList());
        bookingsStylistServiceSpecification.setHomeServiceAvailable(stylistServiceSpecification.getHomeServiceAvailable());
        bookingsStylistServiceSpecification.setDescription(stylistServiceSpecification.getDescription());
        bookingsStylistServiceSpecification.setDepositPaymentPercent(stylistServiceSpecification.getDepositPaymentPercent());
        bookingsStylistServiceSpecification.setHomeServiceAdditionalPrice(stylistServiceSpecification.getHomeServiceAdditionalPrice());
        bookingsStylistServiceSpecification.setImage(stylistServiceSpecification.getImage());
        bookingsStylistServiceSpecification.setNote(stylistServiceSpecification.getNote());
        bookingsStylistServiceSpecification.setOptions(stylistServiceSpecification.getOptions().stream()
                .map(ServiceSpecificationMapper::toBookingsServiceSpecificationOption).toList());
        bookingsStylistServiceSpecification.setTerms(stylistServiceSpecification.getTerms());

        return bookingsStylistServiceSpecification;
    }

    private static BookingsServiceStylistServiceSpecificationResponse.GlamoorService toBookingsService(
            GlamoorService glamoorService) {

        BookingsServiceStylistServiceSpecificationResponse.GlamoorService bookingsService =
                new BookingsServiceStylistServiceSpecificationResponse.GlamoorService();

        bookingsService.setId(glamoorService.getId());
        bookingsService.setName(glamoorService.getName());
        bookingsService.setDescription(glamoorService.getDescription());
        bookingsService.setCategoryId(glamoorService.getCategoryId());

        return bookingsService;
    }

    private static BookingsServiceStylistServiceSpecificationResponse.AddonSpecification toAddonSpecification(
            AddonSpecification addonSpecification) {

        BookingsServiceStylistServiceSpecificationResponse.AddonSpecification bookingsAddonSpecification =
                new BookingsServiceStylistServiceSpecificationResponse.AddonSpecification();

        bookingsAddonSpecification.setId(addonSpecification.getId());
        bookingsAddonSpecification.setAddon(toBookingsAddon(addonSpecification.getAddon()));
        bookingsAddonSpecification.setNote(addonSpecification.getNote());
        bookingsAddonSpecification.setImage(addonSpecification.getImage());
        bookingsAddonSpecification.setTerms(addonSpecification.getTerms());
        bookingsAddonSpecification.setDepositPaymentPercent(addonSpecification.getDepositPaymentPercent());
        bookingsAddonSpecification.setDescription(addonSpecification.getDescription());
        bookingsAddonSpecification.setHomeServiceAdditionalPrice(addonSpecification.getHomeServiceAdditionalPrice());
        bookingsAddonSpecification.setHomeServiceAvailable(addonSpecification.getHomeServiceAvailable());
        bookingsAddonSpecification.setOptions(addonSpecification.getOptions()
                .stream().map(ServiceSpecificationMapper::toBookingsServiceSpecificationOption).toList());

        return bookingsAddonSpecification;
    }

    private static BookingsServiceStylistServiceSpecificationResponse.Addon toBookingsAddon(
            Addon addon) {

        BookingsServiceStylistServiceSpecificationResponse.Addon bookingsAddon =
                new BookingsServiceStylistServiceSpecificationResponse.Addon();

        bookingsAddon.setId(addon.getId());
        bookingsAddon.setName(addon.getName());
        bookingsAddon.setDescription(addon.getDescription());

        return bookingsAddon;
    }

    private static BookingsServiceStylistServiceSpecificationResponse.ServiceSpecificationOption toBookingsServiceSpecificationOption(
            StylistServiceSpecification.ServiceSpecificationOption serviceSpecificationOption) {

        BookingsServiceStylistServiceSpecificationResponse.ServiceSpecificationOption bookingsServiceSpecificationOption =
                new BookingsServiceStylistServiceSpecificationResponse.ServiceSpecificationOption();

        bookingsServiceSpecificationOption.setId(serviceSpecificationOption.getId());
        bookingsServiceSpecificationOption.setPrice(serviceSpecificationOption.getPrice());
        bookingsServiceSpecificationOption.setDescription(serviceSpecificationOption.getDescription());
        bookingsServiceSpecificationOption.setDurationMinutes(serviceSpecificationOption.getDurationMinutes());

        return bookingsServiceSpecificationOption;
    }

}
