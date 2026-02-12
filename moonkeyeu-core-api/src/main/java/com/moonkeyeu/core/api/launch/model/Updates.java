package com.moonkeyeu.core.api.launch.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moonkeyeu.core.api.launch.model.launch.Launch;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(of = "updateId")
@Table(name = "updates", schema = "moonkey_db")
public class Updates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "update_id")
    private Long updateId;
    @Basic
    @Column(name = "profile_image")
    private String profileImage;
    @Basic
    @Column(name = "comment")
    private String comment;
    @Basic
    @Column(name = "info_url")
    private String infoUrl;
    @Basic
    @Column(name = "created_by")
    private String createdBy;
    @Basic
    @Column(name = "created_on")
    private Timestamp createdOn;
    @ManyToOne
    @JoinColumn(name = "launch_id")
    private Launch launch;
}
