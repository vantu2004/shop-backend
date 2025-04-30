package com.vantu.shop_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vantu.shop_backend.dto.BranchDto;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Branch;
import com.vantu.shop_backend.request.BranchUpdateRequest;
import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.service.branch.IBranchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/branches")
@RequiredArgsConstructor
public class BranchController {

	private final IBranchService branchService;

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse> getBranchById(@PathVariable Long id) {
		try {
			Branch branch = branchService.getBranchById(id);
			BranchDto branchDto = branchService.convertBranchEntityToBranchDto(branch);

			return ResponseEntity.ok(new ApiResponse("Success!", branchDto));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@GetMapping("/all")
	public ResponseEntity<ApiResponse> getAllBranches() {
		List<Branch> branches = branchService.getAllBranchs();
		List<BranchDto> branchDtos = branchService.getConvertedBranchs(branches);

		return ResponseEntity.ok(new ApiResponse("Success!", branchDtos));
	}

	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addBranch(@RequestBody Branch branch) {
		try {
			Branch savedBranch = branchService.addBranch(branch);
			BranchDto savedBranchDto = branchService.convertBranchEntityToBranchDto(savedBranch);

			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new ApiResponse("Branch created successfully!", savedBranchDto));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse> updateBranch(@RequestBody BranchUpdateRequest updateRequest,
			@PathVariable Long id) {
		try {
			Branch updatedBranch = branchService.updateBranch(updateRequest, id);
			BranchDto updaBranchDto = branchService.convertBranchEntityToBranchDto(updatedBranch);

			return ResponseEntity.ok(new ApiResponse("Branch updated successfully!", updaBranchDto));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse> deleteBranch(@PathVariable Long id) {
		try {
			branchService.deleteBranchById(id);
			return ResponseEntity.ok(new ApiResponse("Branch deleted successfully!", null));
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
		}
	}
}
