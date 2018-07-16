/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.google;

import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.MemoryDataStoreFactory;

/**
 * Google authentication.
 * 
 * @author thinh ho
 *
 */
public class GoogleIdentityProvider {
    private final String clientId;
    private final String clientSecret;
    private final NetHttpTransport transport;
    private final JsonFactory jsonFactory;
    private final DataStore<String> userInfo;
    
    private Credential credential;
    
    public GoogleIdentityProvider(String clientId, String clientSecret) throws IOException {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.transport = new NetHttpTransport();
        this.jsonFactory = new JacksonFactory();
        this.userInfo = MemoryDataStoreFactory.getDefaultInstance()
            .getDataStore("user");
    }
    
    /**
     * Authenticate via the <code>GoogleAuthorizationCodeFlow</code> using the 
     * provided code verification.
     * 
     * @param verification
     * @return
     * @throws IOException
     */
    public GoogleIdentityProvider authenticate(VerificationCodeReceiver verification) throws IOException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport, 
                jsonFactory, 
                clientId, clientSecret, 
                Arrays.asList("email","profile", "openid"))
            .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
            .setCredentialCreatedListener(new AuthorizationCodeFlow.CredentialCreatedListener() {
                @Override
                public void onCredentialCreated(Credential credential, TokenResponse tokenResponse) throws IOException {
                    userInfo.set("id_token", tokenResponse.get("id_token").toString());
                }
            })
        .build();
        
        Credential cred = new AuthorizationCodeInstalledApp(flow, verification)
            .authorize("");
        this.credential = cred;
        return this;
    }
    
    /**
     * The Google API credential for accessing other Google resources.
     * 
     * @return
     */
    public Credential getCredential() {
        return this.credential;
    }
    
    /**
     * The identity of the authenticated Google user.
     * 
     * @return
     * @throws IOException
     */
    public String getIdToken() throws IOException {
        return (String)userInfo.get("id_token");
    }
}
