package com.othertales.modules.writing.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final int DEFAULT_TARGET = 50000;

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("should create project with all fields")
        void shouldCreateProjectWithAllFields() {
            var project = Project.create(USER_ID, "My Novel", "A great story", "Fantasy", 80000);

            assertNotNull(project.getId());
            assertEquals(USER_ID, project.getUserId());
            assertEquals("My Novel", project.getTitle());
            assertEquals("A great story", project.getSynopsis());
            assertEquals("Fantasy", project.getGenre());
            assertEquals(0, project.getCurrentWordCount());
            assertEquals(80000, project.getTargetWordCount());
            assertEquals(ProjectStatus.DRAFT, project.getStatus());
            assertFalse(project.isDeleted());
            assertNotNull(project.getCreatedAt());
            assertNotNull(project.getUpdatedAt());
            assertEquals(0L, project.getVersion());
        }

        @Test
        @DisplayName("should create project with null genre")
        void shouldCreateProjectWithNullGenre() {
            var project = Project.create(USER_ID, "My Novel", null, null, DEFAULT_TARGET);

            assertNull(project.getGenre());
            assertNull(project.getSynopsis());
        }

        @Test
        @DisplayName("should trim whitespace from title")
        void shouldTrimWhitespaceFromTitle() {
            var project = Project.create(USER_ID, "  My Novel  ", null, null, DEFAULT_TARGET);

            assertEquals("My Novel", project.getTitle());
        }

        @Test
        @DisplayName("should throw exception when title is null")
        void shouldThrowExceptionWhenTitleIsNull() {
            assertThrows(InvalidProjectTitleException.class, () ->
                    Project.create(USER_ID, null, null, null, DEFAULT_TARGET)
            );
        }

        @Test
        @DisplayName("should throw exception when title is empty")
        void shouldThrowExceptionWhenTitleIsEmpty() {
            assertThrows(InvalidProjectTitleException.class, () ->
                    Project.create(USER_ID, "", null, null, DEFAULT_TARGET)
            );
        }

        @Test
        @DisplayName("should throw exception when title is only whitespace")
        void shouldThrowExceptionWhenTitleIsOnlyWhitespace() {
            assertThrows(InvalidProjectTitleException.class, () ->
                    Project.create(USER_ID, "   ", null, null, DEFAULT_TARGET)
            );
        }

        @Test
        @DisplayName("should throw exception when userId is null")
        void shouldThrowExceptionWhenUserIdIsNull() {
            assertThrows(NullPointerException.class, () ->
                    Project.create(null, "Title", null, null, DEFAULT_TARGET)
            );
        }

        @Test
        @DisplayName("should throw exception when targetWordCount is negative")
        void shouldThrowExceptionWhenTargetWordCountIsNegative() {
            assertThrows(InvalidTargetWordCountException.class, () ->
                    Project.create(USER_ID, "Title", null, null, -1)
            );
        }

        @Test
        @DisplayName("should allow zero targetWordCount")
        void shouldAllowZeroTargetWordCount() {
            var project = Project.create(USER_ID, "Title", null, null, 0);

            assertEquals(0, project.getTargetWordCount());
        }
    }

    @Nested
    @DisplayName("updateTitle()")
    class UpdateTitleTests {

        @Test
        @DisplayName("should update title with valid value")
        void shouldUpdateTitleWithValidValue() {
            var project = Project.create(USER_ID, "Old Title", null, null, DEFAULT_TARGET);
            var originalUpdatedAt = project.getUpdatedAt();

            project.updateTitle("New Title");

            assertEquals("New Title", project.getTitle());
            assertTrue(project.getUpdatedAt().isAfter(originalUpdatedAt) ||
                    project.getUpdatedAt().equals(originalUpdatedAt));
        }

        @Test
        @DisplayName("should throw exception when updating with empty title")
        void shouldThrowExceptionWhenUpdatingWithEmptyTitle() {
            var project = Project.create(USER_ID, "Title", null, null, DEFAULT_TARGET);

            assertThrows(InvalidProjectTitleException.class, () ->
                    project.updateTitle("")
            );
        }
    }

    @Nested
    @DisplayName("updateTargetWordCount()")
    class UpdateTargetWordCountTests {

        @Test
        @DisplayName("should update target word count")
        void shouldUpdateTargetWordCount() {
            var project = Project.create(USER_ID, "Title", null, null, DEFAULT_TARGET);

            project.updateTargetWordCount(100000);

            assertEquals(100000, project.getTargetWordCount());
        }

        @Test
        @DisplayName("should throw exception when updating with negative value")
        void shouldThrowExceptionWhenUpdatingWithNegativeValue() {
            var project = Project.create(USER_ID, "Title", null, null, DEFAULT_TARGET);

            assertThrows(InvalidTargetWordCountException.class, () ->
                    project.updateTargetWordCount(-1)
            );
        }
    }

    @Nested
    @DisplayName("updateCurrentWordCount()")
    class UpdateCurrentWordCountTests {

        @Test
        @DisplayName("should update current word count")
        void shouldUpdateCurrentWordCount() {
            var project = Project.create(USER_ID, "Title", null, null, DEFAULT_TARGET);

            project.updateCurrentWordCount(5000);

            assertEquals(5000, project.getCurrentWordCount());
        }

        @Test
        @DisplayName("should set to zero when negative value provided")
        void shouldSetToZeroWhenNegativeValueProvided() {
            var project = Project.create(USER_ID, "Title", null, null, DEFAULT_TARGET);

            project.updateCurrentWordCount(-100);

            assertEquals(0, project.getCurrentWordCount());
        }
    }

    @Nested
    @DisplayName("publish()")
    class PublishTests {

        @Test
        @DisplayName("should change status to PUBLISHED")
        void shouldChangeStatusToPublished() {
            var project = Project.create(USER_ID, "Title", null, null, DEFAULT_TARGET);

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
            var project = Project.create(USER_ID, "Title", null, null, DEFAULT_TARGET);

            project.markAsDeleted();

            assertTrue(project.isDeleted());
        }
    }
}
