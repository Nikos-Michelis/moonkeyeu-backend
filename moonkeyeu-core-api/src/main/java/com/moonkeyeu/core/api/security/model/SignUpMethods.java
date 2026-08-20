package com.moonkeyeu.core.api.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moonkeyeu.core.api.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "entry_methods")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
public class SignUpMethods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private SignUpMethod provider;
    @ManyToMany(mappedBy = "signUpMethods")
    @JsonIgnore
    private Set<User> users;
    @CreatedDate
    @Column(name = "created_at", nullable = false, unique = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, unique = false)
    private Instant updatedAt;
}
