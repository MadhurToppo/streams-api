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
	private CustomerRepository customerRepos;
	
	@Autowired
	private OrderRepository orderRepos;
	
	@Autowired
	private ProductRepository productRepos;

	@Transactional
	@Override
	public void run(String... args) throws Exception {
		log.info("Customers:");
		customerRepos.findAll()
				.forEach(c -> log.info(c.toString()));

		log.info("Orders:");
		orderRepos.findAll()
				.forEach(o -> log.info(o.toString()));

		log.info("Products:");
		productRepos.findAll()
				.forEach(p -> log.info(p.toString()));
	}

}
