package com.madhurtoppo.streamsapi;

import com.madhurtoppo.streamsapi.repositories.CustomerRepository;
import com.madhurtoppo.streamsapi.repositories.OrderRepository;
import com.madhurtoppo.streamsapi.repositories.ProductRepository;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AppCommandRunner implements CommandLineRunner {

  private final CustomerRepository customerRepository;

  private final OrderRepository orderRepository;

  private final ProductRepository productRepository;

  @Transactional
  @Override
  public void run(final String... args) throws Exception {
    log.info("Customers:");
    customerRepository.findAll().forEach(customer -> log.info(customer.toString()));

    log.info("Orders:");
    orderRepository.findAll().forEach(order -> log.info(order.toString()));

    log.info("Products:");
    productRepository.findAll().forEach(product -> log.info(product.toString()));
  }
}
