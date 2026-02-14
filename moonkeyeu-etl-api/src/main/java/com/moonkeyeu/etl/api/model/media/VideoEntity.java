package com.moonkeyeu.etl.api.model.media;

import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.PkBuilder;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "video_url", schema = "moonkey_db")
public class VideoEntity implements CsvEntity<Object>, PkBuilder {
    @Id
    @Column(name = "video_id")
    @EqualsAndHashCode.Include
    private String video_id;
    @Basic
    @Column(name = "priority")
    private String priority;
    @Basic
    @Column(name = "source")
    private String source;
    @Basic
    @Column(name = "publisher")
    private String publisher;
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
    @Column(name = "video_url")
    private String video_url;
    @Basic
    @Column(name = "launch_id")
    private String launch_id;

    @Override
    public Object getPrimaryKey() {
        setPrimaryKey();
        return video_id;
    }
    @Override
    public void setPrimaryKey() {
        this.video_id = priority + launch_id ;
    }
}
