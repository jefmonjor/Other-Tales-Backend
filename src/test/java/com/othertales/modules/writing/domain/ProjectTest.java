package com.othertales.modules.writing.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("should create project with valid title")
        void shouldCreateProjectWithValidTitle() {
            var project = Project.create(USER_ID, "My Novel", "A great story");

            assertNotNull(project.getId());
            assertEquals(USER_ID, project.getUserId());
            assertEquals("My Novel", project.getTitle());
            assertEquals("A great story", project.getSynopsis());
            assertEquals(ProjectStatus.DRAFT, project.getStatus());
            assertFalse(project.isDeleted());
            assertNotNull(project.getCreatedAt());
            assertNotNull(project.getUpdatedAt());
            assertEquals(0L, project.getVersion());
        }

        @Test
        @DisplayName("should trim whitespace from title")
        void shouldTrimWhitespaceFromTitle() {
            var project = Project.create(USER_ID, "  My Novel  ", null);

            assertEquals("My Novel", project.getTitle());
        }

        @Test
        @DisplayName("should throw exception when title is null")
        void shouldThrowExceptionWhenTitleIsNull() {
            assertThrows(InvalidProjectTitleException.class, () ->
                    Project.create(USER_ID, null, null)
            );
        }

        @Test
        @DisplayName("should throw exception when title is empty")
        void shouldThrowExceptionWhenTitleIsEmpty() {
            assertThrows(InvalidProjectTitleException.class, () ->
                    Project.create(USER_ID, "", null)
            );
        }

        @Test
        @DisplayName("should throw exception when title is only whitespace")
        void shouldThrowExceptionWhenTitleIsOnlyWhitespace() {
            assertThrows(InvalidProjectTitleException.class, () ->
                    Project.create(USER_ID, "   ", null)
            );
        }

        @Test
        @DisplayName("should throw exception when userId is null")
        void shouldThrowExceptionWhenUserIdIsNull() {
            assertThrows(NullPointerException.class, () ->
                    Project.create(null, "Title", null)
            );
        }
    }

    @Nested
    @DisplayName("updateTitle()")
    class UpdateTitleTests {

        @Test
        @DisplayName("should update title with valid value")
        void shouldUpdateTitleWithValidValue() {
            var project = Project.create(USER_ID, "Old Title", null);
            var originalUpdatedAt = project.getUpdatedAt();

            project.updateTitle("New Title");

            assertEquals("New Title", project.getTitle());
            assertTrue(project.getUpdatedAt().isAfter(originalUpdatedAt) ||
                    project.getUpdatedAt().equals(originalUpdatedAt));
        }

        @Test
        @DisplayName("should throw exception when updating with empty title")
        void shouldThrowExceptionWhenUpdatingWithEmptyTitle() {
            var project = Project.create(USER_ID, "Title", null);

            assertThrows(InvalidProjectTitleException.class, () ->
                    project.updateTitle("")
            );
        }
    }

    @Nested
    @DisplayName("publish()")
    class PublishTests {

        @Test
        @DisplayName("should change status to PUBLISHED")
        void shouldChangeStatusToPublished() {
            var project = Project.create(USER_ID, "Title", null);

            project.publish();

            assertEquals(ProjectStatus.PUBLISHED, project.getStatus());
        }
    }

    @Nested
    @DisplayName("markAsDeleted()")
    class DeleteTests {

        @Test
        @DisplayName("should mark project as deleted (soft delete)")
        void shouldMarkProjectAsDeleted() {
            var project = Project.create(USER_ID, "Title", null);

            project.markAsDeleted();

            assertTrue(project.isDeleted());
        }
    }
}
