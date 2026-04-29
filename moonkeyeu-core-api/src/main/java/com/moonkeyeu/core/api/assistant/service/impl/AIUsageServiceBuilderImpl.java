package com.moonkeyeu.core.api.assistant.service.impl;

import com.moonkeyeu.core.api.assistant.model.AiUsage;
import com.moonkeyeu.core.api.assistant.service.AIUsageServiceBuilder;
import com.moonkeyeu.core.api.user.model.User;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AIUsageServiceBuilderImpl implements AIUsageServiceBuilder {

    @Override
    public AiUsage usageBuilder(ChatResponse chatResponse, int providerId, User user) {
        return AiUsage.builder()
                .providerId(providerId)
                .promptTokens(chatResponse.getMetadata().getUsage().getPromptTokens())
                .completionTokens(chatResponse.getMetadata().getUsage().getCompletionTokens())
                .totalTokens(chatResponse.getMetadata().getUsage().getTotalTokens())
                .user(user)
                .build();

    }
}
