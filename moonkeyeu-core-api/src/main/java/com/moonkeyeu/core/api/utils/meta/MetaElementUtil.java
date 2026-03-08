package com.moonkeyeu.core.api.utils.meta;

import com.moonkeyeu.core.api.launch.dto.CrawlerDTO;
import com.moonkeyeu.core.api.settings.exceptions.InvalidUserAgentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MetaElementUtil {
    @Value("${application.name}")
    private String appName;

    public String buildJsonLdScript(CrawlerDTO crawlerDTO, String url) {
        return """
            <script type="application/ld+json">
                {
                    "@context": "https://schema.org",
                    "@type": "WebPage",
                    "url": "%s",
                    "name": "%s",
                    "description": "%s",
                    "image": "%s",
                    "datePublished": "%s",
                    "dateModified": "%s",
                    "inLanguage": "en-US"
                }
            </script>
        """.formatted(
                url,
                appName,
                crawlerDTO.getDescription(),
                crawlerDTO.getImage(),
                crawlerDTO.getDatePublished(),
                crawlerDTO.getDateModified()
        );
    }

    public String buildMetaOg(CrawlerDTO crawlerDTO, String content, String url , String jsonLdScript) {

        if (content.isEmpty()) {
            throw new InvalidUserAgentException("Meta content should not be null or empty");
        }

        return """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                        <title>%s</title>
                        <meta property="og:type" content="%s" />
                        <meta property="og:title" content="%s" />
                        <meta property="og:description" content="%s" />
                        <meta property="og:image" content="%s" />
                        <meta property="og:image:alt" content="%s" />
                        <meta property="og:url" content="%s" />
                        <meta property="og:site_name" content="%s" />
                        <meta property="og:locale" content="en_US" />
                        <meta name="twitter:card" content="summary_large_image" />
                        <meta name="twitter:title" content="%s" />
                        <meta name="twitter:description" content="%s" />
                        <meta name="twitter:image" content="%s" />
                        <meta name="twitter:image:alt" content="%s" />
                        <meta name="author" content="%s" />
                        <link rel="canonical" href="%s" />
                        %s
                    </head>
                    <body></body>
                    </html>
                """.formatted(
                crawlerDTO.getTitle(),
                content,
                crawlerDTO.getTitle(),
                crawlerDTO.getDescription(),
                crawlerDTO.getImage(),
                crawlerDTO.getTitle(),
                url,
                appName,
                crawlerDTO.getTitle(),
                crawlerDTO.getDescription(),
                crawlerDTO.getImage(),
                crawlerDTO.getTitle(),
                appName,
                url,
                jsonLdScript
        );
    }
}
