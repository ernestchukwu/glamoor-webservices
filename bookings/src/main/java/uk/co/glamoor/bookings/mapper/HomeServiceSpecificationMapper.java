package uk.co.glamoor.bookings.mapper;

import uk.co.glamoor.bookings.dto.response.HomeServiceSpecificationResponse;
import uk.co.glamoor.bookings.model.HomeServiceSpecification;

public class HomeServiceSpecificationMapper {

    public static HomeServiceSpecification toHomeServiceSpecification(
            HomeServiceSpecificationResponse homeServiceSpecificationResponse) {

        HomeServiceSpecification homeServiceSpecification = new HomeServiceSpecification();

        homeServiceSpecification.setId(homeServiceSpecificationResponse.getId());
        homeServiceSpecification.setServiceCharge(homeServiceSpecificationResponse.getServiceCharge());
        homeServiceSpecification.setServiceDuration(homeServiceSpecificationResponse.getServiceDuration());
        homeServiceSpecification.setCity(StylistMapper.toCity(homeServiceSpecificationResponse.getCity()));

        return homeServiceSpecification;
    }
}
