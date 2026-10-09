package com.moonkeyeu.core.api.security.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientInfoExtractor {

    private final UserAgentAnalyzer userAgentAnalyzer;

    public Map<String, String> getClientInfo(HttpServletRequest request) {

        String userAgentString = request.getHeader("User-Agent");

        UserAgent agent = userAgentAnalyzer.parse(userAgentString);

        Map<String, String> clientInfo = new HashMap<>();
        clientInfo.put("os_family", agent.getValue(UserAgent.OPERATING_SYSTEM_NAME));
        clientInfo.put("device_family", agent.getValue(UserAgent.DEVICE_CLASS));
        clientInfo.put("userAgent_family", agent.getValue(UserAgent.AGENT_NAME));
        clientInfo.put("remote_address", request.getRemoteAddr());
        clientInfo.put("remote_host", request.getRemoteHost());
        clientInfo.put("remote_user", request.getRemoteUser());
        clientInfo.put("content_type", request.getHeader("content-type"));

        return clientInfo;
    }
}