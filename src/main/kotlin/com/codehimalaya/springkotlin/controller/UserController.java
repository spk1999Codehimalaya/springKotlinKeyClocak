package com.codehimalaya.springkotlin.controller;

import com.codehimalaya.springkotlin.model.LoginRequest;
import com.codehimalaya.springkotlin.model.User;
import com.codehimalaya.springkotlin.service.KeycloakAdminClientService;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class UserController {

    @Autowired
    KeycloakAdminClientService keycloakAdminClientService;

    @PostMapping("/save")
    public String createUser(@RequestBody User user) {
        System.out.println("saving.....");
        return keycloakAdminClientService.addUser(user);
    }

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody LoginRequest request)  {
        return ResponseEntity.ok(keycloakAdminClientService.getAccessToken(request));

    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getOpenSessions(@RequestParam String userId){
        return ResponseEntity.ok(keycloakAdminClientService.getOpenSessions(userId));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> closeOpenSessions(@RequestParam String userId){
        return ResponseEntity.ok(keycloakAdminClientService.closeOpenSessions(userId));
    }

}
