package com.madhurtoppo.streamsapi.repositories;

import java.util.List;

import com.madhurtoppo.streamsapi.entities.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findAll();
}
