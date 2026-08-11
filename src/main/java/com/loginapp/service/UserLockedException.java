package com.loginapp.service;

public class UserLockedException extends RuntimeException {

    public UserLockedException(String message) {
        super(message);
    }
}
