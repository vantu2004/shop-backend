package com.vantu.shop_backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;

	/*
	 * chọn JsonIgnore bên này vì bên Product là bên nhiều nên khi lưu là lưu
	 * categoryId bên đó và chỉ quan tâm Product thuộc Category nào, còn bên này thì
	 * ko cần
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "category")
	List<Product> products;
}
