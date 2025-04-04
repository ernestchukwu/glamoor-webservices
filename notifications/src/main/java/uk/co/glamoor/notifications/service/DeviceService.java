package uk.co.glamoor.notifications.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.co.glamoor.notifications.model.Device;
import uk.co.glamoor.notifications.repository.DeviceRepository;

@Service
public class DeviceService {
	
	private final DeviceRepository deviceRepository;
	
	public DeviceService(DeviceRepository deviceRepository) {
		this.deviceRepository = deviceRepository;
	}
	
	public void clearDevices(String userId) {
		deviceRepository.deleteByUser(userId);
	}
	
	public void addDevice(Device device) {
		deviceRepository.save(device);
	}
	
	public List<Device> findDevices(String userId) {
		return deviceRepository.findAllDevicesByUser(userId);
	}
}
