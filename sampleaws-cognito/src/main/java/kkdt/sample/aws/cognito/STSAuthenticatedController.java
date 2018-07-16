/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;

import kkdt.sample.aws.cognito.event.AuthenticatedEvent;

// TODO: Not hooked up yet
public class STSAuthenticatedController extends CognitoController<AuthenticatedEvent> {
    private static final Logger logger = Logger.getLogger(STSAuthenticatedController.class);
    
    private AWSSecurityTokenService sts;
    
    public STSAuthenticatedController(@Value("${cognito.region:null}") String region) {
        super(region);
    }

    @Override
    public void onApplicationEvent(AuthenticatedEvent event) {
//        AWSCredentials awsCredentials = DefaultAWSCredentialsProviderChain.getInstance().getCredentials();
        AWSSecurityTokenServiceClientBuilder builder = AWSSecurityTokenServiceClientBuilder.standard();
        builder.setRegion(region);
        builder.setCredentials(event.awsCredentialsProvider);
        sts = builder.build();
        try {
            AssumeRoleRequest request = new AssumeRoleRequest()
                .withRoleArn("arn:aws:iam::997241245042:role/google_viewonly")
                .withRoleSessionName("Google")
                .withDurationSeconds(3600)
                .withExternalId("123ABC");
            
            AssumeRoleResult response = sts.assumeRole(request);
            logger.info(response);
        } catch (Exception e) {
            logger.error(e);
            error(event.reference, e.getMessage(), "STS Error");
        }
    }
    
}
