package com.service;

public interface AuthService {

    void register(
            String username,
            String password,
            String email);
    String login(
            String username,
            String password);
}