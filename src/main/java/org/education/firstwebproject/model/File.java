package org.education.firstwebproject.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String name;

    @Column(length = 1024)
    private String type;

    @Column()
    private long size;

    @Column(name = "filePath", unique = true)
    private String filePath;

}
