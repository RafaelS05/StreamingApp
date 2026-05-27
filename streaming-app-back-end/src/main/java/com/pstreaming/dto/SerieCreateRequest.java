package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class SerieCreateRequest {

    private String title;
    private LocalDate publishYear;
    private int seasons;
    private int episodes;
    private String description;
    private Long idCategory;
    private Long idStatus;
}
