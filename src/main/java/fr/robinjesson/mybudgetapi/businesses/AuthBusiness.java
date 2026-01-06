package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.entities.UserEntity;
import fr.robinjesson.mybudgetapi.exception.BadRequestException;
import fr.robinjesson.mybudgetapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthBusiness {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public UserEntity authenticate(final String uid, final String password) {
        final Optional<UserEntity> optionnalUser = userRepository.findById(uid);
        if(optionnalUser.isEmpty())
            throw new BadRequestException("User %s does not exist".formatted(uid));

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(uid, password);
        authenticationManager.authenticate(authenticationToken);

        final UserEntity userEntity = optionnalUser.get();
        userEntity.setLastConnection(userEntity.getLastConnection());
        return userRepository.save(userEntity);
    }
}
