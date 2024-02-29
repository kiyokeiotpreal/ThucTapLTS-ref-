package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CinemaRepo extends JpaRepository<Cinema, Integer> {
    boolean existsByCode(String code);
    boolean existsByAddress(String address);

    boolean existsCinemaByAddressAndIdNot(String address, int id);

    boolean existsCinemaByCodeAndIdNot(String code, int id);
}
