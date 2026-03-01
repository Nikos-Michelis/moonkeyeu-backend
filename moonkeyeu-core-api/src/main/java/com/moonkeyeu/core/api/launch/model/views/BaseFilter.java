package com.moonkeyeu.core.api.launch.model.views;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@MappedSuperclass
public class BaseFilter {
    @Id
    @Column(name = "virtual_id")
    private String id;

    @Basic
    @Column(name = "filter_id")
    private Long filterId;

    @Basic
    @Column(name = "filter_name")
    private String filterName;

    @Basic
    @Column(name = "filter_type")
    private String filterType;
}
