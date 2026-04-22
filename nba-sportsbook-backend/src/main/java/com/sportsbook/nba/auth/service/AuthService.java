package com.sportsbook.nba.auth.service;

import com.sportsbook.nba.user.User;
import com.sportsbook.nba.user.UserDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    // registers a new user
    public User register(String username, String password) {
        // clean the string
        username = username.trim();

        User existingUser = userDao.findByUsername(username);
        if (existingUser != null) {
            throw new RuntimeException("User already exists");
        }

        // hash the password before its stored
        String passwordHash = passwordEncoder.encode(password);

        // create the new user give balance of 0
        userDao.createUser(username, passwordHash, 0);

        return userDao.findByUsername(username);
    }

    // log in user
    public User login(String username, String password) {
        username = username.trim();
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Invalid username or password");
        }

        // compare enterd password agaisnt the stored hash
        boolean match = passwordEncoder.matches(password, user.getPasswordHash());

        if (!match) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

}
