package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class MovieResponse {
    private Long idMovie;
    private String title;
    private LocalDate publishYear;
    private String urlImage;
    private String description;
    private String category;
    private String status;
}
