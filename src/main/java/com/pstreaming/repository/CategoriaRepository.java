package com.pstreaming.repository;

import com.pstreaming.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    public Categoria findByNombre(String nombre);

}
