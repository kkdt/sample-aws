/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import org.springframework.context.ApplicationContext;

import kkdt.sample.aws.cognito.SampleConsole;

public class LoginEvent extends CognitoEvent {
    private static final long serialVersionUID = -3253151525774995466L;
    
    public final String email;
    public final char[] password;
    public final String identityProviderName;
    public final String identityProviderId;
    
    public LoginEvent(ApplicationContext applicationContext, SampleConsole reference, 
        String identityProviderName,
        String identityProviderId,
        String email, 
        char[] password) 
    {
        super(applicationContext, reference);
        this.identityProviderName = identityProviderName;
        this.identityProviderId = identityProviderId;
        this.email = email;
        this.password = password;
    }
}
