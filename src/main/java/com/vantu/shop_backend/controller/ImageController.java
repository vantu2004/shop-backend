package com.vantu.shop_backend.controller;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vantu.shop_backend.dto.ImageDto;
import com.vantu.shop_backend.exceptions.ResourceNotFoundException;
import com.vantu.shop_backend.model.Image;
import com.vantu.shop_backend.response.ApiResponse;
import com.vantu.shop_backend.service.image.IImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/images")
@RequiredArgsConstructor
public class ImageController {
	private final IImageService iImageService;

	@PostMapping("/add")
	public ResponseEntity<ApiResponse> saveImages(@RequestParam List<MultipartFile> multipartFiles,
			@RequestParam Long productId) {
		try {
			List<ImageDto> imageDtos = this.iImageService.saveImage(multipartFiles, productId);
			return ResponseEntity.ok(new ApiResponse("Upload Success!", imageDtos));
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse("Upload Failed!", e.getMessage()));
		}
	}

	@GetMapping("/image/download/{imageId}")
	public ResponseEntity<?> downloadImage(@PathVariable Long imageId) {
		try {
			// Lấy đối tượng ảnh từ database theo imageId
			Image image = iImageService.getImageById(imageId);

			/*
			 * Chuyển dữ liệu BLOB thành mảng byte (khi lưu là chuyển từ multipartFile về
			 * mảng byte rồi dùng SerialBlob() để convert lần nữa). getBytes() lấy một phần
			 * hoặc toàn bộ dữ liệu của đối tượng BLOB dưới dạng mảng byte (byte[]). Mặc
			 * định trong SQL mảng bắt đầu từ 1 (0 sẽ ném SQLException) --> nghĩa là lấy
			 * mảng byte của BLOB từ vị trí 1 đến hết
			 */
			ByteArrayResource resource = new ByteArrayResource(
					image.getImage().getBytes(1, (int) image.getImage().length()));

			// Trả về ảnh dưới dạng file download
			return ResponseEntity.ok()
					// Đặt kiểu file (image/png, image/jpeg, ...)
					.contentType(MediaType.parseMediaType(image.getType()))
					// Đặt tên file tải về
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getName() + "\"")
					// Trả về dữ liệu file
					.body(resource);
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PutMapping("/image/{imageId}/update")
	public ResponseEntity<ApiResponse> updateImage(@RequestBody MultipartFile multipartFile,
			@PathVariable long imageId) {
		try {
			Image image = this.iImageService.getImageById(imageId);
			if (image != null && !multipartFile.isEmpty()) {
				this.iImageService.updateImage(multipartFile, imageId);
				return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Update Success!", null));
			}
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiResponse("Update Failed!", HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@DeleteMapping("/image/{imageId}/delete")
	public ResponseEntity<ApiResponse> deleteImage(@PathVariable long imageId) {
		try {
			Image image = this.iImageService.getImageById(imageId);
			if (image != null) {
				this.iImageService.deleteImageById(imageId);
				return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Delete Success!", null));
			}
		} catch (ResourceNotFoundException e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiResponse("Delete Failed!", HttpStatus.INTERNAL_SERVER_ERROR));
	}
}
