package com.moonkeyeu.etl.api.unit.strategies;

import com.moonkeyeu.etl.api.config.TestEntity;
import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.strategy.LocalStorageStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalStorageStrategyTest {

    @Mock
    private RootConfig rootConfig;
    @Mock
    private FilePathProvider filePathProvider;
    @Mock
    private LocalMediaService localMediaService;
    @InjectMocks
    private LocalStorageStrategy localStorageStrategy;


    @Test
    @DisplayName("Should return a local image URL when item and dir path are not null")
    void shouldReturnLocalImageURL_whenDirPathAndEntityAreNotNull() throws IOException {
        // given
        TestEntity entity = new TestEntity("1", "test");
        when(rootConfig.getImagesRootFolder()).thenReturn("media");
        when(localMediaService.saveMediaLocal(entity, "media"))
                .thenReturn("https://image.com/test.png");

        // when
        String imageUrl = localStorageStrategy.save(entity);

        // then
        assertEquals("https://image.com/test.png", imageUrl);
        verify(localMediaService).saveMediaLocal(entity, "media");
    }

    @Test
    @DisplayName("Should return an image URL when image entity is not null")
    void shouldGetImageURL_whenMediaEntityIsNotNull() throws MalformedURLException {
        // given
        TestEntity entity = new TestEntity("1", "test");
        when(localMediaService.getLocalHostUrl(entity))
                .thenReturn("https://image.com/test.png");
        // when
        String imageUrl = localStorageStrategy.getUrl(entity);

        // thn
        assertNotNull(imageUrl);
        assertEquals("https://image.com/test.png", imageUrl);

        verify(localMediaService).getLocalHostUrl(entity);
    }
}