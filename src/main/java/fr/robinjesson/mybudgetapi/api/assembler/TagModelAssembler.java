package fr.robinjesson.mybudgetapi.api.assembler;

import fr.robinjesson.mybudgetapi.api.TagController;
import fr.robinjesson.mybudgetapi.api.response.TagResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagModelAssembler implements RepresentationModelAssembler<TagResponse, EntityModel<TagResponse>> {

    @Override
    public EntityModel<TagResponse> toModel(final TagResponse response) {
        return EntityModel.of(response,
                linkTo(methodOn(TagController.class).findAllTagsForConnectedUser()).withSelfRel());
    }
}
