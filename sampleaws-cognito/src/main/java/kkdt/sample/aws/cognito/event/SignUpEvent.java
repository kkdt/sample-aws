/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import org.springframework.context.ApplicationContext;

import kkdt.sample.aws.cognito.SampleConsole;

public class SignUpEvent extends CognitoEvent {
    private static final long serialVersionUID = -2327711484892604220L;
    
    public final boolean admin;
    
    public SignUpEvent(ApplicationContext source, SampleConsole reference, boolean admin) {
        super(source, reference);
        this.admin = admin;
    }
    
    public SignUpEvent(ApplicationContext source, SampleConsole reference) {
        this(source, reference, false);
    }
}
