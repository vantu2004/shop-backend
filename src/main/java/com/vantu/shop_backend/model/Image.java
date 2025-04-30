package com.vantu.shop_backend.model;

import java.sql.Blob;

import com.vantu.shop_backend.enums.OwnerType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
	
	@ManyToOne
	@JoinColumn(name = "branch_id")
	private Branch branch;

	@OneToOne
	@JoinColumn(name = "user_id", unique = true)
	private User user;

	@OneToOne
	@JoinColumn(name = "category_id", unique = true)
	private Category category;

	@Enumerated(EnumType.STRING)
	@Column(name = "owner_type")
	private OwnerType ownerType;
}
