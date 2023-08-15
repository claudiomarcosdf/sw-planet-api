package com.claudio.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.claudio.web.GeneralExceptionHandler;

@Service
public class PlanetService {
	
	private PlanetRepository planetRepository;
	
	public PlanetService(PlanetRepository planetRepository) {
		this.planetRepository = planetRepository;
	}
	
	public Planet create(Planet planet) {
		return planetRepository.save(planet);
		
	}

	public Optional<Planet> get(Long id) {
		return planetRepository.findById(id);
	}

	public Optional<Planet> getByName(String name) {
 
		return planetRepository.findByName(name);
	}

	public List<Planet> list(String climate, String terrain) {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(null, climate, terrain));
		return planetRepository.findAll(query);
	}

	public void remove(Long id) {
		
		   Optional<Planet> planetFindedOptional =  planetRepository.findById(id);
		   if (planetFindedOptional.isEmpty()) {
			   new Exception();
		   }
	    	   
     	   planetRepository.deleteById(id);
	}

}
