package com.madhurtoppo.streamsapi.repositories;

import com.madhurtoppo.streamsapi.entities.Customer;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Repository for Customers */
@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

  List<Customer> findAll();
}
