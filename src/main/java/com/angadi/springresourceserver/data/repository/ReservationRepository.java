package com.angadi.springresourceserver.data.repository;

import com.angadi.springresourceserver.data.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Iterable<Reservation>   findReservationByResDate(LocalDate resDate);
}