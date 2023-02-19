package com.madhurtoppo.streamsapi;

import com.madhurtoppo.streamsapi.repositories.CustomerRepository;
import com.madhurtoppo.streamsapi.repositories.OrderRepository;
import com.madhurtoppo.streamsapi.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
public class AppCommandRunner implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        log.info("Customers:");
        customerRepository.findAll().forEach(customer -> log.info(customer.toString()));

        log.info("Orders:");
        orderRepository.findAll().forEach(order -> log.info(order.toString()));

        log.info("Products:");
        productRepository.findAll().forEach(product -> log.info(product.toString()));
    }

}
