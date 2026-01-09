package com.moonkeyeu.etl.api.service;

import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.model.images.*;
import com.moonkeyeu.etl.api.model.images.SpacecraftImagesEntity;
import com.moonkeyeu.etl.api.model.media.MissionPatchesEntity;
import com.moonkeyeu.etl.api.model.pad.LaunchPadEntity;
import com.moonkeyeu.etl.api.service.impl.client.ClientLocalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Deprecated
@Slf4j
public class LocalStorageService {

    private final ClientLocalService clientLocalService;
    @Value("${application.backend.url}")
    private String storageUrl;

    private final Map<Class<? extends CsvEntity>, String> entityToLocalKeyMap = Map.of(
            RocketImageEntity.class, "/rockets/",
	        PadImagesEntity.class, "/pads/",
            LaunchPadEntity.class, "/pads-locations/",
            LauncherImagesEntity.class, "/launchers/",
            SpacecraftImagesEntity.class, "/spacecraft/",
            AstronautImagesEntity.class, "/astronauts/",
            AgenciesImagesEntity.class, "/agencies/",
            MissionPatchesEntity.class, "/missions-patches/",
            ProgramsImagesEntity.class, "/programs/"
    );

    private static final int BATCH_SIZE = 200;

    @Autowired
    public LocalStorageService(
            ClientLocalService clientLocalService
    ) {
        this.clientLocalService = clientLocalService;
    }

    public void saveMediaLocal(List<CsvEntity> resultList, String outputFileName,
                               String[] headers, Class<? extends CsvEntity> type) {
        //CustomBatchIterator.batchStreamOf(resultList.stream(), BATCH_SIZE)
               // .forEach(batch -> processBatch(batch, outputFileName, headers, type));
    }

    private void processBatch(List<CsvEntity> batch, String outputFileName,
                              String[] headers, Class<? extends CsvEntity> type) {
        List<CsvEntity> updatedEntities = new ArrayList<>();
        batch.forEach(entity -> {
            if (entity instanceof ImageEntity) {
                //updateImageUrl((ImageEntity) entity);
            }
            updatedEntities.add(entity);
        });
        //writeToCsv(updatedEntities, outputFileName, headers, type);
    }

    /*private void updateImageUrl(ImageEntity entity) {
        try {
            String localDirPath = imagesSource.buildPath(imagesSource.getRawRootFolder(), entityToLocalKeyMap.get(entity.getClass()));
            if (localDirPath != null) {
                URL url = new URL(storageUrl + entityToLocalKeyMap.get(entity.getClass()));
                String imageUrl = fetchImageService.saveLocal(entity.getImageUrl(), localDirPath, String.valueOf(url));
                entity.setImageUrl(imageUrl);
            }
        } catch (IOException e) {
            log.error("Error processing batch: ", e);
        }
    }*/
    /*
    private void writeToCsv(List<CsvEntity> updatedEntities, String outputFileName,
                            String[] headers, Class<? extends CsvEntity> type) {
        try {
            csvService.writeCSVInBatches(updatedEntities, outputFileName, headers, type, BATCH_SIZE);
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            log.error("Error writing to CSV: ", e);
        }
    }
     */
}
