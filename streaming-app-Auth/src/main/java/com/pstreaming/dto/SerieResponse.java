package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class SerieResponse {

    private Long idSerie;
    private String title;
    private LocalDate publishYear;
    private int seasons;
    private int episodes;
    private String urlImage;
    private String description;
    private String category;
    private String status;

}
