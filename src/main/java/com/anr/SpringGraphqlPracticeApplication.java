package com.anr;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@SpringBootApplication
public class SpringGraphqlPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGraphqlPracticeApplication.class, args);
	}

	@Bean
	public RuntimeWiringConfigurer runtimeWiringConfigurer(CustomerService crmService){
		return builder -> {
			builder.type("Customer", wiring -> wiring
					.dataFetcher("profile", env -> crmService.getProfile(env.getSource())));
			builder.type("Query", wiring -> wiring
					.dataFetcher("customers", env -> crmService.getAllCustomer())
					.dataFetcher("customerById", env ->
							crmService.findByCustomerId(Integer.parseInt(env.getArgument("id")))));



		};
	}

}


record Customer(Integer id, String name) { }

record Profile(Integer id, Integer customerId) { }


@Service
class CustomerService{

	Profile getProfile(Customer customer){
		return new Profile(customer.id(), customer.id());
	}

	List<Customer> getAllCustomer(){
		return Stream.of(new Customer(1, "Aswarthana"), new Customer(2, "Bhupathi")).toList();
	}

	Customer findByCustomerId(Integer id){
		return new Customer(id, Math.random() > .05 ? "Aswarthana" : "Bhupathi");
	}
}

