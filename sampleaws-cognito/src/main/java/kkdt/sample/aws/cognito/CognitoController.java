/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import java.awt.Window;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;

/**
 * Base controller class that exposes the Cognito client.
 * 
 * @author thinh ho
 *
 * @param <T> the event to process.
 */
@Component
public abstract class CognitoController<T extends ApplicationEvent> implements ApplicationListener<T> {
    private static final Logger logger = Logger.getLogger(CognitoController.class);
    
    protected final String poolId;
    protected final String clientId;
    protected final String region;
    protected final String identityPool;
    protected final String identityProvider;
    protected AmazonCognitoIdentity cognitoIdentity;
    protected AWSCognitoIdentityProvider cognito;
    
    public CognitoController(String poolId, String clientId, String region, String identityPool, String identityProvider) {
        this.poolId = poolId;
        this.clientId = clientId;
        this.region = region;
        this.identityPool = identityPool;
        this.identityProvider = identityProvider;
        
        cognito = AWSCognitoIdentityProviderClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
            .withRegion(this.region)
            .build();
        cognitoIdentity = AmazonCognitoIdentityClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
            .withRegion(this.region)
            .build();
    }
    
    protected void error(Window reference, String error, String title) {
        JOptionPane.showMessageDialog(reference, 
            error, 
            title, 
            JOptionPane.ERROR_MESSAGE);
    }
    
    protected void info(Window reference, String message, String title) {
        JOptionPane.showMessageDialog(reference, 
            message, 
            title, 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected String outputAuthentication(AuthenticationResultType auth) {
        StringBuilder b = new StringBuilder("Authentication \n");
        b.append(String.format("   Access Token: %s", auth.getAccessToken())).append("\n");
        b.append(String.format("   ID Token: %s", auth.getIdToken())).append("\n");
        b.append(String.format("   Refresh Token: %s", auth.getRefreshToken())).append("\n");
        b.append(String.format("   Expires In: %s ", auth.getExpiresIn())).append("\n");
        b.append(String.format("   Token Type: %s", auth.getTokenType())).append("\n");
        return b.toString();
    }
    
    protected String outputCredentials(Credentials credentials) {
        StringBuilder b = new StringBuilder("Authentication \n");
        b.append(String.format("   Access Key ID: %s", credentials.getAccessKeyId())).append("\n");
        b.append(String.format("   Secret Key: %s", credentials.getSecretKey())).append("\n");
        b.append(String.format("   Session: %s ", credentials.getSessionToken())).append("\n");
        b.append(String.format("   Expiration: %s", credentials.getExpiration())).append("\n");
        return b.toString();
    }
    
    public AuthenticationResultType authenticate(SampleConsole console, String user, char[] password) {
        InitiateAuthRequest authRequest = new InitiateAuthRequest()
            .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
            .withClientId(clientId)
            .addAuthParametersEntry("USERNAME", user)
            .addAuthParametersEntry("PASSWORD", String.valueOf(password));
        
        /*
         * The authentication parameters. These are inputs corresponding to the 
         * AuthFlow that you are invoking.
         *  
         * The required values depend on the value of AuthFlow:
         * 
         * For USER_SRP_AUTH: 
         *      USERNAME (required), 
         *      SRP_A (required), 
         *      SECRET_HASH (required if the app client is configured with a client secret), 
         *      DEVICE_KEY
         * For REFRESH_TOKEN_AUTH/REFRESH_TOKEN: 
         *      REFRESH_TOKEN (required), 
         *      SECRET_HASH (required if the app client is configured with a client secret), 
         *      DEVICE_KEY
         * For CUSTOM_AUTH: 
         *      USERNAME (required), 
         *      SECRET_HASH (if app client is configured with client secret), 
         *      DEVICE_KEY
         */
        
        InitiateAuthResult authResult = cognito.initiateAuth(authRequest);
        logger.info(outputAuthentication(authResult.getAuthenticationResult()));
        AuthenticationResultType auth = authResult.getAuthenticationResult();
        return auth;
    }
}
