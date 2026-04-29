package com.moonkeyeu.core.api.assistant.service;

import com.moonkeyeu.core.api.launch.dto.AiResponseDTO;
import com.moonkeyeu.core.api.user.model.User;

public interface AstronautAgentService {
    AiResponseDTO getAstronautBio(User user, Integer astronautId);
}
