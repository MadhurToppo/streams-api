package com.madhurtoppo.streamsapi.repositories;

import com.madhurtoppo.streamsapi.entities.Order;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Repository for Orders */
@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

  List<Order> findAll();
}
