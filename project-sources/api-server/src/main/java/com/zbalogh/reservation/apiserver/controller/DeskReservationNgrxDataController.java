package com.zbalogh.reservation.apiserver.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
public class DeskReservationNgrxDataController {

	@Autowired
	private DeskReservationService service;
	
	@RequestMapping(value="/deskreservation/getinfo", method = RequestMethod.GET)
	public @ResponseBody DeskReservationInfo getInfo(HttpServletRequest request, HttpServletResponse response)
	{
		return service.getInfo();
	}

	@RequestMapping(value="/deskreservations", method = RequestMethod.GET)
	public @ResponseBody List<DeskReservation> getAll(HttpServletRequest request, HttpServletResponse response)
	{
		return service.findAll();
	}
	
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.GET)
	public @ResponseBody DeskReservation getById(@PathVariable long id, HttpServletRequest request, HttpServletResponse response)
	{
		return service.findById(id);
	}
	
	@RequestMapping(value="/deskreservation", method = RequestMethod.POST)
	public @ResponseBody DeskReservation add(@RequestBody DeskReservation entity, HttpServletRequest request, HttpServletResponse response)
	{
		entity = service.save(entity);
		
		return entity;
	}
	
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.PUT)
	public @ResponseBody DeskReservation update(@PathVariable long id, @RequestBody DeskReservation entity, HttpServletRequest request, HttpServletResponse response)
	{
		entity = service.save(entity);
		
		return entity;
	}
	
	@RequestMapping(value="/deskreservation/{id}", method = RequestMethod.DELETE)
	public @ResponseBody void delete(@PathVariable long id, HttpServletRequest request, HttpServletResponse response)
	{
		service.deleteById(id);
	}
	
}
