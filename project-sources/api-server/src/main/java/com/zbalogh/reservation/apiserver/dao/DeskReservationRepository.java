package com.zbalogh.reservation.apiserver.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zbalogh.reservation.apiserver.entities.DeskReservation;

@Repository
public interface DeskReservationRepository extends JpaRepository<DeskReservation, Long> {

	Optional<DeskReservation> findByDeskNumber(Integer deskNumber);
	
}
