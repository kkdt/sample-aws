/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import kkdt.sample.aws.cognito.event.AuthenticatedEvent;
import kkdt.sample.aws.support.SamplePanel;
import layout.SpringUtilities;
import net.minidev.json.JSONObject;

@Component
public class DisplayAuthenticatedSessionController extends CognitoController<AuthenticatedEvent> {
    private static final Logger logger = Logger.getLogger(DisplayAuthenticatedSessionController.class);
    private static final Dimension defaultSize = new Dimension(400, 100);
    
    private final JFrame frame = new JFrame("User Profile - N/A");
    private final SamplePanel contents = new SamplePanel();

    public DisplayAuthenticatedSessionController(@Value("${cognito.region:null}") String region) {
        super(region);
        initComponents();
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
    
    private void initComponents() {
        frame.setContentPane(contents);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(defaultSize);
    }

    @Override
    public void onApplicationEvent(AuthenticatedEvent event) {
        try {
            JSONObject _payload = null;
            if(event.idToken != null) {
                String _jwt = event.idToken;
                SignedJWT jwt = (SignedJWT)JWTParser.parse(_jwt);
                Payload payload = jwt.getPayload();
                _payload = payload.toJSONObject();
            }
            displayAuthentication(_payload);
        } catch (Exception e) {
            error(null, e.getMessage(), "Display Authentication Error");
            logger.error(e);
        }
    }
    
    /**
     * http://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
     * 
     *      Amazon User Pool
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
     *      Google
     *      "at_hash":"",
     *      "aud":"",
     *      "sub":"",
     *      "email_verified":true,
     *      "azp":"",
     *      "iss":"",
     *      "exp":1530730866,
     *      "iat":1530727266,
     *      "email":""
     *      
     * @param payload
     */
    private void displayAuthentication(JSONObject payload) {
        contents.removeAll();
        if(payload != null) {
            frame.setTitle("User Profile");
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
            panel.add(new JLabel("Authentication time"));
            try {
                panel.add(new JLabel(new Date(Long.parseLong(authTime) * 1000L).toString()));
            } catch (Exception e) {
                panel.add(new JLabel("N/A"));
            }
            panel.add(new JLabel("IDToken expiration time"));
            try {
                panel.add(new JLabel(new Date(Long.parseLong(exp) * 1000L).toString()));
            } catch (Exception e) {
                panel.add(new JLabel("N/A"));
            }
            panel.add(new JLabel("Issued time")); 
            try {
                panel.add(new JLabel(new Date(Long.parseLong(iat) * 1000L).toString())); // Time at which the JWT was issued.
            } catch (Exception e) {
                panel.add(new JLabel("N/A"));
            }
            panel.add(new JLabel("Token use")); panel.add(new JLabel(tokenUse));
            panel.add(new JLabel("Audience")); panel.add(new JLabel(aud));
            panel.add(new JLabel("Event ID")); panel.add(new JLabel(eventId));
            SpringUtilities.makeCompactGrid(panel, 14, 2, 0, 0, 10, 10);
            
            int strut = 5;
            contents.contents(p -> {
                p.setLayout(new BorderLayout(5,5));
                p.add(panel, BorderLayout.CENTER);
                p.add(Box.createHorizontalStrut(strut), BorderLayout.WEST);
                p.add(Box.createHorizontalStrut(strut), BorderLayout.EAST);
                p.add(Box.createHorizontalStrut(strut), BorderLayout.NORTH);
                p.add(Box.createHorizontalStrut(strut), BorderLayout.SOUTH);
                p.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            }).layoutComponents();
        }
        
        SwingUtilities.invokeLater(() -> {
            frame.pack();
            if(payload == null) {
                frame.setTitle("User Profile - N/A");
                frame.setSize(defaultSize);
            }
            frame.setVisible(true); 
        });
    }

}
