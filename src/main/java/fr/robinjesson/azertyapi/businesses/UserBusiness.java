package fr.robinjesson.azertyapi.businesses;

import fr.robinjesson.azertyapi.entities.UserEntity;
import fr.robinjesson.azertyapi.exception.BadRequestException;
import fr.robinjesson.azertyapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBusiness {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserEntity create(final UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        final Optional<UserEntity> existingUser = userRepository.findById(user.getUid());
        if(existingUser.isPresent())
            throw new BadRequestException("User %s already exists".formatted(user.getUid()));
        return userRepository.save(user);
    }

    public UserEntity authenticate(final UserEntity user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUid(),
                        user.getPassword()
                )
        );

        return userRepository.findById(user.getUid())
                .orElseThrow();
    }
}
