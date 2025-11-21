    package com.ledgerCore.ledgerCore.Banking.repo;

    import com.ledgerCore.ledgerCore.Banking.entity.Customer;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface Customerrepo extends JpaRepository<Customer,Long> {

    }
