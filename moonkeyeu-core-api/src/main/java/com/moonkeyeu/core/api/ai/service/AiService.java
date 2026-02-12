package com.moonkeyeu.core.api.ai.service;

import org.springframework.ai.chat.model.ChatResponse;

public interface AiService {
    ChatResponse chat(String prompt);
}
