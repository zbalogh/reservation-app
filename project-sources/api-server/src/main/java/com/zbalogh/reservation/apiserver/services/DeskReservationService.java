package com.zbalogh.reservation.apiserver.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zbalogh.reservation.apiserver.config.ReservationAppConfig;
import com.zbalogh.reservation.apiserver.dao.DeskReservationRepository;
import com.zbalogh.reservation.apiserver.entities.DeskReservation;
import com.zbalogh.reservation.apiserver.resources.DeskReservationInfo;

@Service("deskReservationService")
@Transactional
public class DeskReservationService {
	
	private static final Logger logger = LoggerFactory.getLogger(DeskReservationService.class);
	
	private static final int DEFAULT_NUMBER_OF_ALL_DESKS = 200;
	
	@Autowired
	private ReservationAppConfig reservationAppConfig;

	@Autowired
	private DeskReservationRepository repository;

	@Transactional(readOnly=true)
	public List<DeskReservation> findAll()
	{
		 List<DeskReservation> resultList = Lists.newArrayList(repository.findAll());
		 
		 return Collections.unmodifiableList(resultList);
	}
	
	public DeskReservation findById(Long id)
	{
		Optional<DeskReservation> optional = repository.findById(id);
		
		return optional.isPresent() ? optional.get() : null;
	}
	
	public DeskReservation findByDeskNumber(Integer deskNumber)
	{
		Optional<DeskReservation> optional = repository.findByDeskNumber(deskNumber);
		
		return optional.isPresent() ? optional.get() : null;
	}
	
	public int getAlldeskNumber()
	{
		// set the list size from the default value
		int result = DEFAULT_NUMBER_OF_ALL_DESKS;

		int alldeskNumber = reservationAppConfig.getAlldeskNumber();

		// if we have value from the configuration then we use it
		if (alldeskNumber > 0) {
			result = alldeskNumber;
		}
		
		return result;
	}
	
	public DeskReservationInfo getInfo()
	{
		final DeskReservationInfo info = new DeskReservationInfo();
		
		// set the list size
		int listSize = getAlldeskNumber();
		
		// initialize the list with the given size
		info.initList(listSize);
		logger.info("DeskReservationInfo has been initialized with size=" + listSize);
		
		// get all existing reservations
		final List<DeskReservation> reservations = findAll();
		
		// iterate the reservations and set the ID for the given desk
		for (DeskReservation reservation : reservations) {
			Integer deskNumber = reservation.getDeskNumber();
			Long id = reservation.getId();
			info.addValue(deskNumber-1, id);
		}
		
		// only a test
		//info.addValue(5, 1L);
		
		return info;
	}
	
	public DeskReservation save(DeskReservation entity)
	{
		entity = repository.save(entity);
		
		return entity;
	}
	
	public void delete(DeskReservation entity)
	{
		repository.delete(entity);
	}
	
	public void deleteById(Long id)
	{
		repository.deleteById(id);
	}
	
}
