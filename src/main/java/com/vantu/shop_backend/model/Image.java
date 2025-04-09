package com.vantu.shop_backend.model;

import java.sql.Blob;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String type;

	// làm việc với dữ liệu lớn (Lob - largeObject, Blob - binaryLargeObject)
	/*
	 * muốn truyền dữ liệu vào image để lưu dưới kiểu Blob thì buộc phải dùng
	 * SerialBlob() (nhận vào mảng byte[] từ multipartFile)
	 */
	@Lob
	private Blob image;
	private String downloadUrl;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
}
