package com.example.letranthainguu.backend.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.letranthainguu.backend.config.UserInfoConfig;
import com.example.letranthainguu.backend.domain.User;
import com.example.letranthainguu.backend.exceptions.ResourceNotFoundException;
import com.example.letranthainguu.backend.repository.UserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByEmail(username);
        return (UserDetails) user.map(UserInfoConfig::new).orElseThrow(() -> new ResourceNotFoundException("User", "email", username));
    }
}
