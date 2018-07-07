/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.google;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import kkdt.sample.aws.cognito.CognitoController;
import kkdt.sample.aws.cognito.SampleConsole;
import kkdt.sample.aws.cognito.event.AuthenticatedEvent;
import kkdt.sample.aws.cognito.event.LoginEvent;

@Component
public class GoogleLoginController extends CognitoController<LoginEvent> {
    private static final Logger logger = Logger.getLogger(GoogleLoginController.class);
    public static final String PROVIDERNAME = "Google";
    
    private final String clientId;
    private final String clientSecret;
    private final NetHttpTransport transport;
    private final JsonFactory jsonFactory;
    
    public GoogleLoginController(@Value("${cognito.region:null}") String region,
        @Value("${google.clientId}") String clientId, @Value("${google.clientSecret}") String clientSecret) 
    {
        super(region);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.transport = new NetHttpTransport();
        this.jsonFactory = new JacksonFactory();
    }
    
    @Override
    public void onApplicationEvent(LoginEvent event) {
        if(!PROVIDERNAME.equals(event.identityProviderName)) {
            logger.info(String.format("Provider %s is not supported by %s", 
                event.identityProviderName, getClass().getSimpleName()));
            return;
        }
        
        try {
            String identityProviderId = event.identityProviderId;
            DataStore<String> userInfo = MemoryDataStoreFactory.getDefaultInstance()
                .getDataStore("user");
            
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport, 
                    jsonFactory, 
                    clientId, clientSecret, 
                    Arrays.asList("email", "profile"))
                .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                .setCredentialCreatedListener(new AuthorizationCodeFlow.CredentialCreatedListener() {
                    @Override
                    public void onCredentialCreated(Credential credential, TokenResponse tokenResponse) throws IOException {
                        userInfo.set("id_token", tokenResponse.get("id_token").toString());
                    }
                })
            .build();
            
            GoogleCodeVerification verification = new GoogleCodeVerification(event.reference);
            
            Credential cred = new AuthorizationCodeInstalledApp(flow, verification)
                .authorize("");
            ClientParametersAuthentication auth = (ClientParametersAuthentication)cred.getClientAuthentication();
            
            logger.info(String.format("Google Credentials:\n   %s\n   %s", cred.getAccessToken(), cred.getRefreshToken()));
            logger.info(String.format("Google Autentication:\n   %s\n   %s", auth.getClientId(), auth.getClientSecret()));
            
            String idToken = (String)userInfo.get("id_token");
            Credentials credentials = getCredentials(identityProviderId, idToken);
            logger.info(outputCredentials(credentials));
            
            parseIdToken(idToken, e -> error(event.reference, e.getMessage(), "Google Login Error"));
            
            // notify that the user has been fully authenticated
            ApplicationContext context = event.getApplicationContext();
            context.publishEvent(new AuthenticatedEvent(context, event.reference, idToken, credentials));
            
            SampleConsole console = event.reference;
            console.enableActions(false);
            console.enableInputs(false);
            
        } catch (Exception e) {
            logger.error(e);
            error(event.reference, e.getMessage(), "Google Login Error");
        }
    }
    
    /**
     * Using Google token verification to read the ID Token.
     * 
     * @param idToken
     * @param errorHandler
     */
    private void parseIdToken(String idToken, Consumer<Exception> errorHandler) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Arrays.asList(clientId))
                .setIssuer("https://accounts.google.com")
                .build();
            GoogleIdToken _idToken = null;
            
            _idToken = verifier.verify(idToken);
            
            GoogleIdToken.Payload payload = null;
            if (_idToken != null) {
                payload = _idToken.getPayload();
            }
            
            logger.info(String.format("Google parsed token: %s", payload));
        } catch (Exception e) {
            logger.error(e);
            if(errorHandler != null) {
                errorHandler.accept(e);
            }
        }
    }
    
}
