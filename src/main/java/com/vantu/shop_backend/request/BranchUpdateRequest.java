package com.vantu.shop_backend.request;

import lombok.Data;

@Data
public class BranchUpdateRequest {
	private String name;
	private String phoneNumber;
	private String email;
	private String openingTime;
	private String introduce;
	private boolean status;
    private double latitude;
    private double longitude;
}
