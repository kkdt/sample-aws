/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;

import kkdt.sample.aws.cognito.event.EmailConfirmationEvent;
import kkdt.sample.aws.support.VerificationPanel;

@Component
public class CognitoEmailConfirmationController extends CognitoController<EmailConfirmationEvent> {
    private static final Logger logger = Logger.getLogger(CognitoEmailConfirmationController.class);

    public CognitoEmailConfirmationController(@Value("${cognito.region:null}") String region) {
        super(region);
    }
    
    public void confirmEmail(EmailConfirmationEvent event) {
        VerificationPanel panel = new VerificationPanel(event.email, "Code", "Password", "Confirm Password");
        panel.setRequirePassword(false);
        
        boolean valid = true;
        do {
            JOptionPane.showMessageDialog(event.reference, 
                panel, 
                "Confirm Email", 
                JOptionPane.PLAIN_MESSAGE);
            try {
                ConfirmSignUpRequest request = new ConfirmSignUpRequest()
                    .withUsername(event.email)
                    .withConfirmationCode(panel.getTempValue())
                    .withClientId(aws.getClientId());
                ConfirmSignUpResult confirmationResult = cognito.confirmSignUp(request);
                logger.info(String.format("Email confirmed %s, request %s", 
                    event.email, 
                    confirmationResult.getSdkResponseMetadata().getRequestId()));
                
                info(event.reference, 
                    String.format("Email %s confirmed", event.email), 
                    "Email Confirmation");
            } catch (Exception e) {
                error(event.reference, "Cannot verify code " + panel.getTempValue(), "Email Verification Error");
                logger.error(e);
                valid = false;
            }
        } while(!valid);
    }
    
    @Override
    public void onApplicationEvent(EmailConfirmationEvent event) {
        confirmEmail(event);
    }

}
