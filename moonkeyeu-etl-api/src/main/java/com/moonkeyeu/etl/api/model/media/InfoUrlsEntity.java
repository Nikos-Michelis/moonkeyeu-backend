package com.moonkeyeu.etl.api.model.media;

import com.moonkeyeu.etl.api.model.CsvEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "info_urls", schema = "moonkey_db")
public class InfoUrlsEntity implements CsvEntity<Object> {
    @Id
    @Column(name = "info_id")
    @EqualsAndHashCode.Include
    private String info_id;
    @Basic
    @Column(name = "priority")
    private Integer priority;
    @Basic
    @Column(name = "source")
    private String source;
    @Basic
    @Column(name = "title")
    private String title;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "feature_image")
    private String feature_image;
    @Basic
    @Column(name = "url")
    private String url;
    @Basic
    @Column(name = "launch_id")
    private String launch_id;

    @Override
    public Object getPrimaryKey() {
        setPrimaryKey();
        return info_id;
    }

    public void setPrimaryKey() {
        this.info_id = priority + launch_id;;
    }
}
