package com.madhurtoppo.streamsapi.repositories;

import java.util.List;

import com.madhurtoppo.streamsapi.entities.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findAll();
}
