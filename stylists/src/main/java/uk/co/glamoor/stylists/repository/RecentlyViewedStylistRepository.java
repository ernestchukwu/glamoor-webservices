package uk.co.glamoor.stylists.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.glamoor.stylists.model.RecentlyViewedStylist;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecentlyViewedStylistRepository extends MongoRepository<RecentlyViewedStylist, String> {
    RecentlyViewedStylist findByStylistAndCustomer(String stylistId, String customerId);

    List<RecentlyViewedStylist> findByCustomerAndTimeBefore(String customer, LocalDateTime now, Pageable pageable);

}
