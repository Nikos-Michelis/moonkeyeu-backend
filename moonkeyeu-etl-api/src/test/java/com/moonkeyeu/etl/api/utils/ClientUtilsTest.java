package com.moonkeyeu.etl.api.utils;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientUtilsTest {

    @Test
    void extractImageNameFromURL_shouldReturnImageName() throws Exception {

        String imageUrl = "https://cdn.test.com/images/photo.png";

        String result = ClientUtils.extractImageNameFromURL(imageUrl);

        assertThat(result).isEqualTo("photo.png");
    }

    @Test
    void extractImageNameFromURL_shouldReturnImageName_whenUrlContainsNestedPath() throws Exception {

        String imageUrl = "https://test.com/assets/images/products/item-1.jpg";

        String result = ClientUtils.extractImageNameFromURL(imageUrl);

        assertThat(result).isEqualTo("item-1.jpg");
    }

    @Test
    void extractImageNameFromURL_shouldThrowException_whenUrlIsNull() {

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(null))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("Image url should not be null or empty");
    }

    @Test
    void extractImageNameFromURL_shouldThrowException_whenUrlIsBlank() {

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(" "))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("Image url should not be null or empty");
    }

    @Test
    void extractImageNameFromURL_shouldThrowException_whenProtocolIsInvalid() {

        String imageUrl = "ftp://test.com/image.jpg";

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(imageUrl))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("Invalid image Url protocol: " + imageUrl);
    }

    @Test
    void extractImageNameFromURL_shouldThrowException_whenPathIsEmpty() {

        String imageUrl = "https://test.com";

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(imageUrl))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("URL path is empty: " + imageUrl);
    }

    @Test
    void extractImageNameFromURL_shouldThrowException_whenPathIsSlashOnly() {

        String imageUrl = "https://test.com/";

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(imageUrl))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("URL path is empty: " + imageUrl);
    }

    @Test
    void extractImageNameFromURL_shouldThrowException_whenUrlSyntaxIsInvalid() {

        String imageUrl = "https://test .com/image.jpg";

        assertThatThrownBy(() -> ClientUtils.extractImageNameFromURL(imageUrl))
                .isInstanceOf(MalformedURLException.class)
                .hasMessage("Invalid URL syntax: " + imageUrl);
    }
}