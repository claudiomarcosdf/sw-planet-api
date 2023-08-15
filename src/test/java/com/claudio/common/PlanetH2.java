package com.claudio.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
@Table(name = "planets")
public class PlanetH2 {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private Long id;
	
	@NotEmpty
	@Column(nullable = false, unique = true)
	private String name;
	
	@NotEmpty
	@Column(nullable = false)
	private String climate;
	
	@NotEmpty
	@Column(nullable = false)
	private String terrain;
	
	public PlanetH2() {
		
	}
	
	public PlanetH2(String name, String climate, String terrain) {
		this.name = name;
		this.climate = climate;
		this.terrain = terrain;
	}	
}
