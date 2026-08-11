package com.loginapp.data;

import com.loginapp.domain.User;

public interface UserStore {

    User getUserByLoginName(String loginName);

    int getFailedLoginCounter(String loginName);
    void updateFailedLoginCounter(String loginName, int counter);
}
