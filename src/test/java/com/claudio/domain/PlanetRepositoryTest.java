package com.claudio.domain;
import static com.claudio.common.PlanetConstants.PLANET;
import static com.claudio.common.PlanetConstants.TATOOINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

/**
 * TESTES DE INTEGRAÇÃO
 */

//@SpringBootTest(classes = PlanetRepository.class) -> Não precisa, pois o @DataJpaTest já carrega os componentes

//Configura automaticamente um banco fake baseado no original com H2 (é necessário adicionar o H2 no pom.xml)
@DataJpaTest
public class PlanetRepositoryTest {
	@Autowired
	private PlanetRepository planetRepository;
	
	@Autowired
	private TestEntityManager testEntityManager;
	
	@AfterEach
	public void afterEach() {
		PLANET.setId(null);
	}
	
	@Test
	public void createPlanet_WithValidDate_ReturnsPlanet() {
		Planet planet = planetRepository.save(PLANET);
		
		//Não pode usar o repositório para fazer a busca do planeta p/ depois comparar - usar TestEntityManager
		Planet sut = testEntityManager.find(Planet.class, planet.getId());
		
		//System.err.println(planet.toString());
		
		assertThat(sut).isNotNull();
		assertThat(sut.getName()).isEqualTo(PLANET.getName());
		assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
		assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());
		
	
	}
	
	/**
	 * Caso dados não íntegros, o repositório vai lançar uma exceção
	 */
	@Test
	public void createPlanet_WithInvalidData_ThrowException() {
		Planet emptyPlanet = new Planet(); //com todos os atributos nulos
		Planet invalidPlanet = new Planet("", "", "");
		
		assertThatThrownBy(() -> planetRepository.save(emptyPlanet)).isInstanceOf(RuntimeException.class);
		assertThatThrownBy(() ->planetRepository.save(invalidPlanet)).isInstanceOf(RuntimeException.class);
	}

	@Test
	public void createPlanet_WithExistingName_ThrowException() {
	
		/*
		 * Ao usar o Planet do Postgresql, a estrategia IDENTITY não funcionou com o H2, 
		 * por isso criei o PlanetH2 com estrategia AUTO
		 */
		//PlanetH2 planetToSave = new PlanetH2("name", "climate", "terrain");
		//PlanetH2 planetH2 = testEntityManager.persistFlushFind(planetToSave);
		
		//testEntityManager.detach(planetH2); //desassociar o planet do entityManager, senão o teste não passa
		//planetH2.setId(null);
		
		//Planet planet = new Planet();
		//planet.setClimate(planetH2.getClimate());
		//planet.setTerrain(planetH2.getTerrain());
		//planet.setName(planetH2.getName());
		
		//PLANET inicia sem o id, então o entityManager ao salvar seta o Id nele 
		Planet planet = testEntityManager.persistFlushFind(PLANET);
		testEntityManager.detach(planet);
		planet.setId(null);
		
		//act
		
		assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);
	}
	
	@Test
	public void getPlanet_ByExistingId_ReturunsPlanet() throws Exception {
		Planet planet = testEntityManager.persistFlushFind(PLANET);
		
		Optional<Planet> planetOpt = planetRepository.findById(planet.getId());
		
		assertThat(planetOpt).isNotEmpty();
		assertThat(planetOpt.get()).isEqualTo(planet);
	}
	
	@Test
	public void getPlanet_ByuBExistingId_ReturunsNotFound() throws Exception {
		Optional<Planet> planetOpt = planetRepository.findById(1L);
		
		assertThat(planetOpt).isEmpty();
	}	
	
	@Test
	public void getPlanet_ByExistingName_ReturunsPlanet() throws Exception {
		Planet planet = testEntityManager.persistFlushFind(PLANET);
		
		Optional<Planet> planetOpt = planetRepository.findByName(planet.getName());
		
		assertThat(planetOpt).isNotEmpty();
		assertThat(planetOpt.get()).isEqualTo(planet);
	}
	
	@Test
	public void getPlanet_ByuBExistingName_ReturunsNotFound() throws Exception {
		Optional<Planet> planetOpt = planetRepository.findByName("name");
		
		assertThat(planetOpt).isEmpty();
	}
	
	@Sql(scripts = "/import_planets.sql")
	@Test
	public void listPlanets_ReturnsFilteredPlanets() {
		Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
		Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(null, TATOOINE.getClimate(), TATOOINE.getTerrain()));
		
		List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
		List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);
		
		assertThat(responseWithoutFilters).isNotEmpty();
		assertThat(responseWithoutFilters).hasSize(3);
		assertThat(responseWithFilters).isNotEmpty();
		assertThat(responseWithFilters).hasSize(1);
		assertThat(responseWithFilters.get(0)).isEqualTo(TATOOINE);
	}
	
	@Test
	public void listPlanets_ReturnsNoPlanets() {
		Example<Planet> query = QueryBuilder.makeQuery(new Planet());
		
		List<Planet> response = planetRepository.findAll(query);
		
		assertThat(response).isEmpty();
	}
	
	@Test
	public void removePlanet_WithExistingId_RemovesPlanetFromDatabase() {
		Planet planet = testEntityManager.persistFlushFind(PLANET); //salva o planeta no H2
		
		planetRepository.deleteById(planet.getId()); //delete o planeta no H2
		
		Planet planetNotFound = testEntityManager.find(Planet.class, planet.getId());
		
		assertThat(planetNotFound).isNull();
		
	}
	
	@Test
	public void removePlanet_WithUnexistingId_ThrowsException() {
		Planet planet = testEntityManager.persistFlushFind(PLANET);
		
		Planet planetNotFound = testEntityManager.find(Planet.class, 99L);
		
		assertThrows(EmptyResultDataAccessException.class, () -> planetRepository.deleteById(99L));
		
		
	}	
}
