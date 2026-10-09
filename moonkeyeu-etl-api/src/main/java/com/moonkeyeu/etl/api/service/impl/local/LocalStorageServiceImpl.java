package com.moonkeyeu.etl.api.service.impl.local;
import com.moonkeyeu.etl.api.service.LocalStorageService;
import com.moonkeyeu.etl.api.settings.exceptions.LocalStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class LocalStorageServiceImpl implements LocalStorageService {

    @Override
    public boolean existsByKey(Path filePath) {
        if (Files.exists(filePath)) {
            log.info("Image already exists: {} ", filePath);
            return true;
        }
        return false;
    }

    @Override
    public void save(byte[] data, Path filePath) {
        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            outputStream.write(data);
        } catch (IOException e) {
            throw new LocalStorageException("Failed to save image: " + e.getMessage());
        }
    }
}
