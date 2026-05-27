package com.pstreaming.dto;

import java.time.LocalDate;
import lombok.*;

@Data
public class MovieCreateRequest {

    private String title;
    private LocalDate publishYear;
    private String description;
    private Long idCategory;
    private Long idStatus;
    
}
