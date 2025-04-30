package com.vantu.shop_backend.service.branch;

import java.util.List;

import com.vantu.shop_backend.dto.BranchDto;
import com.vantu.shop_backend.model.Branch;
import com.vantu.shop_backend.request.BranchUpdateRequest;

public interface IBranchService {
	Branch getBranchById(Long branchId);

	List<Branch> getAllBranchs();

	Branch addBranch(Branch branch);

	Branch updateBranch(BranchUpdateRequest branchUpdateRequest, Long id);

	void deleteBranchById(Long branchId);

	List<BranchDto> getConvertedBranchs(List<Branch> branchs);

	BranchDto convertBranchEntityToBranchDto(Branch branch);
}
