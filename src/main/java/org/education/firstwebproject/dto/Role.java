package org.education.firstwebproject.dto;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Transient
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "roles")
    private Set<User> users;

    @Override
    public String getAuthority() {
        return getName();
    }

}
