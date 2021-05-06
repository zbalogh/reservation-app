package com.zbalogh.reservation.apiserver.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("databaseInitializer")
@Transactional
public class DatabaseInitializer {

	/**
	 * This method runs when the application starting up.
	 * It creates initial data in the database.
	 */
	public void createInitialData()
	{
		/*
		repository.save(new Book("Java"));
        repository.save(new Book("Node"));
        repository.save(new Book("Python"));
        */
	}
	
}
