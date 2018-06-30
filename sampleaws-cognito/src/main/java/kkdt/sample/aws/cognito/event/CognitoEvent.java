/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

import kkdt.sample.aws.cognito.SampleConsole;

public abstract class CognitoEvent extends ApplicationContextEvent {
    private static final long serialVersionUID = 5905180346615871029L;
    
    public final SampleConsole reference;
    
    public CognitoEvent(ApplicationContext applicationContext, SampleConsole console) {
        super(applicationContext);
        this.reference = console;
    }
}
