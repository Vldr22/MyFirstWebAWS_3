package org.education.firstwebproject.model;

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

    @Column(length = 1024)
    private String name;

    @Column(length = 1024)
    private String type;

    @Column()
    private long size;

    @Column(unique = true, length = 2048)
    private String filePath;

}
