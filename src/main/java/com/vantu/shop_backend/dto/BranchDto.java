package com.vantu.shop_backend.dto;

import java.util.Set;

import lombok.Data;

@Data
public class BranchDto {
	private Long id;
	private String name;
	private String phoneNumber;
	private String email;
	private String openingTime;
	private String introduce;
	private boolean status;
    private double latitude;
    private double longitude;
	private Set<ImageDto> images;
}
