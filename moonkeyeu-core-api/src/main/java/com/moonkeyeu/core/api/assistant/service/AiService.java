package com.moonkeyeu.core.api.assistant.service;

import org.springframework.ai.chat.model.ChatResponse;

public interface AiService {
    ChatResponse chat(String prompt);
}
