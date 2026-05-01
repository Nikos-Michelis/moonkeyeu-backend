package com.moonkeyeu.etl.api.utils;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientUtils {

    public static String extractImageNameFromURL(String imageUrl) throws MalformedURLException {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new MalformedURLException("Image url should not be null or empty");
        }

        try {
            var path = getPath(imageUrl);
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (URISyntaxException e) {
            throw new MalformedURLException("Invalid URL syntax: " + imageUrl);
        }
    }

    private static String getPath(String imageUrl) throws URISyntaxException, MalformedURLException {
        URI uri = new URI(imageUrl);
        String scheme = uri.getScheme();

        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new MalformedURLException("Invalid image Url protocol: " + imageUrl);
        }

        String path = uri.getPath();
        if (path == null || path.isEmpty() || path.equals("/")) {
            throw new MalformedURLException("URL path is empty: " + imageUrl);
        }
        return path;
    }
}
