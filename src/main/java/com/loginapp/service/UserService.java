package com.loginapp.service;

import com.loginapp.domain.Address;
import com.loginapp.domain.LoginResult;

public interface UserService {

    LoginResult login(String loginName, String password);
    Address getLoggedInUserAddress();

}
