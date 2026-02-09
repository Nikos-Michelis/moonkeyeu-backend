package com.moonkeyeu.etl.api.dto.storage;

import com.moonkeyeu.etl.api.model.CsvEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class EntityConfig implements Serializable {
    private String fileName;
    private Class<? extends CsvEntity<?>> entityClass;
    private int order;
}