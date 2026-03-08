package com.moonkeyeu.core.api.utils.meta;

import com.moonkeyeu.core.api.launch.dto.CrawlerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {MetaElementUtil.class})
@TestPropertySource(properties = "application.name=MoonkeyEU")
class MetaElementUtilTest {
    @Autowired
    private MetaElementUtil metaElementUtil;
    private CrawlerDTO articleCrawlerDTO;
    private CrawlerDTO pageCrawlerDTO;

    @BeforeEach
    void setUp() {
        this.pageCrawlerDTO = CrawlerDTO.builder()
                .description("Stay up to date with upcoming and past spaceflight from NASA, SpaceX, and other leading space agencies around the world.")
                .image("https://cdn.example.com/media/assets/logo/example-logo.png")
                .dateModified(Instant.now())
                .datePublished(Instant.now())
                .build();
        this.articleCrawlerDTO = CrawlerDTO.builder()
                .title("Falcon 9 Block 5 | Starlink Group 17-18")
                .description("Artemis II is the first crewed mission as part of the Artemis program. Artemis II will send a crew of 4 - 3 Americans and 1 Canadian around the moon and return them back to Earth.")
                .image("https://cdn.example.com/media/assets/logo/example-logo.png")
                .dateModified(Instant.now())
                .datePublished(Instant.now())
                .build();
    }

    @Test
    void shouldReturnJsonLdScript() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String expectedJson = """
        {
            "@context": "https://schema.org",
            "@type": "WebPage",
            "url": "https://www.example.com/launches",
            "name": "TestName",
            "description": "Stay up to date with upcoming and past spaceflight from NASA, SpaceX, and other leading space agencies around the world.",
            "image": "https://cdn.example.com/media/assets/logo/example-logo.png",
            "datePublished": "%s",
            "dateModified": "%s",
            "inLanguage": "en-US"
        }
    """.formatted(this.pageCrawlerDTO.getDateModified(), this.pageCrawlerDTO.getDatePublished());

        String resultJson = this.metaElementUtil.buildJsonLdScript(this.pageCrawlerDTO, "https://www.example.com/launches");
        String expectedJsonOnly = expectedJson.strip();
        String resultJsonOnly = resultJson.replaceAll("(?s)<script.*?>|</script>", "").strip();
        var expectedTree = mapper.readTree(expectedJsonOnly);
        var resultTree = mapper.readTree(resultJsonOnly);
        assertEquals(expectedTree, resultTree);
    }

    @Test
    void shouldReturnMetaOgForWebsiteContent() {
        String url = "https://www.example.com/launches";
        String content = "website";
        String JsonLdScript = """
        {
            "@context": "https://schema.org",
            "@type": "WebPage",
            "url": "https://www.example.com/launches",
            "name": "TestName",
            "description": "Stay up to date with upcoming and past spaceflight from NASA, SpaceX, and other leading space agencies around the world.",
            "image": "https://cdn.example.com/media/assets/logo/example-logo.png",
            "datePublished": "%s",
            "dateModified": "%s",
            "inLanguage": "en-US"
        }
    """.formatted(this.articleCrawlerDTO.getDatePublished(), this.articleCrawlerDTO.getDateModified());

        String expected = """
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
                articleCrawlerDTO.getTitle(),
                content,
                articleCrawlerDTO.getTitle(),
                articleCrawlerDTO.getDescription(),
                articleCrawlerDTO.getImage(),
                articleCrawlerDTO.getTitle(),
                url,
                "TestName",
                articleCrawlerDTO.getTitle(),
                articleCrawlerDTO.getDescription(),
                articleCrawlerDTO.getImage(),
                articleCrawlerDTO.getTitle(),
                "TestName",
                url,
                JsonLdScript
        );
        String resultMetaOg = this.metaElementUtil.buildMetaOg(this.articleCrawlerDTO, content, url, JsonLdScript);
        assertEquals(expected, resultMetaOg);
    }

    @Test
    void shouldReturnMetaOgForArticleContent() {
        String url = "https://www.example.com/launches/8034d81b-af96-460c-a7b7-5c8e7f1a1d86";
        String content = "article";
        String JsonLdScript = """
        {
            "@context": "https://schema.org",
            "@type": "WebPage",
            "url": "https://www.example.com/launches",
            "name": "TestName",
            "description": "Stay up to date with upcoming and past spaceflight from NASA, SpaceX, and other leading space agencies around the world.",
            "image": "https://cdn.example.com/media/assets/logo/example-logo.png",
            "datePublished": "%s",
            "dateModified": "%s",
            "inLanguage": "en-US"
        }
    """.formatted(this.articleCrawlerDTO.getDatePublished(), this.articleCrawlerDTO.getDateModified());

        String expected = """
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
                articleCrawlerDTO.getTitle(),
                content,
                articleCrawlerDTO.getTitle(),
                articleCrawlerDTO.getDescription(),
                articleCrawlerDTO.getImage(),
                articleCrawlerDTO.getTitle(),
                url,
                "TestName",
                articleCrawlerDTO.getTitle(),
                articleCrawlerDTO.getDescription(),
                articleCrawlerDTO.getImage(),
                articleCrawlerDTO.getTitle(),
                "TestName",
                url,
                JsonLdScript
        );
        String resultMetaOg = this.metaElementUtil.buildMetaOg(this.articleCrawlerDTO, content, url, JsonLdScript);
        assertEquals(expected, resultMetaOg);
    }
}