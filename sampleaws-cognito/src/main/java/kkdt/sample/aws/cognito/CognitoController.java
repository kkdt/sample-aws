/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityResult;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;

/**
 * Base controller class that exposes the Cognito client.
 * 
 * <p>
 * "Cognito is the AWS solution for managing user profiles, and Federated 
 * Identities help keep track of your users across multiple logins"
 * </p>
 * 
 * @author thinh ho
 *
 * @param <T> the event to process.
 */
@Component
public abstract class CognitoController<T extends ApplicationEvent> implements ApplicationListener<T> {
    protected final String region;
    protected AmazonCognitoIdentity cognitoIdentity;
    protected AWSCognitoIdentityProvider cognito;
    
    @Autowired(required=true)
    protected AWS aws;
    
    public CognitoController(String region) {
        this.region = region;
        
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
        JTextArea area = new JTextArea(error);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setEditable(false);
        area.setFocusable(false);
        area.setOpaque(false);
        
        JScrollPane scrollPane =  new JScrollPane(area,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        
        JOptionPane.showMessageDialog(reference, 
            scrollPane, 
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
        StringBuilder b = new StringBuilder("Credentials \n");
        b.append(String.format("   Access Key ID: %s", credentials.getAccessKeyId())).append("\n");
        b.append(String.format("   Secret Key: %s", credentials.getSecretKey())).append("\n");
        b.append(String.format("   Session: %s ", credentials.getSessionToken())).append("\n");
        b.append(String.format("   Expiration: %s", credentials.getExpiration())).append("\n");
        return b.toString();
    }
    
    /**
     * Obtain a Cognito Identity using the provider's information.
     * 
     * @param providerId
     * @param providerToken
     * @return
     */
    protected GetIdResult getCognitoId(String providerId, String providerToken) {
        GetIdRequest idRequest = new GetIdRequest()
            .withIdentityPoolId(aws.getIdentityPool())
            .addLoginsEntry(providerId, providerToken);
        GetIdResult idResult = cognitoIdentity.getId(idRequest);
        return idResult;
    }
    
    /**
     * Obtain the credentials for the configured identity pool for the specified
     * identity provider. 
     * 
     * @param providerId
     * @param providerToken
     * @return
     */
    protected Credentials getCredentials(String providerId, String providerToken) {
        GetIdResult idResult = getCognitoId(providerId, providerToken);
        GetCredentialsForIdentityRequest credentialRequest = new GetCredentialsForIdentityRequest()
            .withIdentityId(idResult.getIdentityId())
            .addLoginsEntry(providerId, providerToken);
        GetCredentialsForIdentityResult credentialResult = cognitoIdentity.getCredentialsForIdentity(credentialRequest);
        Credentials credentials = credentialResult.getCredentials();
        return credentials;
    }
}
