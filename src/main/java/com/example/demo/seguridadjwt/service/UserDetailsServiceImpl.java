package com.example.demo.seguridadjwt.service;

import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.seguridadjwt.model.User;
import com.example.demo.seguridadjwt.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepo;

    public UserDetailsServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> op = userRepo.findByUsername(username);
        if (op.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        User user = op.get();
        String role = (user.getRole() == null || user.getRole().isBlank()) ? "USER" : user.getRole();
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + role))
                .build();
    }
}