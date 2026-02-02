package com.othertales.modules.writing.infrastructure.config;

import com.othertales.modules.writing.application.port.ProjectRepository;
import com.othertales.modules.writing.application.usecase.CreateProjectUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WritingConfig {

    @Bean
    public CreateProjectUseCase createProjectUseCase(ProjectRepository projectRepository) {
        return new CreateProjectUseCase(projectRepository);
    }
}
