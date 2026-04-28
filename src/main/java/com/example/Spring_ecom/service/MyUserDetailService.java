package com.example.Spring_ecom.service;

import com.example.Spring_ecom.model.User;
import com.example.Spring_ecom.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 🔍 Fetch user from DB
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // ✅ Map DB role to Spring Authority
        // If your DB stores "ADMIN", this sets the authority to "ADMIN"
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole()) // Use the role from your User model
                .build();
    }
}