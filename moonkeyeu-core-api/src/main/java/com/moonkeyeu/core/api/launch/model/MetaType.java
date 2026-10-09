package com.moonkeyeu.core.api.launch.model;

import lombok.Getter;

@Getter
public enum MetaType {
    WEBSITE("website"),
    ARTICLE("article");

    private final String identifier;

    MetaType(String identifier) {
        this.identifier = identifier;
    }
}
