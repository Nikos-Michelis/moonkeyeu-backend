package com.moonkeyeu.core.api.assistant.service.impl;

import com.moonkeyeu.core.api.launch.model.astronaut.Astronaut;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import com.moonkeyeu.core.api.assistant.service.PromptServiceBuilder;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PromptServiceBuilderImpl implements PromptServiceBuilder {

    public Prompt buildLaunchPrompt(Launch launch, String news, String updates){
        String prompt = """
            Based on the following data, give a short 4-6 line conclusion on whether the launch is likely to succeed on schedule.
            Do not guess beyond this data. Just summarize clearly.
            Launch details:
            - Date: {date}
            - Rocket: {rocket}
            - Location: {location}
            - status: {status}
            - status-description: {status-description}
            Recent updates:
            {updates}
            Recent news:
            {news}
            """;
        Map<String, Object> promptMap = new HashMap<>();
        promptMap.put("date", launch.getNet());
        promptMap.put("rocket", launch.getLaunchName());
        promptMap.put("location", launch.getLaunchPad().getLocation().getLocationName());
        promptMap.put("status",  launch.getLaunchStatus().getAbbrev());
        promptMap.put("updates",  updates);
        promptMap.put("news",  news);
        return buildPrompt(prompt, promptMap);
    }

    public Prompt buildAstroanutPrompt(Astronaut astronaut, String launches, String nationalitiesText){
        Map<String, Object> promptMap = new HashMap<>();
        String prompt = """
            Task: Using only the data below, provide a 4–6 line concise summary of the astronaut’s profile and current status.
            Astronaut bio:
            - Age: {age}
            - BirthDate: {birthdate}
            - Description: {description}
            - status: {status}
            - InSpace: {status}
            Nationality: {nationality}
            Related Launches:
            {launches}
            """;
        promptMap.put("age", astronaut.getAge());
        promptMap.put("birthdate", astronaut.getDateOfBirth());
        promptMap.put("nationality", nationalitiesText);
        promptMap.put("description", astronaut.getBio());
        promptMap.put("status", astronaut.getStatus().getStatusName());
        promptMap.put("inSpace", astronaut.getInSpace());
        promptMap.put("launches",  launches);
        return buildPrompt(prompt, promptMap);
    }

    private Prompt buildPrompt(String prompt, Map<String, Object> promptMap){
        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        promptMap.forEach(promptTemplate::add);
        return promptTemplate.create();
    }
}
