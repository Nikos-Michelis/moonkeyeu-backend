package com.moonkeyeu.core.api.ai.service;

import com.moonkeyeu.core.api.ai.model.AiUsage;
import com.moonkeyeu.core.api.user.model.User;
import org.springframework.ai.chat.model.ChatResponse;

public interface AIUsageServiceBuilder {
    AiUsage usageBuilder(ChatResponse chatResponse, int providerId, User user);
}
