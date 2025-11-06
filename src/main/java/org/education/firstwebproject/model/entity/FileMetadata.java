package org.education.firstwebproject.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "files")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false, unique = true)
    private String uniqueName;

    @Column(length = 1024, nullable = false)
    private String originalName;

    private String type;

    @Column(nullable = false)
    private long size;

    @Column(length = 64, nullable = false, unique = true)
    private String fileHash;
}
