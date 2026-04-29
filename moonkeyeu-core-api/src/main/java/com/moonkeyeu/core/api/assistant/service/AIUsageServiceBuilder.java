package com.moonkeyeu.core.api.assistant.service;

import com.moonkeyeu.core.api.assistant.model.AiUsage;
import com.moonkeyeu.core.api.user.model.User;
import org.springframework.ai.chat.model.ChatResponse;

public interface AIUsageServiceBuilder {
    AiUsage usageBuilder(ChatResponse chatResponse, int providerId, User user);
}
