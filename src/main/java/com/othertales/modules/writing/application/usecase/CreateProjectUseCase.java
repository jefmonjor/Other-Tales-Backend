package com.othertales.modules.writing.application.usecase;

import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.domain.Project;

import java.util.UUID;

public class CreateProjectUseCase {

    private final ProjectRepository projectRepository;

    public CreateProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project execute(Command command) {
        var project = Project.create(
                command.userId(),
                command.title(),
                command.synopsis()
        );

        return projectRepository.save(project);
    }

    public record Command(
            UUID userId,
            String title,
            String synopsis
    ) {}
}
