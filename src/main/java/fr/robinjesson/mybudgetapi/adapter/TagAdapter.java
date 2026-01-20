package fr.robinjesson.mybudgetapi.adapter;

import fr.robinjesson.mybudgetapi.api.request.TagRequest;
import fr.robinjesson.mybudgetapi.api.response.TagResponse;
import fr.robinjesson.mybudgetapi.businesses.TagBusiness;
import fr.robinjesson.mybudgetapi.entities.TagEntity;
import fr.robinjesson.mybudgetapi.mappers.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagAdapter {
    private final TagBusiness tagBusiness;
    private final TagMapper tagMapper;

    public List<TagResponse> findAllTagsByUser() {
        final List<TagEntity> tagEntities = tagBusiness.findAllTagsByUser();
        return tagMapper.mapToResponse(tagEntities);
    }


    public TagResponse createTagForConnectedUser(final TagRequest tagRequest) {
        final TagEntity tagEntity = tagMapper.mapToEntity(tagRequest);
        final TagEntity createdTag = tagBusiness.createTagForConnectedUser(tagEntity);
        return tagMapper.mapToResponse(createdTag);
    }

    public TagResponse updateTagForConnectedUser(final String label,final TagRequest tagRequest) {
        final TagEntity tagEntity = tagBusiness.findConcreteById(label);
        tagMapper.mapToExistingEntity(tagEntity, tagRequest);
        final TagEntity updatedTag = tagBusiness.updateTagForConnectedUser(tagEntity);
        return tagMapper.mapToResponse(updatedTag);
    }

}
