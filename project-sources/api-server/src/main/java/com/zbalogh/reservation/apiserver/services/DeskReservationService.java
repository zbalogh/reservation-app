package com.zbalogh.reservation.apiserver.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zbalogh.reservation.apiserver.config.ReservationAppConfig;
import com.zbalogh.reservation.apiserver.dao.DeskReservationRepository;
import com.zbalogh.reservation.apiserver.entities.DeskReservation;
import com.zbalogh.reservation.apiserver.exceptions.DeskReservationExistsException;
import com.zbalogh.reservation.apiserver.resources.DeskReservationInfo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
	public Flux<DeskReservation> findAll()
	{
		Flux<DeskReservation> resultFlux;
		
		resultFlux = Mono.fromCallable(() -> {
			return Collections.unmodifiableList(repository.findAll());
		})
		.flatMapMany(list -> Flux.fromIterable(list));
		
		resultFlux.subscribeOn(Schedulers.boundedElastic());
		
		return resultFlux;
	}
	
	public Mono<DeskReservation> findById(Long id)
	{
		// we can use either 'Mono.defer()' or 'Mono.fromCallable', both provides the same solution.
		return Mono.defer(() -> {
			return Mono.justOrEmpty( repository.findById(id) );
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
	public Mono<DeskReservation> findByDeskNumber(Integer deskNumber)
	{
		return Mono.defer(() -> {
			return Mono.justOrEmpty( repository.findByDeskNumber(deskNumber) );
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
	/*
	public DeskReservation getReservationByDeskNumber(Integer deskNumber)
	{
		return repository.findByDeskNumber(deskNumber).orElse(null);
	}
	*/
	
	public Mono<DeskReservation> findByReservationIdentifier(String reservationIdentifier)
	{
		return Mono.defer(() -> {
			return Mono.justOrEmpty( repository.findByReservationIdentifier(reservationIdentifier) );
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
	public Mono<DeskReservation> findByReservationIdentifierAndEmail(String reservationIdentifier, String email)
	{
		return Mono.defer(() -> {
			return Mono.justOrEmpty( repository.findByReservationIdentifierAndEmail(reservationIdentifier, email) );
		})
		.subscribeOn(Schedulers.boundedElastic());
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
	
	public Mono<DeskReservationInfo> getInfo()
	{
		return Mono.fromCallable(() -> {
			return doGetDeskReservationInfo();
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
	/**
	 * It creates an empty desk reservation info object by initializing with the all desk number.
	 * 
	 * @return
	 */
	private DeskReservationInfo createDeskReservationInfo()
	{
		DeskReservationInfo info = new DeskReservationInfo();
		
		// get the all the desk number supported by this reservation
		int listSize = getAlldeskNumber();
		
		// initialize the list with the given size
		info.initList(listSize);
		
		logger.info("DeskReservationInfo has been initialized with size=" + listSize);
		
		return info;
	}
	
	/**
	 * Return a fully initialized the desk reservation info object with the actual status of reservations.
	 * 
	 * @return
	 */
	private DeskReservationInfo doGetDeskReservationInfo()
	{
		// create an empty desk reservation info object
		final DeskReservationInfo info = createDeskReservationInfo();
		
		// get all existing reservations
		final List<DeskReservation> reservations = repository.findAll();
		
		// iterate the reservations and set the ID for the given desk
		for (DeskReservation reservation : reservations) {
			Integer deskNumber = reservation.getDeskNumber();
			Long id = reservation.getId();
			info.addValue(deskNumber-1, id);
		}
		
		return info;
	}
	
	public Mono<DeskReservation> save(DeskReservation entity)
	{
		return Mono.fromCallable(() -> {
			return repository.save(entity);
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
	public Mono<DeskReservation> addOrUpdate(DeskReservation entity)
	{
		return Mono.fromCallable(() -> {
			// if ID is not zero then it's an update request
			boolean isUpdate = entity.getId() != null && entity.getId() > 0;
			
			if (!isUpdate) {
				// let's check whether we have already an item with the given desk number.
				// If so then we response an error with a status code.
				Optional<DeskReservation> d = repository.findByDeskNumber(entity.getDeskNumber());
				
				// we found an existing item with the same desk number
				if (d.isPresent()) {
					//throw an exception with message
					throw new DeskReservationExistsException("Desk reservation already exists with the given desk number: " + entity.getDeskNumber());
				}
				
				// if we add a new reservation then we generate reservation identifier
				// generate reservation identifier and set it to the new reservation entity
				String reservationId = generateReservationIdentifier();
				entity.setReservationIdentifier(reservationId);
			}
			
			// All things are done and the entity is ready to be saved
			return repository.save(entity);
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
	public Mono<Void> delete(DeskReservation entity)
	{
		return Mono.defer(() -> {
			repository.delete(entity);
			return Mono.empty().then();
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
	public Mono<Void> deleteById(Long id)
	{
		return Mono.defer(() -> {
			repository.deleteById(id);
			return Mono.empty().then();
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
	public Mono<Void> deleteByReservationIdentifier(String reservationIdentifier)
	{
		return Mono.defer(() -> {
			repository.deleteByReservationIdentifier(reservationIdentifier);
			return Mono.empty().then();
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
	public String generateReservationIdentifier()
	{
		return RandomStringUtils.randomAlphanumeric(10).toUpperCase();
	}
	
	/*
	public Scheduler createScheduler() {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(10));
    }
    */
	
}
