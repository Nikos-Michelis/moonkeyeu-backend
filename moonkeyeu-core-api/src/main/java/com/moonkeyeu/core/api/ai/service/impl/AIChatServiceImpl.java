package com.moonkeyeu.core.api.ai.service.impl;

import com.moonkeyeu.core.api.ai.service.AIChatService;
import com.moonkeyeu.core.api.ai.service.AIUsageServiceBuilder;
import com.moonkeyeu.core.api.ai.service.AiService;
import com.moonkeyeu.core.api.launch.dto.AiResponseDTO;
import com.moonkeyeu.core.api.ai.model.AiPrompt;
import com.moonkeyeu.core.api.ai.model.AiUsage;
import com.moonkeyeu.core.api.launch.repository.AiPromptRepository;
import com.moonkeyeu.core.api.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {
    public final AiPromptRepository aiPromptRepository;
    public final AiService aiService;
    private final AIUsageServiceBuilder aiUsageServiceBuilder;

    @Transactional
    @Override
    public AiResponseDTO getQuestionAnswer(User user, String question) {
        AiPrompt completion = generateQuestionCompletion(user, question);
        return AiResponseDTO.builder()
                .completion(completion.getCompletion())
                .updatedAt(completion.getUpdatedAt())
                .build();
    }

    private AiPrompt generateQuestionCompletion(User user, String question){

        ChatResponse chatResponse = aiService.chat(question);
        String completionResponse = chatResponse.getResult().getOutput().getText();

        AiUsage newAiUsage = aiUsageServiceBuilder.usageBuilder(chatResponse, 1, user);
        AiPrompt newAiPrompt = promptBuilder(Prompt.builder().content(question).build(), completionResponse);

        newAiUsage.setPrompt(newAiPrompt);
        newAiPrompt.setUsage(newAiUsage);

        return aiPromptRepository.save(newAiPrompt);
    }

    private AiPrompt promptBuilder(Prompt prompt,  String completionResponse){
        return AiPrompt.builder().prompt(prompt.getContents()).completion(completionResponse).build();
    }

}
