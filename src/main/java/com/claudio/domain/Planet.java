package com.claudio.domain;

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
public class Planet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	
	public Planet() {
		
	}
	
	public Planet(String name, String climate, String terrain) {
		this.name = name;
		this.climate = climate;
		this.terrain = terrain;
	}
	
	public Planet(Long id, String name, String climate, String terrain) {
		this.id = id;
		this.name = name;
		this.climate = climate;
		this.terrain = terrain;
	}	

}
