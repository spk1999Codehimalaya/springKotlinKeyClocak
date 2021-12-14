package com.codehimalaya.springkotlin.service;

import com.codehimalaya.springkotlin.config.KeycloakConfig;
import com.codehimalaya.springkotlin.model.LoginRequest;
import com.codehimalaya.springkotlin.model.UserData;
import com.codehimalaya.springkotlin.model.UserDetails;
import com.codehimalaya.springkotlin.repo.UserDetailsRepo;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;

@Service
public class KeycloakAdminClientService {

    private final UserDetailsRepo userDetailsRepo;

    public KeycloakAdminClientService(UserDetailsRepo userDetailsRepo) {
        this.userDetailsRepo = userDetailsRepo;
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public String addUser(UserData userData) {
        System.out.println("Adding user");
        UsersResource usersResource = KeycloakConfig.getInstance().realm("First").users();

        UserRepresentation kcUser = new UserRepresentation();
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);
        kcUser.setId(uuid);
        kcUser.setUsername(userData.getUsername());
        kcUser.setFirstName(userData.getFirstName());
        kcUser.setLastName(userData.getLastName());
        kcUser.setEmail(userData.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        List<CredentialRepresentation> credentialRepresentations = new ArrayList<>();
        CredentialRepresentation passwordCredentials = createPasswordCredentials(userData.getPassword());
        credentialRepresentations.add(passwordCredentials);
        kcUser.setCredentials(credentialRepresentations);

        Map<String, List<String>> customAttributes = new HashMap<>();
        customAttributes.put("accountNumber", Collections.singletonList(userData.getAccountNumber()));
        kcUser.setAttributes(customAttributes);

        Response response = usersResource.create(kcUser);



        if (response.getStatus() == 409) {
            return "Already exists";
        }


        return "Success";

    }


    public String addUserUsingSeparateTable(UserData userData){
        UsersResource usersResource = KeycloakConfig.getInstance().realm("First").users();

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(userData.getUsername());
        kcUser.setFirstName(userData.getFirstName());
        kcUser.setLastName(userData.getLastName());
        kcUser.setEmail(userData.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        List<CredentialRepresentation> credentialRepresentations = new ArrayList<>();
        CredentialRepresentation passwordCredentials = createPasswordCredentials(userData.getPassword());
        credentialRepresentations.add(passwordCredentials);
        kcUser.setCredentials(credentialRepresentations);

        Response response = usersResource.create(kcUser);

        if (response.getStatus() == 409) {
            return "Already exists";
        }

        var savedUser = usersResource.search(userData.getUsername()).get(0);

        if(savedUser != null && !savedUser.getId().isBlank()){
            System.out.println("Saving into external table..........."+savedUser.getId()+"\n\n");
            UserDetails userDetails = new UserDetails();
            userDetails.setAccNum(userData.getAccountNumber());
            userDetails.setPhoneNum(userData.getPhoneNumber());
            userDetails.setUsername(savedUser.getUsername());
            userDetails.setUserId(savedUser.getId());

            userDetailsRepo.save(userDetails);

        }else{
            // delete the user from keycloak
        }

        return "Success";
    }

    public AccessTokenResponse getAccessToken(LoginRequest request) {
        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", "");
        clientCredentials.put("grant_type", "password");


        Configuration config = new Configuration(
                "http://localhost:8080/auth",
                "First", "springboot",
                clientCredentials, null);

        var authzClient = AuthzClient.create(config);


        return authzClient.obtainAccessToken(request.getUsername(), request.getPassword());

    }

    public AccessTokenResponse getAccessTokenFromAccountNumber(LoginRequest request){
        UserDetails userDetails = userDetailsRepo.findByAccNum(request.getAccNum())
                .orElseThrow(NoSuchElementException::new);

        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", "");
        clientCredentials.put("grant_type", "password");


        Configuration config = new Configuration(
                "http://localhost:8080/auth",
                "First", "springboot",
                clientCredentials, null);

        var authzClient = AuthzClient.create(config);


        return authzClient.obtainAccessToken(userDetails.getUsername(), request.getPassword());

    }

    public Object getOpenSessions(String userId) {

        return KeycloakConfig.getInstance()
                .realm("First")
                .users().get(userId).getUserSessions();

    }

    public String closeOpenSessions(String userId) {
        KeycloakConfig.getInstance()
                .realm("First")
                .users().get(userId).logout();

        return "Success";
    }
}
