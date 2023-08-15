package com.claudio.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.claudio.domain.Planet;
import com.claudio.domain.PlanetService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/planets")
public class PlanetController {
	@Autowired
	private PlanetService planetService;
	
	@PostMapping
	public ResponseEntity<Planet> create(@RequestBody @Valid Planet planet) {
		
		Planet planetCreated = planetService.create(planet);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(planetCreated);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Planet> find(@PathVariable Long id) {
		
		return planetService.get(id).map(planet -> ResponseEntity.ok(planet))
				.orElseGet(() -> ResponseEntity.notFound().build());
		
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<Planet> findName(@PathVariable String name) { 
		
		return planetService.getByName(name).map(planet -> ResponseEntity.ok(planet))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping
	public ResponseEntity<List<Planet>> list(@RequestParam(required = false) String terrain, 
			@RequestParam(required = false) String climate){
		
		List<Planet> planets = planetService.list(climate, terrain);
		
		return ResponseEntity.ok(planets);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remove(@PathVariable Long id) {
		
		planetService.remove(id);
		return ResponseEntity.noContent().build();
	}
	

}
