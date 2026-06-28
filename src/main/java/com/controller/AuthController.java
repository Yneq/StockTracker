package com.controller;

import com.model.User;
import com.model.auth.LoginRequest;
import com.model.auth.LoginResponse;
import com.model.auth.RegisterRequest;
import com.service.AuthService;
import com.service.impl.AuthServiceImpl;
import com.util.JwtUtil;
import com.dao.UserDao;
import com.dao.impl.UserDaoImpl;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService=new AuthServiceImpl();
    private UserDao userDao=new UserDaoImpl();

    @PostMapping("/register")
    public String register(
            @RequestBody RegisterRequest req) {

        authService.register(
                req.getUsername(),
                req.getPassword(),
                req.getEmail());

        return "Register Success";
    }
    
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest req) {

        String token=authService.login(req.getUsername(),req.getPassword());
        return new LoginResponse(token);
    }
    
    @GetMapping("/me")
    public User me(
            @RequestHeader("Authorization")
            String authHeader) {

	    String token=authHeader.replace("Bearer ","");
	    String username=JwtUtil.getUsername(token);
	    return userDao.findByUsername(username);
    }
    
}

