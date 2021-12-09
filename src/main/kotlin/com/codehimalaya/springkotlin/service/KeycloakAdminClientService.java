package com.codehimalaya.springkotlin.service;

import com.codehimalaya.springkotlin.config.KeycloakConfig;
import com.codehimalaya.springkotlin.model.LoginRequest;
import com.codehimalaya.springkotlin.model.User;
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

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public String addUser(User user) {
        System.out.println("Adding user");
        UsersResource usersResource = KeycloakConfig.getInstance().realm("First").users();

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUsername());
        kcUser.setFirstName(user.getFirstName());
        kcUser.setLastName(user.getLastName());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        List<CredentialRepresentation> credentialRepresentations = new ArrayList<>();
        CredentialRepresentation passwordCredentials = createPasswordCredentials(user.getPassword());
        credentialRepresentations.add(passwordCredentials);
        kcUser.setCredentials(credentialRepresentations);

        Map<String, List<String>> customAttributes = new HashMap<>();
        customAttributes.put("accountNumber", Collections.singletonList(user.getAccountNumber()));
        kcUser.setAttributes(customAttributes);

        Response response = usersResource.create(kcUser);

        if (response.getStatus() == 409) {
            return "Already exists";
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
