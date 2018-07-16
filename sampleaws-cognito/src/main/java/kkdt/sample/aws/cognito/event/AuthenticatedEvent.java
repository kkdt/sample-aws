/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import java.awt.Window;

import org.springframework.context.ApplicationContext;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cognitoidentity.model.Credentials;

/**
 * An event notifying that the user has been fully authenticated via Cognito with
 * an identity provider.
 * 
 * <p>
 * To log out, simply clear/forget the access token on the client side.
 * </p>
 * 
 * @author thinh ho
 *
 */
public class AuthenticatedEvent extends CognitoEvent {
    private static final long serialVersionUID = -7781052919673571133L;
    
    /**
     * Authenticated user identity (jwt).
     */
    public final String idToken;
    
    /**
     * Credentials from the provider that the user used to log into the application 
     * (i.e. Facebook, AWS User Pool, etc).
     */
    public final Credentials credentials;
    
    public final AWSCredentialsProvider awsCredentialsProvider;

    public AuthenticatedEvent(ApplicationContext source, Window console, String jwt, 
        Credentials credentials, 
        AWSCredentialsProvider awsCredentialsProvider) 
    {
        super(source, console);
        this.idToken = jwt;
        this.credentials = credentials;
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

}
