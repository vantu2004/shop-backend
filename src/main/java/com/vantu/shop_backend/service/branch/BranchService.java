package com.vantu.shop_backend.service.branch;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.vantu.shop_backend.dto.BranchDto;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Branch;
import com.vantu.shop_backend.repository.BranchRepository;
import com.vantu.shop_backend.request.BranchUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService {

	private final BranchRepository branchRepository;
	private final ModelMapper modelMapper;

	@Override
	public Branch getBranchById(Long branchId) {
		return branchRepository.findById(branchId)
				.orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));
	}

	@Override
	public List<Branch> getAllBranchs() {
		return branchRepository.findAll();
	}

	@Override
	public Branch addBranch(Branch branch) {
		try {
			return branchRepository.save(branch);
		} catch (Exception e) {
			throw new RuntimeException("Error while adding branch: " + e.getMessage(), e);
		}
	}

	@Override
	public Branch updateBranch(BranchUpdateRequest branchUpdateRequest, Long id) {
		Branch existingBranch = this.getBranchById(id);

		existingBranch.setName(branchUpdateRequest.getName());
		existingBranch.setPhoneNumber(branchUpdateRequest.getPhoneNumber());
		existingBranch.setEmail(branchUpdateRequest.getEmail());
		existingBranch.setOpeningTime(branchUpdateRequest.getOpeningTime());
		existingBranch.setIntroduce(branchUpdateRequest.getIntroduce());
		existingBranch.setStatus(branchUpdateRequest.isStatus());
		existingBranch.setLatitude(branchUpdateRequest.getLatitude());
		existingBranch.setLongitude(branchUpdateRequest.getLongitude());

		try {
			return branchRepository.save(existingBranch);
		} catch (Exception e) {
			throw new RuntimeException("Error while updating branch: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteBranchById(Long branchId) {
		this.getBranchById(branchId);

		try {
			branchRepository.deleteById(branchId);
		} catch (Exception e) {
			throw new RuntimeException("Error while deleting branch: " + e.getMessage(), e);
		}
	}

	@Override
	public List<BranchDto> getConvertedBranchs(List<Branch> branchs) {
		return branchs.stream().map(this::convertBranchEntityToBranchDto).toList();
	}

	@Override
	public BranchDto convertBranchEntityToBranchDto(Branch branch) {
		return this.modelMapper.map(branch, BranchDto.class);
	}
}
