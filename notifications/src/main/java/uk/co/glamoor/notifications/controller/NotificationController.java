package uk.co.glamoor.notifications.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import uk.co.glamoor.notifications.dto.UserRequest;
import uk.co.glamoor.notifications.dto.UserSettingsRequest;
import uk.co.glamoor.notifications.mapper.UserMapper;
import uk.co.glamoor.notifications.model.Device;
import uk.co.glamoor.notifications.model.Notification;
import uk.co.glamoor.notifications.model.User;
import uk.co.glamoor.notifications.service.DeviceService;
import uk.co.glamoor.notifications.service.NotificationService;
import uk.co.glamoor.notifications.service.UserService;

@RestController
@RequestMapping("/api/notifications")
@Validated
public class NotificationController {
	
	private final NotificationService notificationService;
	private final UserService userService;
	private final DeviceService deviceService;
	
	public NotificationController(NotificationService notificationService,
			UserService userService, 
			DeviceService deviceService) {
		this.notificationService = notificationService;
		this.userService = userService;
		this.deviceService = deviceService;
	}
	
	@GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam @NotBlank(message = "recipientId must be a string.") String recipientId,
            @RequestParam @PositiveOrZero(message = "offset must be at least 0.") int offset) {
		
        List<Notification> notifications = notificationService.getNotifications(recipientId, offset);
        return ResponseEntity.ok(notifications);
    }
	
	@PatchMapping("/{id}/mark-read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable String id) {
        boolean updated = notificationService.markAsRead(id);
        if (updated) {
            return ResponseEntity.ok("Notification read.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found.");
        }
    }
	
	@PatchMapping("/mark-seen")
    public ResponseEntity<String> markNotificationAsSeen(@PathVariable String recipientId,
    		@RequestParam LocalDateTime time) {
		
        boolean updated = notificationService.markAsSeen(recipientId, time);
        if (updated) {
            return ResponseEntity.ok("Notification read.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found.");
        }
    }
	
	@PostMapping("/users")
	public ResponseEntity<?> addUser(@RequestBody @Valid UserRequest userRequest) {
		
		userService.addUser(UserMapper.toUser(userRequest));
		
		return ResponseEntity.ok("User added");
	}
	
	@PostMapping("/devices")
	public ResponseEntity<?> addDevice(@RequestBody @NotNull Device device) {
		
		deviceService.addDevice(device);
		
		return ResponseEntity.ok("Device added.");
	}

	@PatchMapping("/{userId}/settings")
	public ResponseEntity<?> updateCustomerSettings(
			@PathVariable @NotBlank String userId,
			@RequestBody @Valid UserSettingsRequest userSettingsRequest) {

		userService.updateUserSettings(userId, UserMapper.toUserSettings(userSettingsRequest));

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{userId}/settings")
	public ResponseEntity<User.UserSettings> getCustomerSettings(
			@PathVariable @NotBlank String userId) {

		User user = userService.findUser(userId);
		System.out.println(user);
		return ResponseEntity.ok(user.getSettings());
	}
	
}
