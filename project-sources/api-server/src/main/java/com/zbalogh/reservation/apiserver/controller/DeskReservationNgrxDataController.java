package com.zbalogh.reservation.apiserver.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zbalogh.reservation.apiserver.entities.DeskReservation;
import com.zbalogh.reservation.apiserver.exceptions.DeskReservationExistsException;
import com.zbalogh.reservation.apiserver.resources.DeskReservationInfo;
import com.zbalogh.reservation.apiserver.services.DeskReservationService;
import com.zbalogh.reservation.apiserver.utils.AppConstants;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST-API controller for the desk reservations.
 * 
 * The URL end-points are based on the NgRx/Data standard which is defined for the default EntityDataService. 
 * 
 * @author ZBalogh
 *
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(AppConstants.RESTAPI_BASE_URL + "/data")
public class DeskReservationNgrxDataController
{
	private static final Logger logger = LoggerFactory.getLogger(DeskReservationNgrxDataController.class);

	@Autowired
	private DeskReservationService service;
	
	
	@RequestMapping(value="/deskreservation/getinfo", method = RequestMethod.GET)
	public @ResponseBody Mono<DeskReservationInfo> getInfo(ServerHttpRequest request, ServerHttpResponse response)
	{
		return service.getInfo();
	}

	//
	// https://www.baeldung.com/spring-security-method-security
	// https://developer.okta.com/blog/2019/06/20/spring-preauthorize
	//
	// https://www.baeldung.com/spring-webflux-404
	//
	
	//@Secured("ROLE_ADMIN")
	//@PreAuthorize("isAuthenticated()")
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/deskreservations", method = RequestMethod.GET)
	public @ResponseBody Flux<DeskReservation> getAll(ServerHttpRequest request, ServerHttpResponse response)
	{
		return service.findAll();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.GET)
	public Mono<ResponseEntity<DeskReservation>> getById(@PathVariable long id, ServerHttpRequest request, ServerHttpResponse response)
	{
		return service.findById(id)
				// map the given data into ResponseEntity with status code and body
				.map(entityMapper)
				// if no data found, the Mono is empty. So then we switch to this given alternative Mono (ResponseEntity with NOT_FOUND)
				.switchIfEmpty( Mono.just(ResponseEntity.notFound().build()) );
	}
	
	/**
	 * Retrieve the desk reservation details with the given reservation identifier.
	 * The reservation identifier is generated during the reservation process.
	 * The user is allowed to get details for his own reservation by using the reservation identifier.
	 * 
	 * @param reservationIdentifier
	 * @param request
	 * @param response
	 * 
	 * @return DeskReservation
	 */
	@RequestMapping(value = "/deskreservation/identifier/{reservationIdentifier}", method = RequestMethod.GET)
	public Mono<ResponseEntity<DeskReservation>> getDeskReservationDetailsByIdentifier(@PathVariable String reservationIdentifier, ServerHttpRequest request, ServerHttpResponse response)
	{
		// get the desk reservation by identifier
		return service.findByReservationIdentifier(reservationIdentifier)
				.map(entityMapper)
				.switchIfEmpty( Mono.just(ResponseEntity.notFound().build()) );
	}
	
	/**
	 * Delete a desk reservation by the given reservation identifier.
	 * The user is allowed to delete his own reservation by using the reservation identifier.
	 * 
	 * @param reservationIdentifier
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/deskreservation/identifier/{reservationIdentifier}", method = RequestMethod.DELETE)
	public Mono<ResponseEntity<Void>> deleteByIdentifier(@PathVariable String reservationIdentifier, ServerHttpRequest request, ServerHttpResponse response)
	{
		// find reservation with the given identifier. The service returns Mono with the data
		Mono<DeskReservation> serviceDataMono = service.findByReservationIdentifier(reservationIdentifier);
		
		// create Mono with ResponseEntity. In the "map" we delete entry and return ResponseEntity with status code
		Mono<ResponseEntity<Void>> responseMono = serviceDataMono
			.flatMap(entity -> {
				if (entity == null) {
					return Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
				}
				return service.delete(entity).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
			})
			.switchIfEmpty( Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND)) );
		
		return responseMono;
	}
	
	
	@RequestMapping(value="/deskreservation", method = RequestMethod.POST)
	public Mono<ResponseEntity<DeskReservation>> addOrUpdate(@RequestBody DeskReservation entity, 
														ServerHttpRequest request,
														ServerHttpResponse response,
														@AuthenticationPrincipal Principal principal
	) {
		if (principal != null) {
			logger.info("User principal: " + principal + " ["+principal.getClass().getSimpleName()+"]");
		}
		
		// if ID is not zero then it's an update request
		boolean isUpdate = entity.getId() != null && entity.getId() > 0;
		
		// check if it is Administrator user authenticated
		boolean isAdminAuth = isAdminUserAuthenticated(principal);
		
		// we have to check if the user is authenticated with administrator role
		// only administrator is allowed to perform an update
		if ( isUpdate && !isAdminAuth ) {
			// not logged
			// response with status forbidden
			return Mono.just( new ResponseEntity<>(HttpStatus.FORBIDDEN) );
		}
		
		// Validate the data
		try {
			validate(entity, isUpdate);
		}
		catch (Exception ex) {
			logger.error("Validation failed: " + ex.getMessage(), ex);
			// response with status bad request
			return Mono.just( new ResponseEntity<>(HttpStatus.BAD_REQUEST) );
		}
		
		// save data (add or update)
		return service.addOrUpdate(entity)
		.map(e -> {
			return new ResponseEntity<>(entity, HttpStatus.OK);
		})
		// if "DeskReservationExistsException" occurs then we have to catch it and return BAD REQUEST
		// all other exceptions will be caught by the default (global) exception handler (AbstractErrorWebExceptionHandler in WebFlux)
		.onErrorReturn(DeskReservationExistsException.class, new ResponseEntity<>(HttpStatus.BAD_REQUEST));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.PUT)
	public Mono<ResponseEntity<DeskReservation>> update(@PathVariable long id,
														@RequestBody DeskReservation entity,
														ServerHttpRequest request,
														ServerHttpResponse response,
														@AuthenticationPrincipal Principal principal
	) {
		if (principal != null) {
			logger.info("User principal: " + principal + " ["+principal.getClass().getSimpleName()+"]");
		}
		
		// Validate the data
		try {
			validate(entity, true);
		}
		catch (Exception ex) {
			logger.error(ex.getMessage());
			// response with status bad request
			return Mono.just( new ResponseEntity<>(HttpStatus.BAD_REQUEST) );
		}
		
		// save data (update)
		return service.save(entity)
		.map(e -> {
			return new ResponseEntity<>(entity, HttpStatus.OK);
		});
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Mono<ResponseEntity<Void>> delete(@PathVariable long id, ServerHttpRequest request, ServerHttpResponse response)
	{
		return service.deleteById(id)
				.thenReturn(new ResponseEntity<>(HttpStatus.OK));
	}
	
	
	// ---------------------------------------------------------
	//
	// 							helpers
	//
	// ---------------------------------------------------------
	
	private final Function<DeskReservation, ResponseEntity<DeskReservation>> entityMapper = entity ->
	{
		if (entity == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<>(entity, HttpStatus.OK);
		}
	};
	
	/**
	 * It checks if the given principal (authentication) has ADMIN role.
	 * 
	 * If the HTTP request is already authenticated then Principal (Authentication) object is available via Reactive Security Context.
	 * 
	 * @param principal
	 * @return
	 */
	private boolean isAdminUserAuthenticated(Principal principal)
	{
		boolean authenticated = false;
		
		if (principal == null) {
			return false;
		}
		
		try {
			if (principal instanceof Authentication) {
				// cast Principal to Authentication
				Authentication authentication = (Authentication) principal;
				
				// get the Authorities for the authenticated user
				Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
				
				//logger.info("Authenticated User: " + authentication + " | " + authorities);
				
				boolean hasAdminRole = authorities != null && authorities.contains( new SimpleGrantedAuthority("ROLE_ADMIN") );
				
				if (hasAdminRole) {
					// the authenticated user has ROLE_ADMIN authority.
					authenticated = true;
				}
			}
		}
		catch (Exception ex) {
			logger.error("Exception while reading authorities info from the Authentication (Principal).", ex);
		}
		
		return authenticated;
	}
	
	/**
	 * Validate the given entity. If validation fails then it throws Exception.
	 * 
	 * @param entity
	 * @param isUpdate
	 */
	private void validate(DeskReservation entity, boolean isUpdate) throws Exception
	{
		Assert.notNull(entity, "The entity is null.");
		
		if (isUpdate) {
			Assert.isTrue(entity.getId() != null && entity.getId() > 0, "ID field must be greater than zero.");
		}
		else {
			Assert.isTrue(entity.getId() == null || entity.getId() == 0, "ID field must be null or zero.");
		}
		
		int alldesk = service.getAlldeskNumber();
		
		Assert.notNull(entity.getDeskNumber(), "Desk number field must not be null.");
		Assert.isTrue(entity.getDeskNumber() >= 1 && entity.getDeskNumber() <= alldesk, "Desk number field is invalid.");
		
		Assert.hasText(entity.getFirstname(), "Firstname must not be empty.");
		Assert.hasText(entity.getLastname(), "Lastname must not be empty.");
		Assert.hasText(entity.getEmail(), "Email must not be empty.");
		Assert.hasText(entity.getTelephone(), "Telephone must not be empty.");
		
		Assert.isTrue(entity.getStatus() != null && entity.getStatus() > 0, "Status field is invalid.");
		
		if (isUpdate) {
			Assert.hasText(entity.getReservationIdentifier(), "Reservation identifier must not be empty.");
		}
	}

}
