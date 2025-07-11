package com.example.barogo.repository;

import com.example.barogo.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId_UserIdAndDeliveryDateGreaterThanEqualAndDeliveryDateLessThan(
            String userId, Date fromDate, Date toDate);
}
