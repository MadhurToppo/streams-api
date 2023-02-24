package com.madhurtoppo.streamsapi;

import com.madhurtoppo.streamsapi.entities.Customer;
import com.madhurtoppo.streamsapi.entities.Order;
import com.madhurtoppo.streamsapi.entities.Product;
import com.madhurtoppo.streamsapi.repositories.OrderRepository;
import com.madhurtoppo.streamsapi.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@DataJpaTest
class StreamApiTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Get a list of all Products")
    void listAllProducts() {
        List<Product> products = productRepository.findAll();
        log.info("List all products");
        products.forEach(product -> log.info(product.toString()));
    }

    @Test
    @DisplayName("Get a list of all Orders")
    void listAllOrders() {
        List<Order> orders = orderRepository.findAll();
        log.info("List all orders");
        orders.forEach(order -> log.info(order.toString()));
    }

    @Test
    @DisplayName("Get a list of products with category = \"Books\" and price > 100")
    void exercise1() {
        long startTime = System.currentTimeMillis();
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .filter(product -> product.getPrice() > 100)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 1 - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(p -> log.info(p.toString()));
    }

    @Test
    @DisplayName("Get a list of products with category = \"Books\" and price > 100 (using Predicate chaining for filter)")
    void exercise1a() {
        Predicate<Product> hasBooks = product -> product.getCategory().equalsIgnoreCase("Books");
        Predicate<Product> isPriceAbove100 = product -> product.getPrice() > 100;
        long startTime = System.currentTimeMillis();
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(product -> hasBooks.and(isPriceAbove100).test(product))
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 1a - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(product -> log.info(product.toString()));
    }

    @Test
    @DisplayName("Get a list of products with category = \"Books\" and price > 100 (using BiPredicate for filter)")
    void exercise1b() {
        BiPredicate<Product, String> hasCategory = (product, category) -> product.getCategory()
                .equalsIgnoreCase(category);
        long startTime = System.currentTimeMillis();
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(product -> hasCategory.test(product, "Books") && product.getPrice() > 100)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 1b - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(p -> log.info(p.toString()));
    }

    @Test
    @DisplayName("Get a list of orders with product category = \"Baby\"")
    void exercise2() {
        long startTime = System.currentTimeMillis();
        List<Order> orders = orderRepository.findAll()
                .stream()
                .filter(o -> o.getProducts().stream().anyMatch(p -> p.getCategory().equalsIgnoreCase("Baby")))
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 2 - execution time: %1$d ms", (endTime - startTime)));
        orders.forEach(o -> log.info(o.toString()));
    }

    @Test
    @DisplayName("Get a list of products with category = “Toys” and then apply 10% discount\"")
    void exercise3() {
        long startTime = System.currentTimeMillis();
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Toys"))
                .map(product -> product.withPrice(product.getPrice() * 0.9))
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 3 - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(o -> log.info(o.toString()));
    }

    @Test
    @DisplayName("Get a list of products by tier 2 customers between 01-Feb-2021 and 01-Apr-2021")
    void exercise4() {
        long startTime = System.currentTimeMillis();
        List<Product> products = orderRepository.findAll()
                .stream()
                .filter(order -> order.getCustomer().getTier() == 2)
                .filter(order -> !order.getOrderDate().isBefore(LocalDate.of(2021, 2, 1)))
                .filter(order -> !order.getOrderDate().isAfter(LocalDate.of(2021, 4, 1)))
                .flatMap(order -> order.getProducts().stream())
                .peek(System.out::println)
                .distinct()
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 4 - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(product -> log.info(product.toString()));
    }

    @Test
    @DisplayName("Get the cheapest product in \"Books\" category")
    void exercise5() {
        long startTime = System.currentTimeMillis();
        Optional<Product> product = productRepository.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
                .min(Comparator.comparing(Product::getPrice));
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 5 - execution time: %1$d ms", (endTime - startTime)));
        log.info(String.valueOf(product));
    }

    @Test
    @DisplayName("Get the top 3 cheapest products in \"Books\" category")
    void exercise5a() {
        long startTime = System.currentTimeMillis();
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .peek(System.out::println)
                .sorted(Comparator.comparing(Product::getPrice))
                .limit(3)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 5 - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(product -> log.info(product.toString()));
    }

    @Test
    @DisplayName("Get the top 3 expensive products in \"Books\" category")
    void exercise5b() {
        long startTime = System.currentTimeMillis();
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .limit(3)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 5 - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(product -> log.info(product.toString()));
    }

    @Test
    @DisplayName("Get the 3 most recently placed orders")
    void exercise6() {
        long startTime = System.currentTimeMillis();
        List<Order> result = orderRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 6 - execution time: %1$d ms", (endTime - startTime)));
        result.forEach(o -> log.info(o.toString()));
    }

    @Test
    @DisplayName("Get a list of products ordered on 15-Mar-2021")
    void exercise7() {
        long startTime = System.currentTimeMillis();
        List<Product> products = orderRepository.findAll()
                .stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 7 - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(product -> log.info(product.toString()));
    }

    @Test
    @DisplayName("Get the total price of all orders placed in Feb 2021")
    void exercise8() {
        long startTime = System.currentTimeMillis();
        double total = orderRepository.findAll()
                .stream()
                .filter(o -> !o.getOrderDate().isBefore(LocalDate.of(2021, 2, 1)))
                .filter(o -> o.getOrderDate().isBefore(LocalDate.of(2021, 3, 1)))
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 8 - execution time: %1$d ms", (endTime - startTime)));
        log.info("Total lump sum = " + total);
    }

    @Test
    @DisplayName("Get the total lump of all orders placed in Feb 2021 (using reduce with BiFunction)")
    void exercise8a() {
        BiFunction<Double, Product, Double> accumulator = (acc, product) -> acc + product.getPrice();

        long startTime = System.currentTimeMillis();
        double total = orderRepository.findAll()
                .stream()
                .filter(o -> !o.getOrderDate().isBefore(LocalDate.of(2021, 2, 1)))
                .filter(o -> o.getOrderDate().isBefore(LocalDate.of(2021, 3, 1)))
                .flatMap(o -> o.getProducts().stream())
                .reduce(0D, accumulator, Double::sum);

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 8a - execution time: %1$d ms", (endTime - startTime)));
        log.info("Total lump sum = " + total);
    }

    @Test
    @DisplayName("Get the average price of all orders placed on 15-Mar-2021")
    void exercise9() {
        long startTime = System.currentTimeMillis();
        double average = orderRepository.findAll()
                .stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .flatMap(order -> order.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .average()
                .getAsDouble();

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 9 - execution time: %1$d ms", (endTime - startTime)));
        log.info("Average = " + average);
    }

    @Test
    @DisplayName("Get statistics summary of all products in \"Books\" category")
    void exercise10() {
        long startTime = System.currentTimeMillis();
        DoubleSummaryStatistics statistics = productRepository.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 10 - execution time: %1$d ms", (endTime - startTime)));
        log.info(String.format("count = %1$d, average = %2$f, max = %3$f, min = %4$f, sum = %5$f",
                statistics.getCount(),
                statistics.getAverage(),
                statistics.getMax(),
                statistics.getMin(),
                statistics.getSum()));
    }

    @Test
    @DisplayName("Get a map of order id and the order's product count")
    void exercise11() {
        long startTime = System.currentTimeMillis();
        Map<Long, Integer> orderIdToProductsCount = orderRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Order::getId, order -> order.getProducts().size()));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 11 - execution time: %1$d ms", (endTime - startTime)));
        log.info(orderIdToProductsCount.toString());
    }

    @Test
    @DisplayName("Get a data map of customer and orders list")
    void exercise12() {
        long startTime = System.currentTimeMillis();
        Map<Customer, List<Order>> customerToOrders = orderRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 12 - execution time: %1$d ms", (endTime - startTime)));
        log.info(customerToOrders.toString());
    }

    @Test
    @DisplayName("Obtain a data map of customer_id and list of order_id(s)")
    void exercise12a() {
        long startTime = System.currentTimeMillis();
        HashMap<Long, List<Long>> result = orderRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(order -> order.getCustomer().getId(),
                        HashMap::new,
                        Collectors.mapping(Order::getId, Collectors.toList())));
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 12a - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Obtain a data map with order and its total price")
    void exercise13() {
        long startTime = System.currentTimeMillis();
        Map<Order, Double> result = orderRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Function.identity(),
                        order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum()));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 13 - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Obtain a data map with order and its total price (using reduce)")
    void exercise13a() {
        long startTime = System.currentTimeMillis();
        Map<Long, Double> result = orderRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Order::getId,
                        order -> order.getProducts()
                                .stream()
                                .reduce(0D, (acc, product) -> acc + product.getPrice(), Double::sum)));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 13a - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Obtain a data map of product name by category")
    void exercise14() {
        long startTime = System.currentTimeMillis();
        Map<String, List<String>> result = productRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.mapping(Product::getName, Collectors.toList())));


        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 14 - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Get the most expensive product per category")
    void exercise15() {
        long startTime = System.currentTimeMillis();
        Map<String, Optional<Product>> result = productRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.maxBy(Comparator.comparing(Product::getPrice))));
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 15 - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Get the most expensive product (by name) per category")
    void exercise15a() {
        long startTime = System.currentTimeMillis();
        Map<String, String> result = productRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparingDouble(Product::getPrice)),
                                optionalProduct -> optionalProduct.map(Product::getName).orElse(null))));
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 15a - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }
}