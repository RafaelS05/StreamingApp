package com.pstreaming.repository;

import com.pstreaming.domain.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieRepository extends JpaRepository<Serie, Long>{
    
    public Serie findByTitulo(String titulo);
    
}
