package com.pstreaming.repository;

import com.pstreaming.domain.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    public Pelicula findByTitulo(String titulo);

}
