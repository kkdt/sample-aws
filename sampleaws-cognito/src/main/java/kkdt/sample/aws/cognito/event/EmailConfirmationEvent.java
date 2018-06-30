/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import org.springframework.context.ApplicationContext;

import kkdt.sample.aws.cognito.SampleConsole;

public class EmailConfirmationEvent extends CognitoEvent {
    private static final long serialVersionUID = 2130407745810494102L;
    
    public final String email;
    
    public EmailConfirmationEvent(ApplicationContext source, SampleConsole reference, String email) {
        super(source, reference);
        this.email = email;
    }
}
