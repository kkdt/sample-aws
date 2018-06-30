/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import org.springframework.context.ApplicationContext;

import com.amazonaws.services.cognitoidp.model.UserType;

import kkdt.sample.aws.cognito.SampleConsole;

public class CreateUserEvent extends CognitoEvent {
    private static final long serialVersionUID = 3799358027780949280L;
    
    public final UserType user;

    public CreateUserEvent(ApplicationContext applicationContext, SampleConsole console, UserType user) {
        super(applicationContext, console);
        this.user = user;
    }

}
