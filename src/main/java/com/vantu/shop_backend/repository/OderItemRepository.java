package com.vantu.shop_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vantu.shop_backend.model.OrderItem;

@Repository
public interface OderItemRepository extends JpaRepository<OrderItem, Long> {

}
