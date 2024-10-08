package fr.robinjesson.azertyapi.businesses;

import fr.robinjesson.azertyapi.entities.UserEntity;
import fr.robinjesson.azertyapi.exception.NotFoundException;
import fr.robinjesson.azertyapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserBusiness {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserEntity findByUuid(final UUID uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UserEntity create(final UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserEntity authenticate(final UserEntity user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        return userRepository.findByEmail(user.getEmail())
                .orElseThrow();
    }
}
