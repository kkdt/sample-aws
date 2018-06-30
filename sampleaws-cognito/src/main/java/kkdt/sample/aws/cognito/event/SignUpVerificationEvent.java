/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.event;

import org.springframework.context.ApplicationContext;

import com.amazonaws.services.cognitoidp.model.SignUpResult;

import kkdt.sample.aws.cognito.SampleConsole;

public class SignUpVerificationEvent extends CognitoEvent {
    private static final long serialVersionUID = 3304826922263434398L;
    
    public final SignUpResult verification;
    
    public SignUpVerificationEvent(ApplicationContext source, SampleConsole reference, SignUpResult verification) {
        super(source, reference);
        this.verification = verification;
    }
}
