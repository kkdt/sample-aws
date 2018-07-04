/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import org.springframework.context.ApplicationContext;

import com.amazonaws.services.cognitoidentity.model.Credentials;

import kkdt.sample.aws.cognito.SampleConsole;

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
     * Used to access and securely sign requests to AWS resources.
     */
    public final String awsSecretKey;
    
    /**
     * Credentials for the provider that the user used to log into the application 
     * (i.e. Facebook, AWS User Pool, etc).
     */
    public final Credentials credentials;

    public AuthenticatedEvent(ApplicationContext source, SampleConsole console, String awsSecretKey, Credentials credentials) {
        super(source, console);
        this.awsSecretKey = awsSecretKey;
        this.credentials = credentials;
    }

}
