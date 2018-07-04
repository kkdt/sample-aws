/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito.google;

import java.awt.Window;

import javax.swing.JOptionPane;

import com.google.api.client.googleapis.extensions.java6.auth.oauth2.GooglePromptReceiver;

import kkdt.sample.aws.support.VerificationPanel;

public class GoogleCodeVerification extends GooglePromptReceiver {
    private final Window reference;
    private final VerificationPanel panel;
    
    public GoogleCodeVerification(Window reference) {
        this.reference = reference;
        this.panel = new VerificationPanel("Google Login", "Code", "Password (N/A)", "Confirm Password (N/A)");
    }
    
    @Override
    public String waitForCode() {
        panel.setRequirePassword(false);
        JOptionPane.showMessageDialog(reference, 
            panel, 
            "Confirm Google Verification Code", 
            JOptionPane.PLAIN_MESSAGE);
        return panel.getTempValue();
    }
    
    public String getAuthorizationCode() {
        return panel.getTempValue();
    }
    
}
