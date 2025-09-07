package org.maxzdosreis.bookauthorapi.service;

import lombok.AllArgsConstructor;
import org.maxzdosreis.bookauthorapi.model.User;
import org.maxzdosreis.bookauthorapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.findByUsername(username);
        if(user != null) {
            return user;
        }else {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
    }

    public User findByEmail(String email) {
        User userOpt = repository.findByEmail(email);
        return userOpt;
    }
}
