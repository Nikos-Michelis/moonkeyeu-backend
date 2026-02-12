package com.moonkeyeu.core.api.ai.controller;

import com.moonkeyeu.core.api.launch.dto.AiResponseDTO;
import com.moonkeyeu.core.api.ai.service.AIChatService;
import com.moonkeyeu.core.api.ai.service.AstronautAgentService;
import com.moonkeyeu.core.api.ai.service.LaunchAgentService;
import com.moonkeyeu.core.api.security.limiter.RateLimited;
import com.moonkeyeu.core.api.subscription.aop.Subscribed;
import com.moonkeyeu.core.api.subscription.aop.SubscriptionRule;
import com.moonkeyeu.core.api.subscription.model.ProductType;
import com.moonkeyeu.core.api.user.dto.response.ResponseDTO;
import com.moonkeyeu.core.api.user.model.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
@Tag(name = "agent")
public class AgentController {
    private final AstronautAgentService astronautAgentService;
    private final LaunchAgentService launchAgentService;
    private final AIChatService aiChatService;

    @GetMapping("/astronaut/{id}")
    @RateLimited(requests = 30, durationSeconds = 3600)
    public ResponseEntity<?> getAstronautAiConclusion(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Integer astronautId
    ) {
        AiResponseDTO chatRes = astronautAgentService.getAstronautBio(user, astronautId);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .timestamp(Instant.now())
                        .data(chatRes)
                        .build());
    }

    @GetMapping("/launch/{id}")
    @Subscribed(
            expenseId = "launch-report",
            products = {ProductType.TRAIL, ProductType.PRO},
            rules = {
                    @SubscriptionRule(token = 2),
                    // @SubscriptionRule(expression = "#request.mediaUrls().size() > 1", token = 20),
            }
    )
    @RateLimited(requests = 30, durationSeconds = 3600)
    public ResponseEntity<?> getLaunchLatestReport(
            @AuthenticationPrincipal User user,
            @PathVariable("id") String launchId
    ) {
        AiResponseDTO chatRes = launchAgentService.getLaunchLatestReport(user, launchId);
        return ResponseEntity.ok(ResponseDTO.builder()
                .timestamp(Instant.now())
                .data(chatRes)
                .build());
    }

    @PostMapping("/chat")
    @RateLimited(requests = 30, durationSeconds = 3600)
    public ResponseEntity<?> askQuestion(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> payload
    ) {
        String question = payload.get("question");
        AiResponseDTO answer = aiChatService.getQuestionAnswer(user, question);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .timestamp(Instant.now())
                        .data(answer)
                        .build());
    }

}
