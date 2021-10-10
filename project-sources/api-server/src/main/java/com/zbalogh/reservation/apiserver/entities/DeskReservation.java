package com.zbalogh.reservation.apiserver.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="foglalas")
public class DeskReservation {

	@Id
    @SequenceGenerator(name="foglalas_id_seq", sequenceName="foglalas_id_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="foglalas_id_seq")
	@Column(name = "id", columnDefinition="bigint DEFAULT nextval('foglalas_id_seq')", nullable=false)
	private Long id;
	
	@Column(name = "asztal_szama", nullable = false, unique=true)
	private Integer deskNumber;
	
	@Column(name = "foglalas_idopont", nullable = false)
	private Date reservationAt = new Date();

	@Column(name = "statusz", nullable = false)
	private Integer status= 0;
	
	@Column(name = "keresztnev", nullable = false)
	private String firstname;
	
	@Column(name = "vezeteknev", nullable = false)
	private String lastname;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "telefon")
	private String telephone;
	
	@Column(name = "foglalas_azonosito", nullable = false)
	private String reservationIdentifier;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDeskNumber() {
		return deskNumber;
	}

	public void setDeskNumber(Integer deskNumber) {
		this.deskNumber = deskNumber;
	}

	public Date getReservationAt() {
		return reservationAt;
	}

	public void setReservationAt(Date reservationAt) {
		this.reservationAt = reservationAt;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getReservationIdentifier() {
		return reservationIdentifier;
	}

	public void setReservationIdentifier(String reservationIdentifier) {
		this.reservationIdentifier = reservationIdentifier;
	}
	
}
