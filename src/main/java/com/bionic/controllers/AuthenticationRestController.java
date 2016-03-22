package com.bionic.controllers;

import com.bionic.exception.auth.impl.UserExistsException;
import com.bionic.model.User;
import com.bionic.service.MailService;
import com.bionic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author taras.yaroshchuk
 */

@RestController
@RequestMapping("/rest/api/auth")
public class AuthenticationRestController {
    @Autowired
    private UserService userService;

    @Autowired
    MailService mailService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED) // HTTP 201 "Created"
    public User createUser(@Valid @RequestBody User user, BindingResult result, HttpServletResponse response) throws BindException {
        if (result.hasErrors()) {
            throw new BindException(result);
        }

        try {
            System.out.println(user);
            userService.addUser(user);
        } catch (UserExistsException e) {
            e.printStackTrace();
        }
        User savedUser = userService.findByUserEmail(user.getEmail());
        response.setHeader("Location", "/users/" + savedUser.getId());
        return savedUser;
    }
}