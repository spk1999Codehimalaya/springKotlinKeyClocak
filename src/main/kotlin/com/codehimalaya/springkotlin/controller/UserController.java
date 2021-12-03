package com.codehimalaya.springkotlin.controller;

import com.codehimalaya.springkotlin.model.User;
import com.codehimalaya.springkotlin.service.KeycloakAdminClientService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class UserController {

    @Autowired
    KeycloakAdminClientService keycloakAdminClientService;

    @PostMapping
    public UserRepresentation createUser(@RequestBody User user)
    {
        return keycloakAdminClientService.addUser(user);
    }

}
