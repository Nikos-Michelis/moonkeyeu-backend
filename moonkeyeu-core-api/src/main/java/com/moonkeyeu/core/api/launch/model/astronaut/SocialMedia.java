package com.moonkeyeu.core.api.launch.model.astronaut;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "social_media", schema = "moonkey_db")
public class SocialMedia {
    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long socialId;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "media_url")
    private String mediaUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "astronaut_id")
    @JsonBackReference
    private Astronaut astronaut;
}
