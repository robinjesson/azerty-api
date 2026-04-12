package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.entities.UserEntity;
import fr.robinjesson.mybudgetapi.exception.BadRequestException;
import fr.robinjesson.mybudgetapi.repository.UserRepository;
import fr.robinjesson.mybudgetapi.security.ConnectedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBusiness {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConnectedUser connectedUser;

    public UserEntity create(final UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        final Optional<UserEntity> existingUser = userRepository.findById(user.getUid());
        if(existingUser.isPresent())
            throw new BadRequestException("User %s already exists".formatted(user.getUid()));
        return userRepository.save(user);
    }

    public UserEntity findConnectedUser() {
        return userRepository.findConcreteById(connectedUser.getUid());
    }
}
