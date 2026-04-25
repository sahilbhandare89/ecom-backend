package com.example.Spring_ecom.service;

import com.example.Spring_ecom.model.Address;
import com.example.Spring_ecom.model.User;
import com.example.Spring_ecom.repo.AddressRepo;
import com.example.Spring_ecom.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserRepo userRepo;
    private final AddressRepo addressRepo;

    public Address addAddress(String username, Address address) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        address.setUser(user);
        return addressRepo.save(address);
    }

    public List<Address> getAddressesByUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return addressRepo.findByUser(user);
    }
}
