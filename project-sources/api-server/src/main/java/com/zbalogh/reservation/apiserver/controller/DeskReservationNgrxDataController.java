package com.zbalogh.reservation.apiserver.controller;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.zbalogh.reservation.apiserver.entities.DeskReservation;
import com.zbalogh.reservation.apiserver.resources.DeskReservationInfo;
import com.zbalogh.reservation.apiserver.services.DeskReservationService;
import com.zbalogh.reservation.apiserver.utils.AppConstants;

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
	public @ResponseBody DeskReservationInfo getInfo(HttpServletRequest request, HttpServletResponse response)
	{
		return service.getInfo();
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/deskreservations", method = RequestMethod.GET)
	public @ResponseBody List<DeskReservation> getAll(HttpServletRequest request, HttpServletResponse response)
	{
		return service.findAll();
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.GET)
	public ResponseEntity<DeskReservation> getById(@PathVariable long id, HttpServletRequest request, HttpServletResponse response)
	{
		DeskReservation entity = service.findById(id);
		
		if (entity == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(entity, HttpStatus.OK);
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
	public ResponseEntity<DeskReservation> getDeskReservationDetailsByIdentifier(@PathVariable String reservationIdentifier, HttpServletRequest request, HttpServletResponse response)
	{
		// get the desk reservation by identifier
		DeskReservation entity = service.findByReservationIdentifier(reservationIdentifier);
		
		if (entity == null) {
			// no reservation found with the given identifier
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(entity, HttpStatus.OK);
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
	public ResponseEntity<Void> deleteByIdentifier(@PathVariable String reservationIdentifier, HttpServletRequest request, HttpServletResponse response)
	{
		// get the desk reservation by identifier
		DeskReservation entity = service.findByReservationIdentifier(reservationIdentifier);
		
		if (entity == null) {
			// no reservation found with the given identifier
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		//service.deleteByReservationIdentifier(reservationIdentifier);
		service.delete(entity);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/deskreservation", method = RequestMethod.POST)
	public ResponseEntity<DeskReservation> add(@RequestBody DeskReservation entity, HttpServletRequest request, HttpServletResponse response)
	{
		boolean isUpdate = false;
		
		//
		// First of all, we need to check the following things:
		//
		// - ADD: We check if we have already another existing reservation for the same desk.
		//
		// - UPDATE: If we are updating an existing reservation, we check if the administrator user is logged in. 
		//
		if (entity.getId() == null || entity.getId() <= 0) {
			isUpdate = false;
			// ID is null or zero, so it's a creation request to insert new item into the database.
			// let's check whether we have already an item with the given desk number.
			// If so then we response an error with a status code
			DeskReservation d = service.findByDeskNumber( entity.getDeskNumber() );
			
			// we found an existing item with the same desk number
			if (d != null) {
				// response with status bad request
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			isUpdate = true;
			// if ID is not zero then it's an update request
			// in that case, we have to check if the user is authenticated with administrator role
			if ( !isAdminUserAuthenticated() ) {
				// not logged
				// response with status forbidden
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		}
		
		//
		// Validate the data
		//
		try {
			validate(entity, isUpdate);
		}
		catch (Exception ex) {
			logger.error("Validation failed: " + ex.getMessage(), ex);
			// response with status bad request
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		//
		// if we add a new reservation then we generate reservation identifier
		//
		if (!isUpdate) {
			// generate reservation identifier and set it to the new reservation entity
			String reservationId = service.generateReservationIdentifier();
			entity.setReservationIdentifier(reservationId);
		}
		
		//
		// All things are done and the entity is ready to be saved
		// Saving the entity
		//
		entity = service.save(entity);
		
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.PUT)
	public ResponseEntity<DeskReservation> update(@PathVariable long id, @RequestBody DeskReservation entity, HttpServletRequest request, HttpServletResponse response)
	{
		try {
			validate(entity, true);
		}
		catch (Exception ex) {
			logger.error(ex.getMessage());
			// response with status bad request
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		entity = service.save(entity);
		
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.DELETE)
	public @ResponseBody void delete(@PathVariable long id, HttpServletRequest request, HttpServletResponse response)
	{
		service.deleteById(id);
	}
	
	
	//
	// helpers
	//
	
	private boolean isAdminUserAuthenticated()
	{
		boolean authenticated = false;
		
		try {
			// get the authentication from the security context
			// it returns NULL if no user authenticated
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			
			if (authentication != null) {
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
			logger.error("Exception while reading authentication info from the security context.", ex);
		}
		
		return authenticated;
	}
	
	/**
	 * Validate the given entity. If validation fails then it throws Exception.
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
