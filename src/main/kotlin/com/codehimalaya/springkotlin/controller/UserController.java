package com.codehimalaya.springkotlin.controller;

import com.codehimalaya.springkotlin.model.LoginRequest;
import com.codehimalaya.springkotlin.model.UserData;
import com.codehimalaya.springkotlin.service.KeycloakAdminClientService;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/api")
@RestController
public class UserController {

    @Autowired
    KeycloakAdminClientService keycloakAdminClientService;

    @PostMapping("/save")
    public String createUser(@RequestBody UserData userData) {
        return keycloakAdminClientService.addUserUsingSeparateTable(userData);
    }

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody LoginRequest request)  {
        return ResponseEntity.ok(keycloakAdminClientService.getAccessToken(request));

    }

    @PostMapping("/token/acc")
    public ResponseEntity<?> getTokenFromAccNum(@RequestBody LoginRequest request)  {
        return ResponseEntity.ok(keycloakAdminClientService.getAccessTokenFromAccountNumber(request));

    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getOpenSessions(Principal principal){
        return ResponseEntity.ok(keycloakAdminClientService.getOpenSessions(principal.getName()));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> closeOpenSessions(Principal principal){
        return ResponseEntity.ok(keycloakAdminClientService.closeOpenSessions(principal.getName()));
    }

    @GetMapping("/test")
    public String test(Principal principal){
        if(principal != null){
            System.out.println("\n\n"+principal.getName()+"\n\n");
        }
        return "Test";
    }

    private static AccessToken getKeycloakAccessToken(Principal principal){
        var keycloakToken = (KeycloakAuthenticationToken)principal;
        var simpleKeycloakAccount = (SimpleKeycloakAccount) keycloakToken.getDetails();
        return simpleKeycloakAccount.getKeycloakSecurityContext().getToken();

/*     String userId = accessToken.getOtherClaims().get("userId").toString();
        To get userId, we can get it from principal.getName()
        To get other keycloak related info from token , we use AccessToken methods.

 */

    }

}
