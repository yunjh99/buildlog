package com.example.buildlog.techstack.service;

import com.example.buildlog.techstack.domain.TechStack;
import com.example.buildlog.techstack.domain.TechStackCategory;
import com.example.buildlog.techstack.repository.TechStackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TechStackInitializer implements ApplicationRunner {

    private final TechStackRepository techStackRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        defaultTechStacks().forEach((name, category) ->
                techStackRepository.findByNameIgnoreCase(name)
                        .ifPresentOrElse(
                                techStack -> categorizeIfNeeded(techStack, category),
                                () -> techStackRepository.save(new TechStack(name, category))
                        )
        );
    }

    private void categorizeIfNeeded(TechStack techStack, TechStackCategory category) {
        if (techStack.getCategory() == null
                || techStack.getCategory() == TechStackCategory.UNCATEGORIZED) {
            techStack.update(techStack.getName(), category);
        }
    }

    private Map<String, TechStackCategory> defaultTechStacks() {
        Map<String, TechStackCategory> defaults = new LinkedHashMap<>();
        defaults.put("Java", TechStackCategory.LANGUAGE);
        defaults.put("PHP", TechStackCategory.LANGUAGE);
        defaults.put("Spring Boot", TechStackCategory.FRAMEWORK_LIBRARY);
        defaults.put("Confluence", TechStackCategory.TOOL_IDE);
        defaults.put("Bitbucket", TechStackCategory.TOOL_IDE);
        defaults.put("Slack", TechStackCategory.TOOL_IDE);
        defaults.put("Visual Studio", TechStackCategory.TOOL_IDE);
        defaults.put("IntelliJ IDEA", TechStackCategory.TOOL_IDE);
        return defaults;
    }
}
