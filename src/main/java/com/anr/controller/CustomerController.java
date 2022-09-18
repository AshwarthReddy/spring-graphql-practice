package com.anr.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class CustomerController {


    @QueryMapping
    public Mono<String> hello(){
        return Mono.just("Hello from Dubai");
    }

    @QueryMapping
    public Flux<Customer> customers(){
        return Flux.fromIterable(Stream.of(new Customer(1, "Aswarthana"), new Customer(2, "Bhupathi")).toList());
    }

    @QueryMapping
    public Mono<Customer> customerById(@Argument Integer id){
        return Mono.just(new Customer(id, Math.random() > .05 ? "Aswarthana" : "Reddy"));
    }

//    @SchemaMapping(typeName = "Customer")
//    public Profile profile(Customer customer){
//        System.out.println("getting account for customer # " + customer.id());
//        return new Profile(customer.id(), customer.id());
//    }

    /* solving n+1 problems */
    @BatchMapping
    public Mono<Map<Customer, Profile>> profile(List<Customer> customers){
        System.out.println("getting account for customers # " +customers);
        return Mono.just(customers
                .parallelStream()
                .collect(Collectors.toMap(customer -> customer,
                         customer -> new Profile(customer.id(), customer.id()))));
    }
}


record Customer(Integer id, String name) { }

record Profile(Integer id, Integer customerId) { }

