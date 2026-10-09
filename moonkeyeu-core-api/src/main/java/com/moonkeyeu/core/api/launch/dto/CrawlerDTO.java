package com.moonkeyeu.core.api.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrawlerDTO {
    private String title;
    private String description;
    private String image;
    private Instant datePublished;
    private Instant dateModified;
}
