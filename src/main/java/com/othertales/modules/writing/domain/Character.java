package com.othertales.modules.writing.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Character {

    private UUID id;
    private UUID projectId;
    private String name;
    private String role;
    private String description;
    private String physicalDescription;
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean deleted;

    private Character() {
    }

    public static Character create(UUID projectId, String name, String role, String description,
            String physicalDescription, String imageUrl) {
        var character = new Character();
        character.id = UUID.randomUUID();
        character.projectId = Objects.requireNonNull(projectId, "Project ID is required");
        character.name = Objects.requireNonNull(name, "Name is required").trim();
        if (character.name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        character.role = role != null ? role.trim() : null;
        character.description = description != null ? description.trim() : null;
        character.physicalDescription = physicalDescription != null ? physicalDescription.trim() : null;
        character.imageUrl = imageUrl != null ? imageUrl.trim() : null;
        character.createdAt = Instant.now();
        character.updatedAt = character.createdAt;
        character.deleted = false;
        return character;
    }

    public static Character reconstitute(
            UUID id,
            UUID projectId,
            String name,
            String role,
            String description,
            String physicalDescription,
            String imageUrl,
            Instant createdAt,
            Instant updatedAt,
            boolean deleted) {
        var character = new Character();
        character.id = id;
        character.projectId = projectId;
        character.name = name;
        character.role = role;
        character.description = description;
        character.physicalDescription = physicalDescription;
        character.imageUrl = imageUrl;
        character.createdAt = createdAt;
        character.updatedAt = updatedAt;
        character.deleted = deleted;
        return character;
    }

    public void update(String name, String role, String description, String physicalDescription, String imageUrl) {
        if (name != null) {
            if (name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            this.name = name.trim();
        }
        if (role != null)
            this.role = role.trim();
        if (description != null)
            this.description = description.trim();
        if (physicalDescription != null)
            this.physicalDescription = physicalDescription.trim();
        if (imageUrl != null)
            this.imageUrl = imageUrl.trim();

        this.updatedAt = Instant.now();
    }

    public void markAsDeleted() {
        this.deleted = true;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }

    public String getPhysicalDescription() {
        return physicalDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Character character = (Character) o;
        return Objects.equals(id, character.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
