package fr.robinjesson.mybudgetapi.businesses;

import fr.robinjesson.mybudgetapi.api.response.TagResponse;
import fr.robinjesson.mybudgetapi.entities.TagEntity;
import fr.robinjesson.mybudgetapi.repository.TagRepository;
import fr.robinjesson.mybudgetapi.repository.UserRepository;
import fr.robinjesson.mybudgetapi.security.ConnectedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagBusiness {
    private final TagRepository tagRepository;
    private final ConnectedUser connectedUser;
    private final UserRepository userRepository;

    public TagEntity findConcreteById(final String label) {
        return tagRepository.findConcreteById(label);
    }

    public Set<TagEntity> findAllTagsByUser() {
        return tagRepository.findAllByOwnerUid(connectedUser.getUid());
    }

    public TagEntity createTagForConnectedUser(final TagEntity tagEntity) {
        return userRepository.findById(connectedUser.getUid()).map(user -> {
            tagEntity.setOwner(user);
            return tagRepository.save(tagEntity);
        }).orElseThrow(() -> new IllegalStateException("User not found for UID " + connectedUser.getUid()));
    }

    public TagEntity updateTagForConnectedUser(final TagEntity tagEntity) {
        if(!tagEntity.getOwner().getUid().equals(connectedUser.getUid()))
            throw new IllegalStateException("Cannot update tag not owned by the connected user");
        return tagRepository.save(tagEntity);
    }

}
