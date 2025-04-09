package com.vantu.shop_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vantu.shop_backend.enums.OwnerType;
import com.vantu.shop_backend.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

	@Query("""
			    SELECT i FROM Image i
			    WHERE i.ownerType = :ownerType
			    AND (
			        (i.ownerType = ownerType AND i.product.id = ?1)
			        OR (i.ownerType = ownerType AND i.user.id = ?1)
			        OR (i.ownerType = ownerType AND i.category.id = ?1)
			    )
			""")
	List<Image> findByOwnerIdAndOwnerType(@Param("ownerId") Long ownerId, @Param("ownerType") OwnerType ownerType);

}
