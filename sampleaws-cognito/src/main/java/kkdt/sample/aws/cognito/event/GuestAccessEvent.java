/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import org.springframework.context.ApplicationContext;

import kkdt.sample.aws.cognito.SampleConsole;

public class GuestAccessEvent extends CognitoEvent {
    private static final long serialVersionUID = 1794023424474258520L;
    
    public GuestAccessEvent(ApplicationContext applicationContext, SampleConsole reference) {
        super(applicationContext, reference);
    }
}
