package com.moonkeyeu.etl.api.service;

import java.io.IOException;

public interface ClientS3CloudService {
    String saveToS3(String imageUrl, String bucketName, String key, boolean skipUpload) throws IOException;
}
