/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import java.awt.Window;
import java.util.Objects;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;

import kkdt.sample.aws.cognito.event.CreateUserEvent;
import kkdt.sample.aws.cognito.event.EmailConfirmationEvent;
import kkdt.sample.aws.cognito.event.SignUpEvent;
import kkdt.sample.aws.support.RegistrationPanel;

/**
 * Cognito user sign-up process.
 * 
 * @author thinh ho
 *
 */
@Component
public class CognitoSignUpController extends CognitoController<SignUpEvent> {
    private static final Logger logger = Logger.getLogger(CognitoSignUpController.class);
    
    public CognitoSignUpController(@Value("${cognito.poolid}") String poolId, 
        @Value("${cognito.clientid}") String clientId,
        @Value("${cognito.region}") String region,
        @Value("${cognito.identitypool}") String identityPool,
        @Value("${cognito.providerid}") String identityProvider) 
    {
        super(poolId, clientId, region, identityPool, identityProvider);
    }
    
    /**
     * Admin user sign-up flow.
     * 
     * @param event
     */
    public void adminSignUp(SignUpEvent event) {
        String email = JOptionPane.showInputDialog(event.reference, 
            "Email: ", 
            "Sign Up (admin)", 
            JOptionPane.PLAIN_MESSAGE);
        
        if(Objects.nonNull(email) && !"".equals(email)) {
            AdminCreateUserRequest cognitoRequest = new AdminCreateUserRequest()
                .withUserPoolId(poolId)
                .withUsername(email)
                .withUserAttributes(
                    new AttributeType()
                        .withName("email")
                        .withValue(email),
                    new AttributeType()
                        .withName("email_verified")
                        .withValue("true"))
                .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .withForceAliasCreation(Boolean.FALSE);
            
            try {
                ApplicationContext source = event.getApplicationContext();
                AdminCreateUserResult result = cognito.adminCreateUser(cognitoRequest);
                source.publishEvent(new CreateUserEvent(source, event.reference, result.getUser()));
            } catch (Exception e) {
                error(event.reference, e.getMessage(), "Create User Error");
                logger.error(e);
            }
        }
    }
    
    public void signUp(SignUpEvent event) {
        RegistrationPanel inputs = new RegistrationPanel();
        boolean valid = true;
        do {
            int option = JOptionPane.showConfirmDialog(event.reference, 
                inputs, 
                "Sign Up", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
            switch(option) {
            case JOptionPane.CANCEL_OPTION:
                return; // short circuit
            default:
                valid = validInputs(event.reference, inputs);
                break;
            }
        } while(!valid);
        
        ApplicationContext source = event.getApplicationContext();
        SignUpRequest request = new SignUpRequest()
            .withClientId(clientId)
            .withUsername(inputs.getEmail())
            .withPassword(String.valueOf(inputs.getPassword1()))
            .withUserAttributes(
                new AttributeType()
                    .withName("email")
                    .withValue(inputs.getEmail()),
                new AttributeType()
                    .withName("given_name")
                    .withValue(inputs.getFirstName()),
                new AttributeType()
                    .withName("family_name")
                    .withValue(inputs.getLastName()),
                new AttributeType()
                    .withName("birthdate")
                    .withValue(inputs.getBirthdate()));
        
        try {
            SignUpResult result = cognito.signUp(request);
            logger.info(String.format("Sign Up: %s %s %s %s", result.getUserSub(), 
                result.getCodeDeliveryDetails().getDeliveryMedium(), 
                result.getCodeDeliveryDetails().getDestination(),
                result.getUserConfirmed()));
            
            info(event.reference,
                String.format("Email confirmation sent to %s", inputs.getEmail()),
                "Confirm Sign Up");
            
            source.publishEvent(new EmailConfirmationEvent(source, 
                event.reference, 
                inputs.getEmail()));
        } catch (Exception e) {
            error(event.reference, "Cannot sign up: " + inputs.getEmail(), "Sign Up Error");
            logger.error(e);
        }
    }
    
    @Override
    public void onApplicationEvent(SignUpEvent event) {
        if(event.admin) {
            adminSignUp(event);
        } else {
            signUp(event);
        }
    }
    
    private boolean validInputs(Window reference, RegistrationPanel inputs) {
        String err = null;
        if("".equals(inputs.getEmail())) {
            err = "Email is required";
        } else if("".equals(inputs.getFirstName()) || "".equals(inputs.getLastName())) {
            err = "First and last names are required";
        } else if("".equals(inputs.getBirthdate())) {
            err = "Birthdate is required";
        } else if("".equals(String.valueOf(inputs.getPassword1())) || "".equals(String.valueOf(inputs.getPassword2()))) {
            err = "Password is required";
        } else if(!Objects.equals(String.valueOf(inputs.getPassword1()), String.valueOf(inputs.getPassword1()))) {
            err = "Passwords do not match";
        }
        
        if(err != null) {
            error(reference, err, "Sign Up Error");
            return false;
        }
        
        return true;
    }
}
