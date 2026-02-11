package com.othertales.modules.writing.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for reordering chapters within a project.
 * The client sends the full ordered list of chapter IDs in the desired order.
 *
 * @param orderedChapterIds List of chapter UUIDs in the desired display order
 *                          (index 0 = first)
 */
public record ReorderChaptersRequest(
                @NotEmpty(message = "Chapter order list cannot be empty") List<@NotNull UUID> orderedChapterIds) {
}
