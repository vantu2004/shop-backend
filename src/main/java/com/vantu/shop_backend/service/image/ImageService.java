package com.vantu.shop_backend.service.image;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vantu.shop_backend.dto.ImageDto;
import com.vantu.shop_backend.enums.OwnerType;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Image;
import com.vantu.shop_backend.repository.ImageRepository;
import com.vantu.shop_backend.service.branch.IBranchService;
import com.vantu.shop_backend.service.category.ICategoryService;
import com.vantu.shop_backend.service.product.IProductService;
import com.vantu.shop_backend.service.user.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

	private final static String PREFIX_DOWNLOAD_URL = "/api/v1/images/image/download/";

	private final ImageRepository imageRepository;
	private final IProductService iProductService;
	private final IUserService iUserService;
	private final ICategoryService iCategoryService;
	private final IBranchService iBranchService;

	@Override
	public Image getImageById(Long id) {
		// TODO Auto-generated method stub
		return this.imageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No image found with id " + id));
	}

	@Override
	public void deleteImageById(Long id) {
		// TODO Auto-generated method stub
		this.imageRepository.findById(id).ifPresentOrElse(this.imageRepository::delete, () -> {
			throw new ResourceNotFoundException("No image found with id " + id);
		});
	}

	@Override
	public List<ImageDto> saveImage(List<MultipartFile> multipartFiles, Long ownerId, OwnerType ownerType) {
		// TODO Auto-generated method stub
		List<ImageDto> imageDtos = new ArrayList<ImageDto>();
		for (MultipartFile multipartFile : multipartFiles) {
			try {
				Image image = new Image();
				image.setName(multipartFile.getOriginalFilename());
				image.setType(multipartFile.getContentType());
				image.setImage(new SerialBlob(multipartFile.getBytes()));

				setImageOwner(image, ownerId, ownerType);

				image.setOwnerType(ownerType);

				String downloadUrl = PREFIX_DOWNLOAD_URL + image.getId();
				image.setDownloadUrl(downloadUrl);

				Image savedImage = this.imageRepository.save(image);
				savedImage.setDownloadUrl(PREFIX_DOWNLOAD_URL + savedImage.getId());
				this.imageRepository.save(savedImage);

				ImageDto imageDto = new ImageDto();
				imageDto.setId(savedImage.getId());
				imageDto.setName(savedImage.getName());
				imageDto.setDownloadUrl(savedImage.getDownloadUrl());

				imageDtos.add(imageDto);

			} catch (IOException | SQLException e) {
				// TODO: handle exception
				throw new RuntimeException(e.getMessage());
			}
		}

		return imageDtos;
	}

	private void setImageOwner(Image image, Long ownerId, OwnerType ownerType) {
		// TODO Auto-generated method stub
		switch (ownerType) {
		case PRODUCT:
			image.setProduct(this.iProductService.getProductById(ownerId));
			break;
		case USER:
			image.setUser(this.iUserService.getUserById(ownerId));
			break;
		case CATEGORY:
			image.setCategory(this.iCategoryService.getCategoryById(ownerId));
			break;
		case BRANCH:
			image.setBranch(this.iBranchService.getBranchById(ownerId));
			break;
		default:
			throw new IllegalArgumentException("Unsupported OwnerType: " + ownerType);
		}
	}

	@Override
	public void updateImage(MultipartFile multipartFile, Long imageId) {
		// TODO Auto-generated method stub
		Image image = getImageById(imageId);
		try {
			image.setName(multipartFile.getOriginalFilename());
			image.setType(multipartFile.getContentType());
			image.setImage(new SerialBlob(multipartFile.getBytes()));

			this.imageRepository.save(image);
		} catch (IOException | SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
