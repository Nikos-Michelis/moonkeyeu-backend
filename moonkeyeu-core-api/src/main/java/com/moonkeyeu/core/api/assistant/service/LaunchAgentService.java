package com.moonkeyeu.core.api.assistant.service;

import com.moonkeyeu.core.api.launch.dto.AiResponseDTO;
import com.moonkeyeu.core.api.user.model.User;

public interface LaunchAgentService {
    AiResponseDTO getLaunchLatestReport (User user, String launchId);
}
