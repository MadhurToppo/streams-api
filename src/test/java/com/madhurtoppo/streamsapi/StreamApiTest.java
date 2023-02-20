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
public class StreamApiTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("List all Products")
    public void listAllProducts() {
        List<Product> products = productRepository.findAll();
        log.info("List all products");
        products.forEach(product -> log.info(product.toString()));
    }

    @Test
    @DisplayName("List all Orders")
    public void listAllOrders() {
        List<Order> orders = orderRepository.findAll();
        log.info("List all orders");
        orders.forEach(order -> log.info(order.toString()));
    }

    @Test
    @DisplayName("Obtain a list of product with category = \"Books\" and price > 100")
    public void exercise1() {
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
    @DisplayName("Obtain a list of product with category = \"Books\" and price > 100 (using Predicate chaining for filter)")
    public void exercise1a() {
        Predicate<Product> hasBooks = product -> product.getCategory().equalsIgnoreCase("Books");
        Predicate<Product> priceAbove100 = product -> product.getPrice() > 100;
        long startTime = System.currentTimeMillis();
        List<Product> products = productRepository.findAll()
                .stream()
                .filter(product -> hasBooks.and(priceAbove100).test(product))
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 1a - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(product -> log.info(product.toString()));
    }

    @Test
    @DisplayName("Obtain a list of product with category = \"Books\" and price > 100 (using BiPredicate for filter)")
    public void exercise1b() {
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
    @DisplayName("Obtain a list of order with product category = \"Baby\"")
    public void exercise2() {
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
    @DisplayName("Obtain a list of product with category = “Toys” and then apply 10% discount\"")
    public void exercise3() {
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
    @DisplayName("Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021")
    public void exercise4() {
        long startTime = System.currentTimeMillis();
        List<Product> products = orderRepository.findAll()
                .stream()
                .filter(order -> order.getCustomer().getTier() == 2)
                .filter(order -> order.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0)
                .filter(order -> order.getOrderDate().compareTo(LocalDate.of(2021, 4, 1)) <= 0)
                .flatMap(order -> order.getProducts().stream())
                .peek(product -> System.out.println(product))
                .distinct()
                .collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 4 - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(product -> log.info(product.toString()));
    }

    @Test
    @DisplayName("Get the 3 cheapest products of \"Books\" category")
    public void exercise5() {
        long startTime = System.currentTimeMillis();
        Optional<Product> result = productRepository.findAll()
                .stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("Books"))
                .min(Comparator.comparing(Product::getPrice));
        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 5 - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.get().toString());
    }

    @Test
    @DisplayName("Get the 3 most recent placed order")
    public void exercise6() {
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
    @DisplayName("Get a list of products which were ordered on 15-Mar-2021")
    public void exercise7() {
        long startTime = System.currentTimeMillis();
        List<Product> products = orderRepository.findAll()
                .stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .peek(order -> System.out.println(order))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 7 - execution time: %1$d ms", (endTime - startTime)));
        products.forEach(o -> log.info(o.toString()));
    }

    @Test
    @DisplayName("Calculate the total lump of all orders placed in Feb 2021")
    public void exercise8() {
        long startTime = System.currentTimeMillis();
        double result = orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 3, 1)) < 0)
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 8 - execution time: %1$d ms", (endTime - startTime)));
        log.info("Total lump sum = " + result);
    }

    @Test
    @DisplayName("Calculate the total lump of all orders placed in Feb 2021 (using reduce with BiFunction)")
    public void exercise8a() {
        BiFunction<Double, Product, Double> accumulator = (acc, product) -> acc + product.getPrice();

        long startTime = System.currentTimeMillis();
        double result = orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 2, 1)) >= 0)
                .filter(o -> o.getOrderDate().compareTo(LocalDate.of(2021, 3, 1)) < 0)
                .flatMap(o -> o.getProducts().stream())
                .reduce(0D, accumulator, Double::sum);

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 8a - execution time: %1$d ms", (endTime - startTime)));
        log.info("Total lump sum = " + result);
    }

    @Test
    @DisplayName("Calculate the average price of all orders placed on 15-Mar-2021")
    public void exercise9() {
        long startTime = System.currentTimeMillis();
        double result = orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .average()
                .getAsDouble();

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 9 - execution time: %1$d ms", (endTime - startTime)));
        log.info("Average = " + result);
    }

    @Test
    @DisplayName("Obtain statistics summary of all products belong to \"Books\" category")
    public void exercise10() {
        long startTime = System.currentTimeMillis();
        DoubleSummaryStatistics statistics = productRepository.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("Books"))
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
    @DisplayName("Obtain a mapping of order id and the order's product count")
    public void exercise11() {
        long startTime = System.currentTimeMillis();
        Map<Long, Integer> result = orderRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Order::getId, order -> order.getProducts().size()));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 11 - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Obtain a data map of customer and list of orders")
    public void exercise12() {
        long startTime = System.currentTimeMillis();
        Map<Customer, List<Order>> result = orderRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Order::getCustomer));

        long endTime = System.currentTimeMillis();
        log.info(String.format("exercise 12 - execution time: %1$d ms", (endTime - startTime)));
        log.info(result.toString());
    }

    @Test
    @DisplayName("Obtain a data map of customer_id and list of order_id(s)")
    public void exercise12a() {
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
    public void exercise13() {
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
    public void exercise13a() {
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
    public void exercise14() {
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
