package uk.co.glamoor.notifications.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.co.glamoor.notifications.model.Device;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
	List<Device> findAllDevicesByUser(String user);
	void deleteByUser(String user);
}
