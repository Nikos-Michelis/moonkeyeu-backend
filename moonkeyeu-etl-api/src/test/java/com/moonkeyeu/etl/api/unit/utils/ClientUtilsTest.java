package com.moonkeyeu.etl.api.unit.utils;

import com.moonkeyeu.etl.api.utils.ClientUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientUtilsTest {

    @Test
    @DisplayName("Should return image name from valid URL")
    void extractImageNameFromURL_shouldReturnImageName() throws Exception {

        String imageUrl = "https://cdn.test.com/images/photo.png";

        String result = ClientUtils.extractImageNameFromURL(imageUrl);

        assertThat(result).isEqualTo("photo.png");
    }

    @Test
    @DisplayName("Should return image name when URL contains nested path")
    void extractImageNameFromURL_shouldReturnImageName_whenUrlContainsNestedPath() throws Exception {

        String imageUrl = "https://test.com/assets/images/products/item-1.jpg";

        String result = ClientUtils.extractImageNameFromURL(imageUrl);

        assertThat(result).isEqualTo("item-1.jpg");
    }

    @Test
    @DisplayName("Should throw exception when URL is null")
    void extractImageNameFromURL_shouldThrowException_whenUrlIsNull() {

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(null))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("Image url should not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when URL is blank")
    void extractImageNameFromURL_shouldThrowException_whenUrlIsBlank() {

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(" "))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("Image url should not be null or empty");
    }

    @Test
    @DisplayName("Should throw exception when protocol is invalid")
    void extractImageNameFromURL_shouldThrowException_whenProtocolIsInvalid() {

        String imageUrl = "ftp://test.com/image.jpg";

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(imageUrl))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("Invalid image Url protocol: " + imageUrl);
    }

    @Test
    @DisplayName("Should throw exception when URL path is empty")
    void extractImageNameFromURL_shouldThrowException_whenPathIsEmpty() {

        String imageUrl = "https://test.com";

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(imageUrl))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("URL path is empty: " + imageUrl);
    }

    @Test
    @DisplayName("Should throw exception when URL path contains only slash")
    void extractImageNameFromURL_shouldThrowException_whenPathIsSlashOnly() {

        String imageUrl = "https://test.com/";

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(imageUrl))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("URL path is empty: " + imageUrl);
    }

    @Test
    @DisplayName("Should throw exception when URL syntax is invalid")
    void extractImageNameFromURL_shouldThrowException_whenUrlSyntaxIsInvalid() {

        String imageUrl = "https://test .com/image.jpg";

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(imageUrl))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("Invalid URL syntax: " + imageUrl);
    }
}