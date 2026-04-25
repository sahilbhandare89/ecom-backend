package com.example.Spring_ecom.repo;

import com.example.Spring_ecom.model.Address;
import com.example.Spring_ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepo extends JpaRepository<Address,Long> {
    List<Address> findByUser(User user);
}
