package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.shared.application.port.StoragePort;
import com.othertales.modules.writing.application.dto.CreateStoryRequest;
import com.othertales.modules.writing.application.dto.StoryResponse;
import com.othertales.modules.writing.application.dto.UpdateStoryRequest;
import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.application.port.StoryRepository;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import com.othertales.modules.writing.domain.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final ProjectRepository projectRepository;
    private final StoragePort storagePort;

    public StoryService(StoryRepository storyRepository,
            ProjectRepository projectRepository,
            StoragePort storagePort) {
        this.storyRepository = storyRepository;
        this.projectRepository = projectRepository;
        this.storagePort = storagePort;
    }

    @Transactional(readOnly = true)
    public Page<StoryResponse> getStoriesByProjectId(UUID projectId, Pageable pageable, UUID userId) {
        verifyProjectOwnership(projectId, userId);
        return storyRepository.findAllByProjectId(projectId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public StoryResponse getStoryById(UUID storyId, UUID userId) {
        var story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        verifyProjectOwnership(story.getProjectId(), userId);
        return toResponse(story);
    }

    @Transactional
    public StoryResponse createStory(UUID projectId, CreateStoryRequest request, byte[] imageDetails,
            String imageContentType, UUID userId) {
        verifyProjectOwnership(projectId, userId);

        var story = Story.create(
                projectId,
                request.title(),
                request.synopsis(),
                request.theme(),
                request.secondaryPlots(),
                request.others(),
                request.imageUrl());
        var saved = storyRepository.save(story);

        if (imageDetails != null && imageDetails.length > 0) {
            String path = "projects/" + projectId + "/stories/" + saved.getId() + ".webp";
            try {
                String publicUrl = storagePort.upload(path, imageDetails,
                        imageContentType != null ? imageContentType : "image/webp");
                saved.update(
                        saved.getTitle(),
                        saved.getSynopsis(),
                        saved.getTheme(),
                        saved.getSecondaryPlots(),
                        saved.getOthers(),
                        publicUrl);
                storyRepository.save(saved);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image, rolling back story creation", e);
            }
        }

        return toResponse(saved);
    }

    @Transactional
    public StoryResponse updateStory(UUID storyId, UpdateStoryRequest request, byte[] imageDetails,
            String imageContentType, UUID userId) {
        var story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        verifyProjectOwnership(story.getProjectId(), userId);

        String imageUrl = story.getImageUrl();
        if (imageDetails != null && imageDetails.length > 0) {
            String path = "projects/" + story.getProjectId() + "/stories/" + story.getId() + ".webp";
            try {
                imageUrl = storagePort.upload(path, imageDetails,
                        imageContentType != null ? imageContentType : "image/webp");
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        } else if (request.imageUrl() != null) {
            imageUrl = request.imageUrl();
        }

        story.update(
                request.title(),
                request.synopsis(),
                request.theme(),
                request.secondaryPlots(),
                request.others(),
                imageUrl);

        var saved = storyRepository.save(story);
        return toResponse(saved);
    }

    @Transactional
    public void deleteStory(UUID storyId, UUID userId) {
        var story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found with id: " + storyId));

        verifyProjectOwnership(story.getProjectId(), userId);

        story.markAsDeleted();
        storyRepository.save(story);
    }

    private void verifyProjectOwnership(UUID projectId, UUID userId) {
        if (!projectRepository.existsByIdAndUserId(projectId, userId)) {
            throw new ProjectNotFoundException(projectId);
        }
    }

    private StoryResponse toResponse(Story story) {
        return new StoryResponse(
                story.getId(),
                story.getProjectId(),
                story.getTitle(),
                story.getSynopsis(),
                story.getTheme(),
                story.getSecondaryPlots(),
                story.getOthers(),
                story.getImageUrl(),
                story.getCreatedAt(),
                story.getUpdatedAt());
    }
}
