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

import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.RespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.UserType;

import kkdt.sample.aws.cognito.event.CreateUserEvent;
import kkdt.sample.aws.support.VerificationPanel;

@Component
public class CognitoVerificationController extends CognitoController<CreateUserEvent> {
    private static final Logger logger = Logger.getLogger(CognitoVerificationController.class);
    
    public CognitoVerificationController(@Value("${cognito.poolid}") String poolId, 
        @Value("${cognito.clientid}") String clientId,
        @Value("${cognito.region}") String region,
        @Value("${cognito.identitypool}") String identityPool,
        @Value("${cognito.providerid}") String identityProvider) 
    {
        super(poolId, clientId, region, identityPool, identityProvider);
    }
    
    public void doVerification(CreateUserEvent event) {
        UserType user = event.user;
        String email = user.getUsername();
        
        VerificationPanel contents = new VerificationPanel(email, "Code", "Password", "Confirm Password");
        boolean done = false;
        do {
            JOptionPane.showMessageDialog(event.reference, 
                contents, 
                "Sign-Up Verifiction: " + email, 
                JOptionPane.PLAIN_MESSAGE);
            
            String _password1 = new String(contents.getPassword1());
            String _password2 = new String(contents.getPassword2());
            
            if("".equals(_password1) || "".equals(_password2)
                || !Objects.equals(_password1, _password2))
            {
                JOptionPane.showInputDialog(event.reference, 
                    "New passwords do not match", 
                    "Confirm User: " + email, 
                    JOptionPane.ERROR_MESSAGE);
                done = false;
            } else {
                done = true;
            }
        } while(!done);
        
        /*
         * The authentication parameters. These are inputs corresponding to the 
         * AuthFlow that you are invoking.
         *  
         * The required values depend on the value of AuthFlow:
         * 
         * For USER_SRP_AUTH: 
         *      USERNAME (required), 
         *      SRP_A (required), 
         *      SECRET_HASH (required if the app client is configured with a client secret), 
         *      DEVICE_KEY
         * For REFRESH_TOKEN_AUTH/REFRESH_TOKEN: 
         *      REFRESH_TOKEN (required), 
         *      SECRET_HASH (required if the app client is configured with a client secret), 
         *      DEVICE_KEY
         * For CUSTOM_AUTH: 
         *      USERNAME (required), 
         *      SECRET_HASH (if app client is configured with client secret), 
         *      DEVICE_KEY
         */
        
        InitiateAuthRequest request = new InitiateAuthRequest()
            .withClientId(clientId)
            .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
            .withAuthParameters(null)
            .addAuthParametersEntry("USERNAME", email)
            .addAuthParametersEntry("PASSWORD", contents.getTempValue());
        InitiateAuthResult authResult = cognito.initiateAuth(request);
        
        RespondToAuthChallengeRequest challenge = new RespondToAuthChallengeRequest()
            .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
            .withClientId(clientId)
            .withSession(authResult.getSession())
            .addChallengeResponsesEntry("USERNAME", email)
            .addChallengeResponsesEntry("PASSWORD", contents.getTempValue())
            .addChallengeResponsesEntry("NEW_PASSWORD", new String(contents.getPassword1()));
        
        RespondToAuthChallengeResult challengeResponse = cognito.respondToAuthChallenge(challenge);
        outputAuthentication(challengeResponse);

    }
    
    private void outputAuthentication(RespondToAuthChallengeResult result) {
        AuthenticationResultType auth = result.getAuthenticationResult();
        StringBuilder b = new StringBuilder("Authentication");
        b.append(String.format("   Access Token: %s", auth.getAccessToken())).append("\n");
        b.append(String.format("   Expires In: %s ", auth.getExpiresIn())).append("\n");
        b.append(String.format("   ID Token: %s", auth.getIdToken())).append("\n");
        b.append(String.format("   Refresh Token: %s", auth.getRefreshToken())).append("\n");
        b.append(String.format("   Token Type: %s", auth.getTokenType())).append("\n");
        logger.info(b.toString());
    }

    @Override
    public void onApplicationEvent(CreateUserEvent event) {
        doVerification(event);
    }

}
