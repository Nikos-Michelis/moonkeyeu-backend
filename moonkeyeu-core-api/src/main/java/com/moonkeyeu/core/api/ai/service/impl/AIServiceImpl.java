package com.moonkeyeu.core.api.ai.service.impl;

import com.moonkeyeu.core.api.settings.exceptions.GenAiException;
import com.moonkeyeu.core.api.ai.service.AiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.stereotype.Service;

@Service
public class AIServiceImpl implements AiService {
    private final ChatClient chatClient;

    public AIServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public ChatResponse chat(String prompt) {
        try {
            return chatClient.prompt().user(prompt).call().chatResponse();
        } catch (GenAiException | NonTransientAiException e) {
            throw new GenAiException("Something went wrong with the AI service.", e);
        }
    }
}
