package com.moonkeyeu.etl.api.dto.chunks;

import com.moonkeyeu.etl.api.configuration.files.CsvSource;
import org.springframework.batch.infrastructure.item.Chunk;

public record ChunkMapper<T>(Chunk<T> data, CsvSource target) {}
