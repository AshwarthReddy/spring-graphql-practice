package com.anr.controller;

import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class CustomerController {


    private final Map<Integer, Customer> DB = new ConcurrentHashMap<>();
    private final AtomicInteger PK = new AtomicInteger();
    @QueryMapping
    public Mono<String> hello(){
        return Mono.just("Hello from Dubai");
    }

    @QueryMapping
    public Flux<Customer> customers(){
        return Flux.fromIterable(DB.values());
    }

    @QueryMapping
    public Mono<Customer> customerById(@Argument Integer id){
        return Mono.just(DB.get(id));
    }

//    @MutationMapping
    @SchemaMapping(typeName = "Mutation", field = "addCustomer")
    public Mono<Customer> addCustomer(@Argument String name){
        int id = PK.incrementAndGet();
        Customer value = new Customer(id, name);
        DB.put(id, value);
        return Mono.just(value);
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

