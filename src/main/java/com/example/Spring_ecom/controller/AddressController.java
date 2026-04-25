package com.example.Spring_ecom.controller;



import com.example.Spring_ecom.model.Address;
import com.example.Spring_ecom.service.AddressService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody Address address ){

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Address savedAddress=addressService.addAddress(username,address);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
    }


    @GetMapping("/my-addresses")
    public ResponseEntity<List<Address>> getAddressByUser(){
        String username=SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return ResponseEntity.ok(addressService.getAddressesByUser(username));
    }
}
