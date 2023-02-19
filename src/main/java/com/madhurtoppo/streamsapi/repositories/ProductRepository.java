package com.madhurtoppo.streamsapi.repositories;

import java.util.List;

import com.madhurtoppo.streamsapi.entities.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    List<Product> findAll();
}
