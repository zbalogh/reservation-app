package com.zbalogh.reservation.apiserver.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zbalogh.reservation.apiserver.entities.DeskReservation;

@Repository
public interface DeskReservationRepository extends JpaRepository<DeskReservation, Long> {

	public Optional<DeskReservation> findByDeskNumber(Integer deskNumber);
	
	public Optional<DeskReservation> findByReservationIdentifier(String reservationIdentifier);
	
	public Optional<DeskReservation> findByReservationIdentifierAndEmail(String reservationIdentifier, String email);
	
	public void deleteByReservationIdentifier(String reservationIdentifier);
	
	public void deleteByReservationIdentifierAndEmail(String reservationIdentifier, String email);
	
}
