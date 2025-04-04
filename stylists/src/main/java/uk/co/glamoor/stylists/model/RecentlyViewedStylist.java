package uk.co.glamoor.stylists.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "recently-viewed-stylists")
@CompoundIndex(def = "{'customer': 1, 'time': -1}")
public class RecentlyViewedStylist {
    @Id
    private String id;
    private String customer;
    private String stylist;
    private LocalDateTime time = LocalDateTime.now();

    public RecentlyViewedStylist(String stylist, String customer) {
        this.stylist = stylist;
        this.customer = customer;
    }
}
