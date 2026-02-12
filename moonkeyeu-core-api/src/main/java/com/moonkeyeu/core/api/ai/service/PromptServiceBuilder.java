package com.moonkeyeu.core.api.ai.service;

import com.moonkeyeu.core.api.launch.model.astronaut.Astronaut;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import org.springframework.ai.chat.prompt.Prompt;

public interface PromptServiceBuilder {
    Prompt buildLaunchPrompt(Launch launch, String news, String updates);
    Prompt buildAstroanutPrompt(Astronaut astronaut, String launches, String nationalitiesText);
}
