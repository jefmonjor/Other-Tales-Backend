package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.shared.application.port.StoragePort;
import com.othertales.modules.writing.application.dto.CharacterResponse;
import com.othertales.modules.writing.application.dto.CreateCharacterRequest;
import com.othertales.modules.writing.application.dto.UpdateCharacterRequest;
import com.othertales.modules.writing.application.port.CharacterRepository;
import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.domain.Character;
import com.othertales.modules.writing.domain.ProjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final ProjectRepository projectRepository;
    private final StoragePort storagePort;

    public CharacterService(CharacterRepository characterRepository,
            ProjectRepository projectRepository,
            StoragePort storagePort) {
        this.characterRepository = characterRepository;
        this.projectRepository = projectRepository;
        this.storagePort = storagePort;
    }

    @Transactional(readOnly = true)
    public Page<CharacterResponse> getCharactersByProjectId(UUID projectId, Pageable pageable, UUID userId) {
        verifyProjectOwnership(projectId, userId);
        return characterRepository.findAllByProjectId(projectId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CharacterResponse getCharacterById(UUID characterId, UUID userId) {
        var character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + characterId));

        verifyProjectOwnership(character.getProjectId(), userId);
        return toResponse(character);
    }

    @Transactional
    public CharacterResponse createCharacter(UUID projectId, CreateCharacterRequest request, byte[] imageDetails,
            String imageContentType, UUID userId) {
        verifyProjectOwnership(projectId, userId);

        // 1. Save Entity (Initially without URL or with provided one)
        var character = Character.create(
                projectId,
                request.name(),
                request.role(),
                request.description(),
                request.physicalDescription(),
                request.imageUrl() // Fallback if no file is provided but URL string is
        );
        var saved = characterRepository.save(character);

        // 2. Upload Image (if present)
        if (imageDetails != null && imageDetails.length > 0) {
            String path = "projects/" + projectId + "/characters/" + saved.getId() + ".webp";
            try {
                String publicUrl = storagePort.upload(path, imageDetails,
                        imageContentType != null ? imageContentType : "image/webp");
                // 3. Update Entity with URL
                saved.update(
                        saved.getName(),
                        saved.getRole(),
                        saved.getDescription(),
                        saved.getPhysicalDescription(),
                        publicUrl);
                characterRepository.save(saved);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image, rolling back character creation", e);
            }
        }

        return toResponse(saved);
    }

    @Transactional
    public CharacterResponse updateCharacter(UUID characterId, UpdateCharacterRequest request, byte[] imageDetails,
            String imageContentType, UUID userId) {
        var character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + characterId));

        verifyProjectOwnership(character.getProjectId(), userId);

        String imageUrl = character.getImageUrl();
        // If new file provided, upload and update URL
        if (imageDetails != null && imageDetails.length > 0) {
            String path = "projects/" + character.getProjectId() + "/characters/" + character.getId() + ".webp";
            try {
                imageUrl = storagePort.upload(path, imageDetails,
                        imageContentType != null ? imageContentType : "image/webp");
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        // If no file, but URL string in JSON request, use that (e.g. clearing image or
        // manual link)
        else if (request.imageUrl() != null) {
            imageUrl = request.imageUrl();
        }

        character.update(
                request.name(),
                request.role(),
                request.description(),
                request.physicalDescription(),
                imageUrl);

        var saved = characterRepository.save(character);
        return toResponse(saved);
    }

    @Transactional
    public void deleteCharacter(UUID characterId, UUID userId) {
        var character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + characterId));

        verifyProjectOwnership(character.getProjectId(), userId);

        character.markAsDeleted();
        characterRepository.save(character);
    }

    private void verifyProjectOwnership(UUID projectId, UUID userId) {
        if (!projectRepository.existsByIdAndUserId(projectId, userId)) {
            throw new ProjectNotFoundException(projectId);
        }
    }

    private CharacterResponse toResponse(Character character) {
        return new CharacterResponse(
                character.getId(),
                character.getProjectId(),
                character.getName(),
                character.getRole(),
                character.getDescription(),
                character.getPhysicalDescription(),
                character.getImageUrl(),
                character.getCreatedAt(),
                character.getUpdatedAt());
    }
}
