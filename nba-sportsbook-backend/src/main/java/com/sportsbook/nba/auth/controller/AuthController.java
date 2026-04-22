package com.sportsbook.nba.auth.controller;

import com.sportsbook.nba.auth.dto.AuthResponseDto;
import com.sportsbook.nba.auth.dto.LoginRequestDto;
import com.sportsbook.nba.auth.dto.RegisterRequestDto;
import com.sportsbook.nba.auth.service.AuthService;
import com.sportsbook.nba.user.User;
import com.sportsbook.nba.user.UserDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;
    private final UserDao userDao;

    public AuthController(AuthService authService, UserDao userDao, UserDao userDaoq) {
        this.authService = authService;
        this.userDao = userDao;
    }

    // register a new user
    @PostMapping("/register")
    public AuthResponseDto register(@RequestBody RegisterRequestDto request, HttpSession session) {
        User user = authService.register(request.username(), request.password());

        // remember who is logged in
        session.setAttribute("user", user.getId());

        // send back response
        return new AuthResponseDto(user.getId(), user.getUsername());
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequestDto request, HttpSession session) {
        User user = authService.login(request.username(), request.password());

        // remember who is logged in
        session.setAttribute("userId", user.getId());

        return new AuthResponseDto(user.getId(), user.getUsername());
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/me")
    public AuthResponseDto me(HttpSession session) {

        // pull userId from session
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj == null) {
            throw new RuntimeException("Not logged in");
        }

        Long userId = (Long) userIdObj;

        User user = userDao.findById(userId);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return new AuthResponseDto(user.getId(), user.getUsername());
    }

}
