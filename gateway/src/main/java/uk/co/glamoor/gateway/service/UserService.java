package uk.co.glamoor.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.glamoor.gateway.model.entity.User;
import uk.co.glamoor.gateway.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> addUser(User user) {
        return userRepository.save(user);
    }

    public Mono<Void> removeUser(String userId) {
        return userRepository.deleteById(userId);
    }

    public Mono<User> findUser(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("User not found with ID: {}" + userId)));
    }
}
