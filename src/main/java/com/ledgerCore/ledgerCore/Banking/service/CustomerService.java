package com.ledgerCore.ledgerCore.Banking.service;

import com.ledgerCore.ledgerCore.Banking.entity.Customer;
import com.ledgerCore.ledgerCore.Banking.repo.Customerrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private Customerrepo customerrepo;

    // Create customer
    public Customer createCustomer(Customer customer) {
        return customerrepo.save(customer);
    }

    // Get customer by id
    public Optional<Customer> getCustomerById(Long id) {
        return customerrepo.findById(id);
    }

    // Update existing customer
    public Customer updateCustomer(Long id, Customer customerUpdates) {
        Optional<Customer> opt = customerrepo.findById(id);
        if (opt.isEmpty()) {
            return null; // optionally throw NotFoundException
        }
        Customer cx = opt.get();

        if (customerUpdates.getName() != null) cx.setName(customerUpdates.getName());
        if (customerUpdates.getEmail() != null) cx.setEmail(customerUpdates.getEmail());
        if (customerUpdates.getPhone() != null) cx.setPhone(customerUpdates.getPhone());

        return customerrepo.save(cx);
    }

    // Delete customer
    public String deleteCustomer(Long id) {
        customerrepo.deleteById(id);
        return "Customer deleted successfully";
    }

    // Get all customers
    public java.util.List<Customer> getAllCustomers() {
        return customerrepo.findAll();
    }
}
