package com.moonkeyeu.core.api.launch.model.info;

import com.moonkeyeu.core.api.launch.model.launch.Launch;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "videos", schema = "moonkey_db")
public class VideoUrl {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private String videoId;
    @Basic
    @Column(name = "priority")
    private Integer priority;
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
    private String featureImage;
    @Basic
    @Column(name = "video_url")
    private String videoUrl;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "launch_id")
    private Launch launch;
}
