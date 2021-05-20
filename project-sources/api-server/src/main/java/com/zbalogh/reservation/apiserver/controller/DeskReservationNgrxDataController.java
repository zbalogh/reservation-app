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
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}
	
	@RequestMapping(value="/deskreservation", method = RequestMethod.POST)
	public ResponseEntity<DeskReservation> add(@RequestBody DeskReservation entity, HttpServletRequest request, HttpServletResponse response)
	{
		if (entity.getId() == null || entity.getId() <= 0) {
			// ID is null or zero, so it's a creation request to insert new item into the database.
			// let's check whether we have already an item with the given desk number.
			// If so then we response an error with a status code
			DeskReservation d = service.findByDeskNumber( entity.getDeskNumber() );
			
			// we found an existing item with the same desk number
			if (d != null) {
				// response with status bad request
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
		else {
			// if ID is not zero then it's an update request
			// in that case, we have to check if the user is authenticated with administrator role
			if ( !isAdminUserAuthenticated() ) {
				// not logged
				// response with status forbidden
				return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
			}
		}
		
		entity = service.save(entity);
		
		return new ResponseEntity<>(entity, HttpStatus.OK);
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.PUT)
	public ResponseEntity<DeskReservation> update(@PathVariable long id, @RequestBody DeskReservation entity, HttpServletRequest request, HttpServletResponse response)
	{
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
}
