package com.vantu.shop_backend.service.image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.vantu.shop_backend.dto.ImageDto;
import com.vantu.shop_backend.enums.OwnerType;
import com.vantu.shop_backend.model.Image;

public interface IImageService {
	Image getImageById(Long id);

	void deleteImageById(Long id);

	List<ImageDto> saveImage(List<MultipartFile> multipartFiles, Long ownerId, OwnerType ownerType);

	void updateImage(MultipartFile multipartFile, Long imageId);
}
