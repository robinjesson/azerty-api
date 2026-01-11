package fr.robinjesson.mybudgetapi.mappers;

import fr.robinjesson.mybudgetapi.api.request.TagRequest;
import fr.robinjesson.mybudgetapi.api.response.TagResponse;
import fr.robinjesson.mybudgetapi.entities.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Set;

@Mapper(config = MapperConfiguration.class)
public interface TagMapper {
    TagEntity mapToEntity(TagRequest source);

    Set<TagResponse> mapToResponse(Set<TagEntity> sources);
    TagResponse mapToResponse(TagEntity source);

    void mapToExistingEntity(@MappingTarget TagEntity target, TagRequest source);
}
