package com.pstreaming.repository;

import com.pstreaming.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    public Movie findByTitulo(String titulo);
    
}
