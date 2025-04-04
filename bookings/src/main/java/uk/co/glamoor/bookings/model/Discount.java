package uk.co.glamoor.bookings.model;

import lombok.Data;
import uk.co.glamoor.bookings.enums.DiscountType;

@Data
public class Discount {
	
    private Double amount;

    private DiscountType discountType;
}

