package com.vantu.shop_backend.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Branch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String phoneNumber;
	private String email;
	private String openingTime;
	
	@Column(length = 1024)
	private String introduce;
	
	private boolean status;
	
    // Tọa độ địa lý
    private double latitude;
    private double longitude;
    
	@OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<Image> images = new HashSet<>();
}
