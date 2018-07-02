/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import org.springframework.context.ApplicationContext;

import kkdt.sample.aws.cognito.SampleConsole;

/**
 * Indicate that the user selected to reset their password.
 * 
 * @author thinh ho
 *
 */
public class ResetPasswordEvent extends CognitoEvent {
    private static final long serialVersionUID = -1408642502582321220L;

    public ResetPasswordEvent(ApplicationContext applicationContext, SampleConsole console) {
        super(applicationContext, console);
    }
}
