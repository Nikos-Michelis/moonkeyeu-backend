package com.moonkeyeu.core.api.launch.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonkeyeu.core.api.utils.DTOEntity;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NewsDTO implements DTOEntity {
    private int id;
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("news_site")
    private String newsSite;
    private String summary;
    @JsonProperty("published_at")
    private Instant publishedAt;
    @JsonProperty("updated_at")
    private Instant updatedAt;
    private boolean featured;
}