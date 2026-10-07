package com.project.book_store.payload.mapper;

import com.project.book_store.entity.concretes.Publisher;
import com.project.book_store.payload.request.PublisherRequest;
import com.project.book_store.payload.response.PublisherResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    Publisher toEntity(PublisherRequest publisherRequest);

    PublisherResponse toResponse(Publisher publisher);

    List<PublisherResponse> toPublisherResponseList(List<Publisher> publishers);

    void updatePublisherFromRequest(PublisherRequest publisherRequest,@MappingTarget Publisher publisher);

}
