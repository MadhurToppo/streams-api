package com.madhurtoppo.streamsapi.repositories;

import com.madhurtoppo.streamsapi.entities.Product;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Repository for products */
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

  List<Product> findAll();
}
