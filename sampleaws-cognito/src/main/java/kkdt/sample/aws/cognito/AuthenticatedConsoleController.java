/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import kkdt.sample.aws.cognito.event.AuthenticatedEvent;

/**
 * Listens for the <code>AuthenticatedEvent</code> to set the console in the
 * authenticated mode.
 * 
 * @author thinh ho
 *
 */
@Component
public class AuthenticatedConsoleController implements ApplicationListener<AuthenticatedEvent> {

    @Override
    public void onApplicationEvent(AuthenticatedEvent event) {
        if(event.reference instanceof SampleConsole) {
            SampleConsole console = (SampleConsole)event.reference;
            console.enableActions(false);
            console.enableInputs(false);
        }
    }
    
}
