package com.moonkeyeu.core.api.assistant.service.impl;

import com.moonkeyeu.core.api.assistant.service.AIUsageServiceBuilder;
import com.moonkeyeu.core.api.assistant.service.AiService;
import com.moonkeyeu.core.api.assistant.service.LaunchAgentService;
import com.moonkeyeu.core.api.assistant.service.PromptServiceBuilder;
import com.moonkeyeu.core.api.configuration.utils.CacheNames;
import com.moonkeyeu.core.api.launch.dto.AiResponseDTO;
import com.moonkeyeu.core.api.launch.model.Updates;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import com.moonkeyeu.core.api.launch.dto.NewsDTO;
import com.moonkeyeu.core.api.launch.repository.LaunchRepository;
import com.moonkeyeu.core.api.launch.services.*;
import com.moonkeyeu.core.api.assistant.model.AiPrompt;
import com.moonkeyeu.core.api.assistant.model.AiUsage;
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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.moonkeyeu.core.api.utils.DateTimeUtil.getDateTimeFormatter;
import static com.moonkeyeu.core.api.utils.DateTimeUtil.getStartOfCurrentYearUtc;

@Slf4j
@Service
@RequiredArgsConstructor
public class LaunchAgentServiceImpl implements LaunchAgentService {
    public final AiPromptRepository aiPromptRepository;
    public final AiService aiService;
    public final PromptServiceBuilder promptBuilderService;
    public final ClientNewsService clientNewsService;
    private final LaunchRepository launchRepository;
    private final AIUsageServiceBuilder aiUsageServiceBuilder;


    @Override
    @Transactional
    @Cacheable(value = CacheNames.AI_CACHE, key = "'ai-launch-' + #launchId", sync = true)
    public AiResponseDTO getLaunchLatestReport(User user, String launchId) {

        Launch launch = launchRepository.findLaunchWithLaunchId(launchId)
                .orElseThrow(() -> new ResourceNotFoundException("Launch not found or out of bound."));

        AiPrompt aiPrompt = aiPromptRepository.findAiPromptByLaunchId(launch.getLaunchId())
                .orElse(null);

        boolean needsNewCompletion = aiPrompt == null ||
                aiPrompt.getLaunches().stream()
                        .filter(l -> aiPrompt.getCreatedAt().isBefore(l.getLastUpdated()))
                        .toList()
                        .isEmpty();

        AiPrompt completion = needsNewCompletion ? generateLaunchCompletion(user, launch) : aiPrompt;

        return AiResponseDTO.builder()
                .id(launchId)
                .completion(completion.getCompletion())
                .updatedAt(completion.getUpdatedAt())
                .build();
    }

    private AiPrompt generateLaunchCompletion(User user, Launch launch){
        List<NewsDTO> newsDTO = clientNewsService.fetchLatestNewsByLaunchId(launch.getLaunchId()).block();
        String newsText = getNewsText(newsDTO);
        String updatesText = getUpdatesText(launch.getUpdates());
        Prompt prompt = promptBuilderService.buildLaunchPrompt(launch, newsText, updatesText);
        System.out.println("length: " + prompt.getContents().length());
        System.out.println("length: " + prompt.getContents().trim());
        ChatResponse chatResponse = aiService.chat(prompt.getContents());
        String completionResponse = chatResponse.getResult().getOutput().getText();

        AiUsage newAiUsage = aiUsageServiceBuilder.usageBuilder(chatResponse, 1, user);
        AiPrompt newAiPrompt = promptBuilder(launch, prompt, completionResponse);

        newAiUsage.setPrompt(newAiPrompt);
        newAiPrompt.setUsage(newAiUsage);

        return aiPromptRepository.save(newAiPrompt);
    }

    private AiPrompt promptBuilder(Launch launch, Prompt prompt,  String completionResponse){
        AiPrompt aiPrompt = AiPrompt.builder().prompt(prompt.getContents()).completion(completionResponse).build();
        aiPrompt.addLaunch(launch);
        return aiPrompt;
    }

    private String getUpdatesText(Set<Updates> updates) {
        LocalDate startOfYear = getStartOfCurrentYearUtc();
        DateTimeFormatter formatter = getDateTimeFormatter("dd/MM/yyyy", ZoneOffset.UTC);

        if (updates == null || updates.isEmpty()) {
            return "No recent updates.";
        }

        return updates.stream()
                .filter(u -> u.getCreatedOn().toLocalDateTime().toLocalDate().isAfter(startOfYear))
                .sorted((o1, o2) -> o2.getCreatedOn().compareTo(o1.getCreatedOn()))
                .limit(4)
                .map(u -> "- " + u.getComment() + " - " + formatter.format(u.getCreatedOn().toInstant()))
                .collect(Collectors.joining("\n"));
    }

    private String getNewsText(List<NewsDTO> newsDTO) {
        LocalDate startOfYear = getStartOfCurrentYearUtc();
        DateTimeFormatter formatter = getDateTimeFormatter("dd/MM/yyyy", ZoneOffset.UTC);

        if (newsDTO == null || newsDTO.isEmpty()) {
            return "No recent news.";
        }

        return newsDTO.stream()
                .filter(u -> u.getPublishedAt().atZone(ZoneOffset.UTC).toLocalDate().isAfter(startOfYear))
                .sorted((o1, o2) -> o2.getPublishedAt().compareTo(o1.getPublishedAt()))
                .limit(4)
                .map(article ->
                        "- " + article.getTitle() + " - " +
                                formatter.format(article.getPublishedAt()))
                .collect(Collectors.joining("\n"));
    }

}
