/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityResult;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import kkdt.sample.aws.cognito.event.LoginEvent;
import layout.SpringUtilities;
import net.minidev.json.JSONObject;

@Component
public class CognitoLoginController extends CognitoController<LoginEvent> {
    private static final Logger logger = Logger.getLogger(CognitoLoginController.class);
    
    public CognitoLoginController(@Value("${cognito.poolid}") String poolId, 
        @Value("${cognito.clientid}") String clientId,
        @Value("${cognito.region}") String region,
        @Value("${cognito.identitypool}") String identityPool,
        @Value("${cognito.providerid}") String identityProvider) 
    {
        super(poolId, clientId, region, identityPool, identityProvider);
    }
    
    /**
     * http://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
     * 
     *      "sub":"0d1113ba-ea37-494f-aeeb-11d1b6e2b43a",
     *      "email_verified":true,
     *      "birthdate":"",
     *      "iss":"https:\/\/cognito-idp.us-east-1.amazonaws.com\/us-east-1_bq8CzQ10z",
     *      "cognito:username":"",
     *      "given_name":"",
     *      "aud":"",
     *      "event_id":"5d84c1ed-7bf7-11e8-9cf8-cb3ce753205d",
     *      "token_use":"id",
     *      "auth_time":1530316299,
     *      "exp":1530319899,
     *      "iat":1530316299,
     *      "family_name":"",
     *      "email":""
     *      
     * @param payload
     */
    private void displayAuthentication(JSONObject payload) {
        String sub = String.valueOf(payload.get("sub"));
        String emailVerified = String.valueOf(payload.get("email_verified"));
        String dob = String.valueOf(payload.get("birthdate"));
        String iss = String.valueOf(payload.get("iss"));
        String cognitoUsername = String.valueOf(payload.get("cognito:username"));
        String firstName = String.valueOf(payload.get("given_name"));
        String aud = String.valueOf(payload.get("aud"));
        String eventId = String.valueOf(payload.get("event_id"));
        String tokenUse = String.valueOf(payload.get("token_use"));
        String authTime = String.valueOf(payload.get("auth_time"));
        String exp = String.valueOf(payload.get("exp"));
        String iat = String.valueOf(payload.get("iat"));
        String lastName = String.valueOf(payload.get("family_name"));
        String email = String.valueOf(payload.get("email"));
        
        JPanel panel = new JPanel(new SpringLayout());
        panel.add(new JLabel("UserId")); panel.add(new JLabel(sub));
        panel.add(new JLabel("Email")); panel.add(new JLabel(email));
        panel.add(new JLabel("Email verified")); panel.add(new JLabel(emailVerified));
        panel.add(new JLabel("Firstname")); panel.add(new JLabel(firstName));
        panel.add(new JLabel("Lastname")); panel.add(new JLabel(lastName));
        panel.add(new JLabel("DOB")); panel.add(new JLabel(dob));
        panel.add(new JLabel("Issuer")); panel.add(new JLabel(iss));
        panel.add(new JLabel("Cognito Username")); panel.add(new JLabel(cognitoUsername));
        panel.add(new JLabel("Authentication time")); panel.add(new JLabel(new Date(Long.parseLong(authTime) * 1000L).toString()));
        panel.add(new JLabel("IDToken expiration time")); panel.add(new JLabel(new Date(Long.parseLong(exp) * 1000L).toString()));
        panel.add(new JLabel("Issued time")); panel.add(new JLabel(new Date(Long.parseLong(iat) * 1000L).toString())); // Time at which the JWT was issued.
        panel.add(new JLabel("Token use")); panel.add(new JLabel(tokenUse));
        panel.add(new JLabel("Audience")); panel.add(new JLabel(aud));
        panel.add(new JLabel("Event ID")); panel.add(new JLabel(eventId));
        SpringUtilities.makeCompactGrid(panel, 14, 2, 0, 0, 10, 10);
        
        int strut = 5;
        JPanel content = new JPanel(new BorderLayout(5,5));
        content.add(panel, BorderLayout.CENTER);
        content.add(Box.createHorizontalStrut(strut), BorderLayout.WEST);
        content.add(Box.createHorizontalStrut(strut), BorderLayout.EAST);
        content.add(Box.createHorizontalStrut(strut), BorderLayout.NORTH);
        content.add(Box.createHorizontalStrut(strut), BorderLayout.SOUTH);
        content.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        JFrame userInfo = new JFrame("User Profile");
        userInfo.setLocationRelativeTo(null);
        userInfo.setContentPane(content);
        userInfo.pack();
        userInfo.setVisible(true); 
    }

    @Override
    public void onApplicationEvent(LoginEvent event) {
        try {
            AuthenticationResultType auth = authenticate(event.reference, event.email, event.password);
            
            // reassign the cognity identity provider service to the new authentication
            cognitoIdentity = AmazonCognitoIdentityClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(auth.getAccessToken(), auth.getIdToken())))
                .withRegion(this.region)
                .build();
            
            SignedJWT jwt = (SignedJWT)JWTParser.parse(auth.getIdToken());
            Payload payload = jwt.getPayload();
            JSONObject _payload = payload.toJSONObject();
            logger.info(_payload);
            displayAuthentication(_payload);
            
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
            
            SampleConsole console = event.reference;
            console.setCredentials(credentials);
            console.enableLogin(false);
            console.enableCreate(false);
            console.enableGuest(false);
            
        } catch (NotAuthorizedException e) {
            error(event.reference, "Not Authorized", "Guest Access Error");
        } catch (Exception e) {
            error(event.reference, e.getMessage(), "Guest Access Error");
        }
    }
}
