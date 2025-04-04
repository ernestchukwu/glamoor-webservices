package uk.co.glamoor.bookings.mapper;

import uk.co.glamoor.bookings.dto.response.StylistServiceSpecificationResponse;
import uk.co.glamoor.bookings.model.*;

public class StylistServiceSpecificationMapper {

    public static Booking.ServiceSpecification.ServiceSpecificationOption toBookingServiceSpecificationOption(
            ServiceSpecification.ServiceSpecificationOption serviceSpecificationOption) {

        Booking.ServiceSpecification.ServiceSpecificationOption option = new Booking.ServiceSpecification.ServiceSpecificationOption();

        option.setId(serviceSpecificationOption.getId());
        option.setDurationMinutes(serviceSpecificationOption.getDurationMinutes());
        option.setDescription(serviceSpecificationOption.getDescription());
        option.setPrice(serviceSpecificationOption.getPrice());

        return option;
    }

    public static StylistServiceSpecification toStylistServiceSpecification(
            StylistServiceSpecificationResponse stylistServiceSpecificationResponse) {

        StylistServiceSpecification stylistServiceSpecification = new StylistServiceSpecification();

        stylistServiceSpecification.setId(stylistServiceSpecificationResponse.getId());
        stylistServiceSpecification.setService(toService(stylistServiceSpecificationResponse.getService()));
        stylistServiceSpecification.setAddonSpecifications(stylistServiceSpecificationResponse.getAddonSpecifications()
                .stream().map(StylistServiceSpecificationMapper::toAddonSpecification).toList());
        stylistServiceSpecification.setHomeServiceAvailable(stylistServiceSpecificationResponse.getHomeServiceAvailable());
        stylistServiceSpecification.setDescription(stylistServiceSpecificationResponse.getDescription());
        stylistServiceSpecification.setDepositPaymentPercent(stylistServiceSpecificationResponse.getDepositPaymentPercent());
        stylistServiceSpecification.setHomeServiceAdditionalPrice(stylistServiceSpecificationResponse.getHomeServiceAdditionalPrice());
        stylistServiceSpecification.setImage(stylistServiceSpecificationResponse.getImage());
        stylistServiceSpecification.setNote(stylistServiceSpecificationResponse.getNote());
        stylistServiceSpecification.setOptions(stylistServiceSpecificationResponse.getOptions().stream()
                .map(StylistServiceSpecificationMapper::toServiceSpecificationOption).toList());
        stylistServiceSpecification.setTerms(stylistServiceSpecificationResponse.getTerms());

        return stylistServiceSpecification;
    }

    private static GlamoorService toService(
            StylistServiceSpecificationResponse.GlamoorService stylistServiceSpecificationResponseService) {

        GlamoorService glamoorService = new GlamoorService();

        glamoorService.setId(stylistServiceSpecificationResponseService.getId());
        glamoorService.setName(stylistServiceSpecificationResponseService.getName());
        glamoorService.setDescription(stylistServiceSpecificationResponseService.getDescription());
        glamoorService.setCategoryId(stylistServiceSpecificationResponseService.getCategoryId());

        return glamoorService;
    }

    private static AddonSpecification toAddonSpecification(
            StylistServiceSpecificationResponse.AddonSpecification stylistServiceSpecificationResponseAddonSpecification) {

        AddonSpecification addonSpecification = new AddonSpecification();

        addonSpecification.setId(stylistServiceSpecificationResponseAddonSpecification.getId());
        addonSpecification.setAddon(toAddon(stylistServiceSpecificationResponseAddonSpecification.getAddon()));
        addonSpecification.setNote(stylistServiceSpecificationResponseAddonSpecification.getNote());
        addonSpecification.setImage(stylistServiceSpecificationResponseAddonSpecification.getImage());
        addonSpecification.setTerms(stylistServiceSpecificationResponseAddonSpecification.getTerms());
        addonSpecification.setDepositPaymentPercent(stylistServiceSpecificationResponseAddonSpecification.getDepositPaymentPercent());
        addonSpecification.setDescription(stylistServiceSpecificationResponseAddonSpecification.getDescription());
        addonSpecification.setHomeServiceAdditionalPrice(stylistServiceSpecificationResponseAddonSpecification.getHomeServiceAdditionalPrice());
        addonSpecification.setHomeServiceAvailable(stylistServiceSpecificationResponseAddonSpecification.getHomeServiceAvailable());
        addonSpecification.setOptions(stylistServiceSpecificationResponseAddonSpecification.getOptions()
                .stream().map(StylistServiceSpecificationMapper::toServiceSpecificationOption).toList());

        return addonSpecification;
    }

    private static Addon toAddon(
            StylistServiceSpecificationResponse.Addon stylistServiceSpecificationResponseAddon) {

        Addon addon = new Addon();

        addon.setId(stylistServiceSpecificationResponseAddon.getId());
        addon.setName(stylistServiceSpecificationResponseAddon.getName());
        addon.setDescription(stylistServiceSpecificationResponseAddon.getDescription());

        return addon;
    }

    private static StylistServiceSpecification.ServiceSpecificationOption toServiceSpecificationOption(
            StylistServiceSpecificationResponse.ServiceSpecificationOption stylistServiceSpecificationResponseServiceSpecificationOption) {

        StylistServiceSpecification.ServiceSpecificationOption option = new StylistServiceSpecification.ServiceSpecificationOption();

        option.setId(stylistServiceSpecificationResponseServiceSpecificationOption.getId());
        option.setPrice(stylistServiceSpecificationResponseServiceSpecificationOption.getPrice());
        option.setDescription(stylistServiceSpecificationResponseServiceSpecificationOption.getDescription());
        option.setDurationMinutes(stylistServiceSpecificationResponseServiceSpecificationOption.getDurationMinutes());

        return option;
    }
}
