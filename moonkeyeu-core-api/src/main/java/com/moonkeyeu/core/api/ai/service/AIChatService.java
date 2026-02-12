package com.moonkeyeu.core.api.ai.service;

import com.moonkeyeu.core.api.launch.dto.AiResponseDTO;
import com.moonkeyeu.core.api.user.model.User;

public interface AIChatService {
    AiResponseDTO getQuestionAnswer(User user, String question);
}
