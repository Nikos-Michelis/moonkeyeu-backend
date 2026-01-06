package com.moonkeyeu.etl.api.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class ClientUtils {

    public static String extractImageNameFromURL(String imageUrl) throws MalformedURLException {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new MalformedURLException("Image url should not be null or empty");
        }

        URL url = new URL(imageUrl); // validates URL
        String protocol = url.getProtocol();

        if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
            throw new MalformedURLException("Invalid image Url protocol: " + imageUrl);
        }

        return Paths.get(url.getPath()).getFileName().toString();
    }
}
