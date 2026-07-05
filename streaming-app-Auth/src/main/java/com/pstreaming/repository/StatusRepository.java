package com.pstreaming.repository;

import com.pstreaming.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {

    public Status findByName(String name);

}
