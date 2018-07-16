/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.google;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;

import kkdt.sample.aws.cognito.CognitoController;
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
        @Value("${google.clientId:null}") String clientId, @Value("${google.clientSecret:null}") String clientSecret) 
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
            GoogleIdentityProvider provider = new GoogleIdentityProvider(clientId, clientSecret)
                .authenticate(new GoogleCodeVerification(event.reference));
            Credential googleCredential = provider.getCredential();
            
            String idToken = provider.getIdToken();
            Credentials awsCredential = getCredentials(event.identityProviderId, idToken);
            AWSCredentials clientCredentials = new BasicSessionCredentials(
                awsCredential.getAccessKeyId(), 
                awsCredential.getSecretKey(), 
                awsCredential.getSessionToken());
            AWSCredentialsProvider authenticatedCredentials = new AWSStaticCredentialsProvider(clientCredentials);
            
            logger.info(outputCredentials(awsCredential));
            parseIdToken(idToken, e -> error(event.reference, e.getMessage(), "Google Login Error"));
            parseUserInfo(googleCredential, e -> error(event.reference, e.getMessage(), "Google Login Error"));
            
            // notify that the user has been fully authenticated
            ApplicationContext context = event.getApplicationContext();
            context.publishEvent(new AuthenticatedEvent(context, event.reference, 
                idToken, 
                awsCredential, 
                authenticatedCredentials));
            
        } catch (Exception e) {
            logger.error(e);
            error(event.reference, e.getMessage(), "Google Login Error");
        }
    }
    
    private void parseUserInfo(Credential credential, Consumer<Exception> errorHandler){
//        GoogleCredential googleCredential = new GoogleCredential()
//            .setAccessToken(cred.getAccessToken());
        Oauth2 oauth2 = new Oauth2.Builder(transport, jsonFactory, credential)
            .setApplicationName("sampleaws")
            .build();
        try {
            Userinfoplus userinfo = oauth2.userinfo().get().execute();
            logger.info(String.format("Google user information:\n%s", userinfo.toPrettyString()));
            logger.info(String.format("Google Credentials:\n   AccessToken: %s\n   RefreshToken: %s\n   Expires: %tc",
                credential.getAccessToken(),
                credential.getRefreshToken(),
                new Date(credential.getExpirationTimeMilliseconds())));
        } catch (IOException e) {
            logger.error(e);
            if(errorHandler != null) {
                errorHandler.accept(e);
            }
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
                .setIssuer("accounts.google.com")
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
