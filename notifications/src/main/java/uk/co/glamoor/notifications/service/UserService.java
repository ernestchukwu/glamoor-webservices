package uk.co.glamoor.notifications.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import uk.co.glamoor.notifications.exception.EntityNotFoundException;
import uk.co.glamoor.notifications.exception.EntityType;
import uk.co.glamoor.notifications.model.User;
import uk.co.glamoor.notifications.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}

	public void addUser(User user) {
		Optional<User> optionalUser = userRepository.findById(user.getId());
		if (optionalUser.isPresent()) {
			User _user = optionalUser.get();
			if (user.getEmail() != null && !user.getEmail().isEmpty()){
				_user.setEmail(user.getEmail());
			}
			if (user.getPhone() != null && !user.getPhone().isEmpty()){
				_user.setPhone(user.getPhone());
			}
			userRepository.save(_user);
			return;
		}
		userRepository.save(user);
	}
	
	public User findUser(String id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(id, EntityType.USER));
	}
	
	public List<User> findUsers(List<String> ids) {
		return userRepository.findAllById(ids);
	}

	public void updateUserSettings(String userId, User.UserSettings userSettings) {
		User user = findUser(userId);


//		TODO: Handle scheduled notifications if applicable.
		user.setSettings(userSettings);

		userRepository.save(user);
	}
}
