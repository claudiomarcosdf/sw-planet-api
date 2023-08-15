package com.claudio.web;

import static com.claudio.common.PlanetConstants.PLANET;
import static com.claudio.common.PlanetConstants.TATOOINE;
import static com.claudio.common.PlanetConstants.PLANETS;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.claudio.domain.Planet;
import com.claudio.domain.PlanetService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Interação com contexto web
 */
@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {
	@MockBean //usado com os controllers
	private PlanetService planetService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void createPlanet_WithValidData_ReturnsCreated() throws Exception {
		when(planetService.create(PLANET)).thenReturn(PLANET);
		
		mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(PLANET)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$").value(PLANET)); // O $ é o raiz do obj JSON  
	}
	
	@Test
	public void createPlanet_WithInvalidData_ReturnsBadRequest() throws Exception {
		Planet emptyPlanet = new Planet(); //com todos os atributos nulos
		Planet invalidPlanet = new Planet("", "", "");		
	
		mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(emptyPlanet)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnprocessableEntity());
		
		mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(invalidPlanet)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnprocessableEntity());		
	}
	
	@Test
	public void createPlanet_WithExistingName_ReturnConflict() throws Exception {
		when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);
		
		mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(PLANET)).contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict());	
	}
	
	@Test
	public void getPlanet_ByExistingId_ReturunsPlanet() throws Exception {
		when(planetService.get(1L)).thenReturn(Optional.of(PLANET));
		
		mockMvc.perform(get("/planets/1"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").value(PLANET));
	}
	
	@Test
	public void getPlanet_ByuBExistingId_ReturunsNotFound() throws Exception {
		mockMvc.perform(get("/planets/1"))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void getPlanet_ByExistingName_ReturunsPlanet() throws Exception {
		when(planetService.getByName(PLANET.getName())).thenReturn(Optional.of(PLANET));
		
		mockMvc.perform(get("/planets/name/" + PLANET.getName()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").value(PLANET));
	}
	
	@Test
	public void getPlanet_ByuBExistingName_ReturunsNotFound() throws Exception {
		mockMvc.perform(get("/planets/name/planetaW"))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void listPlanets_ReturnsFilteredPlanets() throws Exception {
		when(planetService.list(null, null)).thenReturn(PLANETS);
		when(planetService.list(TATOOINE.getClimate(), TATOOINE.getTerrain())).thenReturn(List.of(TATOOINE));
		
		mockMvc
		.perform(get("/planets"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(3)));
		
		mockMvc
		.perform(get("/planets?" + String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate())))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0]").value(TATOOINE));		
	}
	
	@Test
	public void listPlanets_ReturnsNoPlanets() throws Exception {
		when(planetService.list(null, null)).thenReturn(Collections.emptyList());

		mockMvc
		.perform(get("/planets"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));	
	}
}