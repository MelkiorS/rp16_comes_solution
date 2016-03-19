package com.bionic.service;

import com.bionic.exception.auth.impl.PasswordIncorrectException;
import com.bionic.exception.auth.impl.UserExistsException;
import com.bionic.exception.auth.impl.UserNotExistsException;
import com.bionic.model.Job;
import com.bionic.model.User;

import java.util.List;

public interface UserService {


    User findById(int id);
    User addUser(User user) throws UserExistsException;
    void delete(int id);
    User findByUsername(String name);
    User findByUserEmail(String email);
    User saveUser(User user);
    List<User> getAll();
    Job findJobById(int id);

    void resetPassword(String email) throws UserNotExistsException;

    void changePassword(String email, String oldPassword, String newPassword) throws PasswordIncorrectException;

}
