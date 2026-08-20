package com.moonkeyeu.etl.api.configuration.batch.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.moonkeyeu.etl.api.pipeline.ll2.Context;
import com.moonkeyeu.etl.api.pipeline.core.RowSink;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.AgencyExtractor;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.CrewExtractor;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.Extractor;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.LaunchExtractor;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.LauncherStageExtractor;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.MissionExtractor;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.PadExtractor;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.ProgramExtractor;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.RocketExtractor;
import com.moonkeyeu.etl.api.pipeline.ll2.extract.SpacecraftStageExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Turns one upstream record into the rows it implies.
 * <p>
 * The extractors are stateless and hold no Spring dependencies, so they are composed here as plain
 * objects rather than component-scanned. The shape of the tree is then visible in one place, which
 * is the point — previously it was spread across twenty-five mapper methods and an eighteen-line
 * wall of {@code chunk.add(...)} calls mapping each collection to a CSV file.
 */
@Slf4j
@Configuration
public class ProcessorsConfig {

    @Bean
    public LaunchExtractor launchExtractor() {
        return new LaunchExtractor(
                new RocketExtractor(
                        new LauncherStageExtractor(),
                        new SpacecraftStageExtractor(new CrewExtractor())),
                new PadExtractor(),
                new MissionExtractor(),
                new ProgramExtractor());
    }

    @Bean
    public AgencyExtractor agencyFeedExtractor() {
        return new AgencyExtractor();
    }

    @Bean
    public ItemProcessor<JsonNode, RowSink> launchesProcessor(LaunchExtractor launchExtractor) {
        return toRowSink(launchExtractor);
    }

    @Bean
    public ItemProcessor<JsonNode, RowSink> agenciesProcessor(AgencyExtractor agencyExtractor) {
        return toRowSink(agencyExtractor);
    }

    private ItemProcessor<JsonNode, RowSink> toRowSink(Extractor root) {
        return node -> {
            RowSink sink = new RowSink();
            root.extract(node, Context.empty(), sink);
            return sink.isEmpty() ? null : sink;
        };
    }
}
