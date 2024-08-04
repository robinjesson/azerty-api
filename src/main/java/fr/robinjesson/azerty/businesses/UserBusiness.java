package fr.robinjesson.azerty.businesses;

import fr.robinjesson.azerty.entities.UserEntity;
import fr.robinjesson.azerty.exception.NotFoundException;
import fr.robinjesson.azerty.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBusiness {
    private final UserRepository userRepository;

    public UserEntity findById(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UserEntity save(final UserEntity user) {
        return userRepository.save(user);
    }
}
