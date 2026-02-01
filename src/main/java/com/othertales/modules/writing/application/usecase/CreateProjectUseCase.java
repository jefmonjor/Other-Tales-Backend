package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.domain.Project;

import java.util.UUID;

public class CreateProjectUseCase {

    private static final int DEFAULT_TARGET_WORD_COUNT = 50000;

    private final ProjectRepository projectRepository;

    public CreateProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project execute(Command command) {
        var targetWordCount = command.targetWordCount() != null
                ? command.targetWordCount()
                : DEFAULT_TARGET_WORD_COUNT;

        var project = Project.create(
                command.userId(),
                command.title(),
                command.synopsis(),
                command.genre(),
                targetWordCount
        );

        return projectRepository.save(project);
    }

    public record Command(
            UUID userId,
            String title,
            String synopsis,
            String genre,
            Integer targetWordCount
    ) {}
}
