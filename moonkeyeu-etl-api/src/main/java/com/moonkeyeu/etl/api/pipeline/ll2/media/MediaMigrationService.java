package com.moonkeyeu.etl.api.pipeline.ll2.media;

import com.moonkeyeu.etl.api.configuration.batch.jobs.StorageType;
import com.moonkeyeu.etl.api.configuration.batch.jobs.StoreOperation;
import com.moonkeyeu.etl.api.strategy.StorageStrategy;
import com.moonkeyeu.etl.api.strategy.registry.StorageOperationRegistry;
import com.moonkeyeu.etl.api.strategy.registry.StorageStrategyRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Copies images from the upstream host into our own storage and repoints the database at them.
 * <p>As its own step it is driven by a query rather than by whatever happened to be passing through,
 * which makes it restartable for free — the set of rows still pointing upstream is its own progress
 * marker, so an interrupted run simply picks up what is left.<p/>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaMigrationService {

    private static final int MAX_ROWS = 500;

    private final JdbcTemplate jdbcTemplate;
    private final StorageStrategyRegistry storageStrategyRegistry;
    private final StorageOperationRegistry storageOperationRegistry;

    public int migrate(StorageType storageType, StoreOperation operation) {
        StorageStrategy storageStrategy = storageStrategyRegistry.applyStrategy(storageType);
        BiFunction<StorageStrategy, StorableImage, String> operationStrategy = storageOperationRegistry.getStrategy(operation);

        int migrated = 0;
        for (MediaTarget target : MediaTarget.values()) {
            migrated += migrateTarget(target, storageStrategy, operationStrategy);
        }
        log.info("Media migration complete: {} images now served from {}", migrated, storageType);
        return migrated;
    }

    private int migrateTarget(MediaTarget target, StorageStrategy storageStrategy, BiFunction<StorageStrategy, StorableImage, String> operationStrategy) {

        String prefix = storageStrategy.getBaseUrl();

        /**
         * We prefer infinity loop because we don't know exactly how many images we have to process, and it loops until we don't have anything to process.
         * <p> Also, retrive image per 500 batches in order to avoid high consumption memory and accumulate all the data in a single List <p/>
        */
        int migrated = 0;
        while (true) {
            List<PendingImage> pending = findImagesByPrefix(target, prefix);
            if (pending.isEmpty()) {
                break;
            }

            List<PendingImage> updated = new ArrayList<>(pending.size());
            for (PendingImage image : pending) {
                image.setImageUrl(operationStrategy.apply(storageStrategy, image));
                updated.add(image);
            }

            updateImages(target, updated);
            migrated += updated.size();

            if (updated.isEmpty()) {
                break;
            }
        }
        return migrated;
    }
    /**
     * Find images by prefix.
     * So that means if an image have not already processed it will have the provider url (e.g. cdn.thespacedevs.com) and not our application url (cdn.moonkeyeu.com)
     * **/
    private List<PendingImage> findImagesByPrefix(MediaTarget target, String prefix) {
        String sql = "SELECT id, " + target.getUrlColumn()
                + " FROM " + target.getTableName()
                + " WHERE " + target.getUrlColumn() + " IS NOT NULL"
                + " AND " + target.getUrlColumn() + " NOT LIKE CONCAT(?, '%')"
                + " LIMIT " + MAX_ROWS;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new PendingImage(target, rs.getObject("id"), rs.getString(target.getUrlColumn())), prefix);
    }

    private void updateImages(MediaTarget target, List<PendingImage> images) {
        if (images.isEmpty()) {
            return;
        }
        String sql = "UPDATE " + target.getTableName()
                + " SET " + target.getUrlColumn() + " = ?"
                + " WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, images, images.size(), (ps, image) -> {
            ps.setString(1, image.getImageUrl());
            ps.setObject(2, image.getId());
        });
    }
}
