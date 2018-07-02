/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityResult;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.GetIdentityProviderByIdentifierRequest;
import com.amazonaws.services.cognitoidp.model.GetIdentityProviderByIdentifierResult;

import kkdt.sample.aws.cognito.event.GuestAccessEvent;

@Component
public class GuestAccessController extends CognitoController<GuestAccessEvent> {
    private static final Logger logger = Logger.getLogger(GuestAccessController.class);
    
    public GuestAccessController(@Value("${cognito.region:null}") String region) {
        super(region);
    }

    @Override
    public void onApplicationEvent(GuestAccessEvent event) {
        try {
            GetIdResult identity = getUnauthenticatedIdentity(aws.getIdentityPool());
            String id = identity.getIdentityId();
            logger.info(identity);
            
            GetCredentialsForIdentityRequest credentialRequest = new GetCredentialsForIdentityRequest()
                .withIdentityId(id);
            GetCredentialsForIdentityResult credentialResult = cognitoIdentity.getCredentialsForIdentity(credentialRequest);
            logger.info(credentialResult);
            
            GetIdentityProviderByIdentifierResult identityProviderResult = cognito.getIdentityProviderByIdentifier(new GetIdentityProviderByIdentifierRequest()
                .withUserPoolId(aws.getPoolId())
                .withIdpIdentifier(aws.getIdentityPool()));
            logger.info(identityProviderResult);
        } catch (NotAuthorizedException e) {
            error(event.reference, "Not Authorized", "Guest Access Error");
        } catch (Exception e) {
            error(event.reference, e.getMessage(), "Guest Access Error");
        }
    }
    
    protected GetIdResult getUnauthenticatedIdentity(String identityPool) {
        /*
         * The available provider names for Logins are as follows:
         *      Facebook: graph.facebook.com
         *      Amazon Cognito Identity Provider: cognito-idp.us-east-1.amazonaws.com/us-east-1_123456789
         *      Google: accounts.google.com
         *      Amazon: www.amazon.com
         *      Twitter: api.twitter.com
         *      Digits: www.digits.com
         */
        
        GetIdRequest idrequest = new GetIdRequest()
            .withIdentityPoolId(identityPool);
        GetIdResult identity = cognitoIdentity.getId(idrequest);
        return identity;
    }
}
