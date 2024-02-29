package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Integer> {
    boolean existsByLineAndNumber(String line, int number);

    boolean existsByRoomAndLineAndNumber(Room room, String line, int number);
}
