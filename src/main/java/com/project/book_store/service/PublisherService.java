package com.project.book_store.service;

import com.project.book_store.entity.concretes.Publisher;
import com.project.book_store.exception.ConflictException;
import com.project.book_store.payload.mapper.PublisherMapper;
import com.project.book_store.payload.request.PublisherRequest;
import com.project.book_store.payload.response.PublisherResponse;
import com.project.book_store.repository.PublisherRepository;
import com.project.book_store.utils.Messages;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    public Page<PublisherResponse> getAllPublishers(Pageable pageable) {
        Page<Publisher> publisherPage = publisherRepository.findAll(pageable);
        return publisherPage.map(publisherMapper::toResponse);
    }

    public List<PublisherResponse> getAllList() {
        List<Publisher> publisherList = publisherRepository.findAll();
        return publisherMapper.toPublisherResponseList(publisherList);
    }

    public PublisherResponse getPublisherById(Long id) {
        Publisher publisher = findPublisherByIdOrThrow(id);
        return publisherMapper.toResponse(publisher);
    }

    @Transactional
    public PublisherResponse createPublisher(PublisherRequest publisherRequest) {
        if (publisherRepository.existsByNameIgnoreCase(publisherRequest.getName().trim())) {
            throw new ConflictException(Messages.PUBLISHER_ALREADY_EXIST_MESSAGE);
        }

        Publisher newPublisher = publisherMapper.toEntity(publisherRequest);
        Publisher savedPublisher = publisherRepository.save(newPublisher);
        return publisherMapper.toResponse(savedPublisher);
    }

    @Transactional
    public PublisherResponse updatePublisher(Long id, PublisherRequest publisherRequest) {
        Publisher existingPublisher = findPublisherByIdOrThrow(id);
        String newName = publisherRequest.getName().trim();

        if (!existingPublisher.getName().equalsIgnoreCase(newName) &&
                publisherRepository.existsByNameIgnoreCase(newName)) {
            throw new ConflictException(Messages.PUBLISHER_ALREADY_EXIST_MESSAGE);
        }

        publisherMapper.updatePublisherFromRequest(publisherRequest, existingPublisher);
        Publisher updatedPublisher = publisherRepository.save(existingPublisher);
        return publisherMapper.toResponse(updatedPublisher);
    }

    @Transactional
    public void deletePublisher(Long id) {
        Publisher existingPublisher = findPublisherByIdOrThrow(id);
        publisherRepository.delete(existingPublisher);
    }

    public Publisher findPublisherByIdOrThrow(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(Messages.PUBLISHER_NOT_FOUND_MESSAGE, id)));
    }
}