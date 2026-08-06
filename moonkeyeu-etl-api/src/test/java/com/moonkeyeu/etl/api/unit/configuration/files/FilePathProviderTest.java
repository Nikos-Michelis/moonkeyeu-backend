package com.moonkeyeu.etl.api.unit.configuration.files;

import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.dto.storage.EntityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FilePathProvider Tests")
class FilePathProviderTest {

    @Mock
    private RootConfig rootConfig;

    @InjectMocks
    private FilePathProvider filePathProvider;


    @Test
    @DisplayName("Should build correct JSON source path with segment and filename")
    void shouldBuildJsonSourcePath_whenSegmentAndFilenameProvided() {
        // Arrange
        String rootFolder = "/data/json";
        String segment = "users";
        String filename = "users.json";

        when(rootConfig.getJsonRootFolder()).thenReturn(rootFolder);

        // Act
        String result = filePathProvider.getJsonSource(segment, filename);

        // Assert
        String expected = Paths.get(rootFolder, segment, filename).toString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle empty segment in JSON source")
    void shouldBuildJsonSourcePath_whenSegmentIsEmpty() {
        String rootFolder = "/data/json";
        when(rootConfig.getJsonRootFolder()).thenReturn(rootFolder);

        String result = filePathProvider.getJsonSource("", "data.json");
        String expected = Paths.get(rootFolder, "", "data.json").toString();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle special characters in JSON path")
    void shouldBuildJsonSourcePath_whenPathContainsSpecialCharacters() {
        String rootFolder = "/data/json";
        String segment = "segment-v2";
        String filename = "file_2024.json";

        when(rootConfig.getJsonRootFolder()).thenReturn(rootFolder);

        String result = filePathProvider.getJsonSource(segment, filename);
        String expected = Paths.get(rootFolder, segment, filename).toString();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should build correct CSV source path")
    void shouldBuildCsvSourcePath_whenFilenameProvided() {
        // Arrange
        String rootFolder = "/data/csv";
        String filename = "products.csv";

        when(rootConfig.getCsvRootFolder()).thenReturn(rootFolder);

        // Act
        String result = filePathProvider.getCsvSource(filename);

        // Assert
        String expected = Paths.get(rootFolder, filename).toString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle CSV filename with path separators")
    void shouldBuildCsvSourcePath_whenFilenameContainsSubdirectory() {
        String rootFolder = "/data/csv";
        String filename = "exports/data.csv";

        when(rootConfig.getCsvRootFolder()).thenReturn(rootFolder);

        String result = filePathProvider.getCsvSource(filename);
        String expected = Paths.get(rootFolder, filename).toString();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should build correct JSON directory path")
    void shouldBuildJsonDirectoryPath_whenSegmentProvided() {
        // Arrange
        String rootFolder = "/data/json";
        String segment = "documents";

        when(rootConfig.getJsonRootFolder()).thenReturn(rootFolder);

        // Act
        String result = filePathProvider.getJsonDir(segment);

        // Assert
        String expected = Paths.get(rootFolder, segment).toString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle root-level JSON directory")
    void shouldBuildJsonDirectoryPath_whenSegmentIsEmpty() {
        String rootFolder = "/data/json";
        when(rootConfig.getJsonRootFolder()).thenReturn(rootFolder);

        String result = filePathProvider.getJsonDir("");
        String expected = Paths.get(rootFolder, "").toString();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should build correct images directory path")
    void shouldBuildImageDirectoryPath_whenDirNameProvided() {
        // Arrange
        String rootFolder = "/media/images";
        String dirName = "thumbnails";

        when(rootConfig.getImagesRootFolder()).thenReturn(rootFolder);

        // Act
        String result = filePathProvider.getImagesDir(dirName);

        // Assert
        String expected = Paths.get(rootFolder, dirName).toString();
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle nested images directory")
    void shouldBuildImageDirectoryPath_whenDirNameIsNested() {
        String rootFolder = "/media/images";
        String dirName = "products/2024/summer";

        when(rootConfig.getImagesRootFolder()).thenReturn(rootFolder);

        String result = filePathProvider.getImagesDir(dirName);
        String expected = Paths.get(rootFolder, dirName).toString();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should return CSV groups sorted by order")
    void shouldReturnCsvGroupsSortedByOrder_whenCsvGroupsAreRequested() {
        // Arrange
        String csvFolder = "/data/csv";
        when(rootConfig.getCsvRootFolder()).thenReturn(csvFolder);

        // Act
        LinkedList<EntityConfig> result = filePathProvider.getCsvGroups();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // Verify sorting by order
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getOrder() <= result.get(i + 1).getOrder(),
                    "CSV groups should be sorted by order");
        }
    }

    @Test
    @DisplayName("Should return LinkedList type for CSV groups")
    void shouldReturnLinkedListType_whenGettingCsvGroups() {
        when(rootConfig.getCsvRootFolder()).thenReturn("/data/csv");

        LinkedList<EntityConfig> result = filePathProvider.getCsvGroups();

        assertInstanceOf(LinkedList.class, result);
    }

    @Test
    @DisplayName("Should have EntityConfig with correct data")
    void shouldPopulateEntityConfigWithCorrectData_whenCsvGroupsAreRetrieved() {
        when(rootConfig.getCsvRootFolder()).thenReturn("/data/csv");

        LinkedList<EntityConfig> result = filePathProvider.getCsvGroups();

        // Verify each EntityConfig has required fields
        result.forEach(config -> {
            assertNotNull(config.getFileName());
            assertNotNull(config.getEntityClass());
        });
    }

    @Test
    @DisplayName("Should work with realistic path configuration")
    void testWithRealisticConfiguration() {
        // Arrange
        String jsonRoot = "/app/data/json";
        String csvRoot = "/app/data/csv";
        String imagesRoot = "/app/media/images";

        when(rootConfig.getJsonRootFolder()).thenReturn(jsonRoot);
        when(rootConfig.getCsvRootFolder()).thenReturn(csvRoot);
        when(rootConfig.getImagesRootFolder()).thenReturn(imagesRoot);

        // Act & Assert
        assertEquals(
                Paths.get(jsonRoot, "users", "users.json").toString(),
                filePathProvider.getJsonSource("users", "users.json")
        );

        assertEquals(
                Paths.get(csvRoot, "products.csv").toString(),
                filePathProvider.getCsvSource("products.csv")
        );

        assertEquals(
                Paths.get(imagesRoot, "gallery").toString(),
                filePathProvider.getImagesDir("gallery")
        );
    }

    @Test
    @DisplayName("Should handle Windows-style paths")
    void testWithWindowsPaths() {
        String rootFolder = "C:\\data\\json";
        String segment = "documents";
        String filename = "document.json";

        when(rootConfig.getJsonRootFolder()).thenReturn(rootFolder);

        String result = filePathProvider.getJsonSource(segment, filename);
        String expected = Paths.get(rootFolder, segment, filename).toString();

        assertEquals(expected, result);
        assertTrue(result.contains(segment));
        assertTrue(result.contains(filename));
    }

    @Test
    @DisplayName("Should return consistent paths across multiple calls")
    void testPathConsistency() {
        String jsonRoot = "/data/json";
        when(rootConfig.getJsonRootFolder()).thenReturn(jsonRoot);

        String result1 = filePathProvider.getJsonDir("segment1");
        String result2 = filePathProvider.getJsonDir("segment1");

        assertEquals(result1, result2, "Same input should return same path");
    }
}