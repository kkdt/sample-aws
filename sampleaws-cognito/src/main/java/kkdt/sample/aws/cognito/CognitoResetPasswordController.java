/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import java.util.Objects;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;

import kkdt.sample.aws.cognito.event.ResetPasswordEvent;
import kkdt.sample.aws.support.VerificationPanel;

@Component
public class CognitoResetPasswordController extends CognitoController<ResetPasswordEvent> {
    private static final Logger logger = Logger.getLogger(CognitoResetPasswordController.class);
    
    public CognitoResetPasswordController(@Value("${cognito.region:null}") String region) {
        super(region);
    }

    @Override
    public void onApplicationEvent(ResetPasswordEvent event) {
        String email = JOptionPane.showInputDialog(event.reference, 
            "Email: ", 
            "Reset Password", 
            JOptionPane.PLAIN_MESSAGE);
        
        if(Objects.nonNull(email) && !"".equals(email)) {
            try {
                ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest()
                    .withUsername(email)
                    .withClientId(aws.getClientId());
                ForgotPasswordResult forgotResult = cognito.forgotPassword(forgotRequest);
                info(event.reference, "Password reset sent to " + email, "Password Reset");
                logger.info("Forgot Password Result: " + forgotResult);
                
                VerificationPanel panel = new VerificationPanel(email, "Code", "Password", "Confirm Password");
                
                ConfirmForgotPasswordRequest confirmForgotRequest = null;
                boolean valid = true;
                do {
                    JOptionPane.showMessageDialog(event.reference, 
                        panel, 
                        "Reset Password", 
                        JOptionPane.PLAIN_MESSAGE);
                    try {
                        confirmForgotRequest = new ConfirmForgotPasswordRequest()
                            .withUsername(email)
                            .withConfirmationCode(panel.getTempValue())
                            .withPassword(new String(panel.getPassword1()))
                            .withClientId(aws.getClientId());
                        ConfirmForgotPasswordResult confirmForgotResult = cognito.confirmForgotPassword(confirmForgotRequest);
                        logger.info("Confirm Forgot Password Result: " + confirmForgotResult);
                        
                        info(event.reference, 
                            String.format("Forgot password confirmed %s", email), 
                            "Reset Password");
                    } catch (Exception e) {
                        error(event.reference, "Cannot verify code " + panel.getTempValue(), "Forgot Password Error");
                        logger.error(e);
                        valid = false;
                    }
                } while(!valid);
            } catch (Exception e) {
                logger.error(e);
                error(event.reference, e.getMessage(), "Reset Password Error");
            }
        }
    }
    
    

}
