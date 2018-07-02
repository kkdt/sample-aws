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
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityResult;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;

import kkdt.sample.aws.cognito.event.AuthenticatedEvent;
import kkdt.sample.aws.cognito.event.LoginEvent;

@Component
public class CognitoLoginController extends CognitoController<LoginEvent> {
    private static final Logger logger = Logger.getLogger(CognitoLoginController.class);
    
    public CognitoLoginController(@Value("${cognito.region:null}") String region) {
        super(region);
    }
    
    @Override
    public void onApplicationEvent(LoginEvent event) {
        try {
            String identityPool = aws.getIdentityPool();
            // the provider the user chose
            String identityProvider = event.reference.getIdentityProvider();
            
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
            
            GetIdRequest idRequest = new GetIdRequest()
                .withIdentityPoolId(identityPool)
                .addLoginsEntry(identityProvider, auth.getIdToken());
            GetIdResult idResult = cognitoIdentity.getId(idRequest);
            logger.info(idResult);
            
            GetCredentialsForIdentityRequest credentialRequest = new GetCredentialsForIdentityRequest()
                .withIdentityId(idResult.getIdentityId())
                .addLoginsEntry(identityProvider, auth.getIdToken());
            GetCredentialsForIdentityResult credentialResult = cognitoIdentity.getCredentialsForIdentity(credentialRequest);
            Credentials credentials = credentialResult.getCredentials();
            logger.info(outputCredentials(credentials));
            
            // notify that the user has been fully authenticated
            ApplicationContext context = event.getApplicationContext();
            context.publishEvent(new AuthenticatedEvent(context, event.reference, authenticatedCredentials, credentials));
            
            SampleConsole console = event.reference;
            console.enableActions(false);
            console.enableInputs(false);
            
        } catch (NotAuthorizedException e) {
            error(event.reference, "Not Authorized", "Guest Access Error");
        } catch (Exception e) {
            error(event.reference, e.getMessage(), "Guest Access Error");
        }
    }
}
