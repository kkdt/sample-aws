/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;

import kkdt.sample.aws.cognito.event.AuthenticatedEvent;
import kkdt.sample.aws.cognito.event.LoginEvent;

@Component
public class CognitoLoginController extends CognitoController<LoginEvent> {
    private static final Logger logger = Logger.getLogger(CognitoLoginController.class);
    
    public CognitoLoginController(@Value("${cognito.region:null}") String region) {
        super(region);
    }
    
    protected boolean supportProvider(String provider) {
        return aws.getDefaultProvider().equals(provider);
    }
    
    @Override
    public void onApplicationEvent(LoginEvent event) {
        try {
            String identityProviderId = event.identityProviderId;
            
            if(!supportProvider(event.identityProviderName)) {
                logger.info(String.format("Provider %s is not supported by %s", 
                    event.identityProviderName, getClass().getSimpleName()));
                return ;
            }
            
            AuthenticationResultType auth = authenticate(event.email, event.password);
            AWSCredentials clientCredentials = new BasicAWSCredentials(auth.getAccessToken(), auth.getIdToken());
            AWSCredentialsProvider authenticatedCredentials = new AWSStaticCredentialsProvider(clientCredentials);
            
            // reassign with authenticated credentials
//            cognito = AWSCognitoIdentityProviderClientBuilder.standard()
//                .withCredentials(authenticatedCredentials)
//                .withRegion(this.region)
//                .build();
            
            // reassign the cognity identity provider service to the new authentication
//            cognitoIdentity = AmazonCognitoIdentityClientBuilder
//                .standard()
//                .withCredentials(authenticatedCredentials)
//                .withRegion(this.region)
//                .build();
            
            // Example identity id: region and user pool id below
            // cognito-idp.ap-southeast-2.amazonaws.com/ap-southeast-2_EPyUfpQq7
            
            Credentials credentials = getCredentials(identityProviderId, auth.getIdToken());
            logger.info(outputCredentials(credentials));
            
            // notify that the user has been fully authenticated
            ApplicationContext context = event.getApplicationContext();
            context.publishEvent(new AuthenticatedEvent(context, event.reference, 
                authenticatedCredentials.getCredentials().getAWSSecretKey(), 
                credentials));
            
            SampleConsole console = event.reference;
            console.enableActions(false);
            console.enableInputs(false);
            
        } catch (NotAuthorizedException e) {
            error(event.reference, "Not Authorized", "Guest Access Error");
        } catch (Exception e) {
            error(event.reference, e.getMessage(), "Guest Access Error");
        }
    }
    
    public AuthenticationResultType authenticate(String user, char[] password) {
        InitiateAuthRequest authRequest = new InitiateAuthRequest()
            .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
            .withClientId(aws.getClientId())
            .addAuthParametersEntry("USERNAME", user)
            .addAuthParametersEntry("PASSWORD", String.valueOf(password));
        
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
        
        InitiateAuthResult authResult = cognito.initiateAuth(authRequest);
        logger.info(outputAuthentication(authResult.getAuthenticationResult()));
        AuthenticationResultType auth = authResult.getAuthenticationResult();
        return auth;
    }
}
