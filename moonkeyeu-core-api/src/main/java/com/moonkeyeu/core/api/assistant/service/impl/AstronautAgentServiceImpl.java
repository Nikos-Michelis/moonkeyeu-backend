package com.moonkeyeu.core.api.assistant.service.impl;

import com.moonkeyeu.core.api.assistant.service.AIUsageServiceBuilder;
import com.moonkeyeu.core.api.assistant.service.AiService;
import com.moonkeyeu.core.api.assistant.service.AstronautAgentService;
import com.moonkeyeu.core.api.assistant.service.PromptServiceBuilder;
import com.moonkeyeu.core.api.configuration.utils.CacheNames;
import com.moonkeyeu.core.api.launch.dto.AiResponseDTO;
import com.moonkeyeu.core.api.assistant.model.AiPrompt;
import com.moonkeyeu.core.api.assistant.model.AiUsage;
import com.moonkeyeu.core.api.launch.model.astronaut.Astronaut;
import com.moonkeyeu.core.api.launch.repository.AstronautsRepository;
import com.moonkeyeu.core.api.launch.repository.AiPromptRepository;
import com.moonkeyeu.core.api.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AstronautAgentServiceImpl implements AstronautAgentService {
    public final AiPromptRepository aiPromptRepository;
    public final AiService aiService;
    public final PromptServiceBuilder promptBuilderService;
    private final AstronautsRepository astronautsRepository;
    private final AIUsageServiceBuilder aiUsageServiceBuilder;

    @Override
    @Transactional
    @Cacheable(value = CacheNames.AI_CACHE, key = "'ai-launch-' + #astronautId", sync = true)
    public AiResponseDTO getAstronautBio(User user, Integer astronautId) {

        Astronaut astronaut = astronautsRepository.findAstronautAndLaunchByAstronautId(astronautId)
                .orElseThrow(() -> new ResourceNotFoundException("Astronaut not found or out of bound."));

        AiPrompt aiPrompt = aiPromptRepository.findAiPromptByAstronautId(astronaut.getAstronautId())
                .orElse(null);

        AiPrompt completion = aiPrompt == null ? generateLaunchCompletion(user, astronaut) : aiPrompt;

        return AiResponseDTO.builder()
                .id(astronautId)
                .completion(completion.getCompletion())
                .updatedAt(completion.getUpdatedAt())
                .build();
    }

    private AiPrompt generateLaunchCompletion(User user, Astronaut astronaut){
        String launchesText = getLaunchesText(astronaut);
        String nationalitiesText = getNationalitiesText(astronaut);
        Prompt prompt = promptBuilderService.buildAstroanutPrompt(astronaut, launchesText, nationalitiesText);
        ChatResponse chatResponse = aiService.chat(prompt.getContents());
        String completionResponse = chatResponse.getResult().getOutput().getText();

        AiUsage newAiUsage = aiUsageServiceBuilder.usageBuilder(chatResponse, 1, user);
        AiPrompt newAiPrompt = promptBuilder(astronaut, prompt, completionResponse);

        newAiUsage.setPrompt(newAiPrompt);
        newAiPrompt.setUsage(newAiUsage);

        return aiPromptRepository.save(newAiPrompt);
    }

    private AiPrompt promptBuilder(Astronaut astronaut, Prompt prompt,  String completionResponse){
        AiPrompt aiPrompt = AiPrompt.builder().prompt(prompt.getContents()).completion(completionResponse).build();
        aiPrompt.addAstronaut(astronaut);
        return aiPrompt;
    }

    private String getNationalitiesText(Astronaut astronaut) {
        return astronaut.getCountries().stream()
                .map(c -> " - " + c.getCountryName())
                .collect(Collectors.joining("\n"));
    }

    private String getLaunchesText(Astronaut astronaut) {
        return astronaut.getCrewMembers().stream()
                .filter(crewMember -> crewMember.getAstronaut().getAstronautId().equals(astronaut.getAstronautId()))
                .map(crewMember ->
                        " - " + crewMember.getLaunch().getLaunchName() +
                        " - " + crewMember.getLaunch().getLaunchStatus().getStatusName() +
                        " - " + crewMember.getLaunch().getNet()
                ).collect(Collectors.joining("\n"));
    }
}
