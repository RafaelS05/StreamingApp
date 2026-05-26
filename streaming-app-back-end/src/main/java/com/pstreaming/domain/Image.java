package com.pstreaming.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_image")
    private Long idImage;

    @Column(name = "url_firebase")
    private String firebase;

    @Column(name = "doc_name")
    private String docName;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;
}
