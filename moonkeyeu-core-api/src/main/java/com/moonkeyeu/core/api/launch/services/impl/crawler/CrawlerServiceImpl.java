package com.moonkeyeu.core.api.launch.services.impl.crawler;

import com.moonkeyeu.core.api.launch.dto.CrawlerDTO;
import com.moonkeyeu.core.api.launch.dto.ImageDTO;
import com.moonkeyeu.core.api.launch.dto.agency.AgencyDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.astronaut.AstronautDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.launch.LaunchDTO;
import com.moonkeyeu.core.api.launch.dto.pad.LaunchPadDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.program.ProgramDetailedDTO;
import com.moonkeyeu.core.api.launch.dto.rocket.RocketConfigurationDTO;
import com.moonkeyeu.core.api.launch.dto.spacecraft.SpacecraftConfigurationDTO;
import com.moonkeyeu.core.api.launch.model.MetaType;
import com.moonkeyeu.core.api.launch.model.SocialMediaCrawler;
import com.moonkeyeu.core.api.launch.services.*;
import com.moonkeyeu.core.api.settings.exceptions.InvalidUserAgentException;
import com.moonkeyeu.core.api.utils.meta.MetaElementUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {
    private final MetaElementUtil metaElementUtil;
    private final LaunchService launchService;
    private final AstronautService astronautService;
    private final ProgramsService programsService;
    private final SpacecraftService spacecraftService;
    private final LaunchPadService launchPadService;
    private final AgenciesService agenciesService;
    @Value("${application.seo.name}")
    private String applicationName;
    @Value("${application.seo.description}")
    private String defaultDescription;
    @Value("${application.seo.logo}")
    private String applicationLogo;

    @Override
    public boolean isCrawler(String userAgent) throws InvalidUserAgentException{
        if (userAgent == null || userAgent.isBlank()) throw new InvalidUserAgentException("User-Agent should not be null, please provide the User-Agent.");
        String agent = userAgent.toLowerCase();
        return Arrays.stream(SocialMediaCrawler.values())
                .anyMatch(crawler -> crawler.getIdentifier().toLowerCase().contains(agent));
    }

    @Override
    public String getMetaHtml(String content, String url, CrawlerDTO crawlerDTO) {
        String jsonLdScript = metaElementUtil.buildJsonLdScript(crawlerDTO, url);
        return metaElementUtil.buildMetaOg(crawlerDTO, content, url, jsonLdScript);
    }

    @Override
    public String getDefaultMetaHtml(String url) {
        CrawlerDTO crawlerDTO = getDefaultCrawlerDTO();
        return getMetaHtml(MetaType.WEBSITE.getIdentifier(), url, crawlerDTO);
    }

    @Override
    public String getMetaHtmlBySegment(String url, String segment) {
        CrawlerDTO crawlerDTO = getDefaultCrawlerDTO();
        crawlerDTO.setTitle(segment.toLowerCase());
        return getMetaHtml(MetaType.WEBSITE.getIdentifier(), url, crawlerDTO);
    }

    @Override
    public String getLaunchMetaHtml(String id, String url) {
        LaunchDTO launchDTO = (LaunchDTO) launchService.getLaunchById(id);

        String description = getRocketConfigDescription(launchDTO.getRocket().getRocketConfiguration());
        String image = launchDTO.getRocketConfImages() != null ? launchDTO.getRocketConfImages().getImageUrl() : applicationLogo;
        CrawlerDTO crawlerDTO = getParametarizedCrawlerDTO(launchDTO.getLaunchName(), image, description);

        return getMetaHtml(MetaType.ARTICLE.getIdentifier(), url, crawlerDTO);
    }

    @Override
    public String getAstronautMetaHtml(Integer id, String url) {
        AstronautDetailedDTO astronautDetailedDTO = (AstronautDetailedDTO) astronautService.getAstronautById(id);

        String imageUrl = getImageUrl(Optional.ofNullable(astronautDetailedDTO.getAstronautImages()));
        CrawlerDTO crawlerDTO = getParametarizedCrawlerDTO(astronautDetailedDTO.getName(), imageUrl, astronautDetailedDTO.getBio());

        return getMetaHtml(MetaType.ARTICLE.getIdentifier(), url, crawlerDTO);
    }

    @Override
    public String getProgramMetaHtml(Integer id, String url) {
        ProgramDetailedDTO programDetailedDTO = (ProgramDetailedDTO) programsService.getProgramById(id);

        String imageUrl = getImageUrl(Optional.ofNullable(programDetailedDTO.getProgramImages()));
        CrawlerDTO crawlerDTO = getParametarizedCrawlerDTO(programDetailedDTO.getTypeName(), imageUrl, programDetailedDTO.getDescription());

        return getMetaHtml(MetaType.ARTICLE.getIdentifier(), url, crawlerDTO);
    }

    @Override
    public String getSpacecraftMetaHtml(Integer id, String url) {
        SpacecraftConfigurationDTO spacecraftDTO = spacecraftService.getSpacecraftById(id);

        String imageUrl = getImageUrl(Optional.ofNullable(spacecraftDTO.getSpacecraftConfImages()));
        CrawlerDTO crawlerDTO = getParametarizedCrawlerDTO(spacecraftDTO.getSpacecraftConfName(), imageUrl, spacecraftDTO.getDetails() );

        return getMetaHtml(MetaType.ARTICLE.getIdentifier(), url, crawlerDTO);
    }

    @Override
    public String getLaunchPadMetaHtml(Integer id, String url) {
        LaunchPadDetailedDTO launchPadDTO = (LaunchPadDetailedDTO) launchPadService.getLaunchPadById(id);

        String imageUrl = launchPadDTO.getMapImage() != null ? launchPadDTO.getMapImage() : applicationLogo;
        CrawlerDTO crawlerDTO = getParametarizedCrawlerDTO(launchPadDTO.getLaunchPadName(), imageUrl, launchPadDTO.getDescription() );

        return getMetaHtml(MetaType.ARTICLE.getIdentifier(), url, crawlerDTO);
    }

    @Override
    public String getAgencyMetaHtml(Integer id, String url) {
        AgencyDetailedDTO agencyDetailedDTO = (AgencyDetailedDTO) agenciesService.getAgencyById(id);

        String imageUrl = getImageUrl(Optional.ofNullable(agencyDetailedDTO.getAgenciesImages()));
        CrawlerDTO crawlerDTO = getParametarizedCrawlerDTO(agencyDetailedDTO.getAgencyName(), imageUrl, agencyDetailedDTO.getDescription());

        return getMetaHtml(MetaType.ARTICLE.getIdentifier(), url, crawlerDTO);
    }

    private String getImageUrl(Optional<Set<ImageDTO>> imageDTOS) {

        if (imageDTOS.isEmpty()) {
            return applicationLogo;
        }

        Optional<ImageDTO> imageDTO = imageDTOS.get().stream().findFirst();
        return imageDTO.map(ImageDTO::getImageUrl).orElse(applicationLogo);
    }

    private String getRocketConfigDescription(RocketConfigurationDTO configuration) {
        return configuration.getDescription() != null ? configuration.getDescription() : defaultDescription;
    }

    private CrawlerDTO getDefaultCrawlerDTO() {
        return CrawlerDTO.builder()
                .title(applicationName + " - " + "Space Launch Tracker")
                .description(defaultDescription)
                .image(applicationLogo)
                .datePublished(Instant.now())
                .dateModified(Instant.now())
                .build();
    }

    private CrawlerDTO getParametarizedCrawlerDTO(String title, String imageUrl, String description) {
        return CrawlerDTO.builder()
                .title(isNotNullOrEmpty(title)
                        ? applicationName + " - " + capitalizeFirstLetter(title)
                        : applicationName + " - " + "Space Launch Tracker"
                )
                .description(isNotNullOrEmpty(description) ? description : defaultDescription)
                .image(isNotNullOrEmpty(imageUrl) ? imageUrl : applicationLogo)
                .datePublished(Instant.now())
                .dateModified(Instant.now())
                .build();
    }

    private String capitalizeFirstLetter(String title) {
        if (title == null || title.isEmpty()) {
            return title;
        }
        return title.substring(0, 1).toUpperCase() + title.substring(1);
    }

    private boolean isNotNullOrEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}
