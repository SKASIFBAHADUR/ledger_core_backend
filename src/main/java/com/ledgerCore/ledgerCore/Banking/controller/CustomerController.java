package com.ledgerCore.ledgerCore.Banking.controller;

import com.ledgerCore.ledgerCore.Banking.entity.Customer;
import com.ledgerCore.ledgerCore.Banking.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/list")
    public java.util.List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PostMapping("/create")
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping("/get/{id}")
    public Optional<Customer> getCustomer(@PathVariable("id") Long id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/update/{id}")
    public Customer updateCustomer(@PathVariable("id") Long id, @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        return customerService.deleteCustomer(id);
    }
}
