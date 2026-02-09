package com.moonkeyeu.etl.api.service;

import java.io.IOException;

public interface MediaDownloadService {
    byte[] download(String resourceUrl) throws IOException;
}
