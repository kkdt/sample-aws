/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.context.ApplicationContext;

import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;

import kkdt.sample.aws.cognito.event.GuestAccessEvent;
import kkdt.sample.aws.cognito.event.LoginEvent;
import kkdt.sample.aws.cognito.event.SignUpEvent;
import kkdt.sample.aws.support.PasswordFieldUI;
import kkdt.sample.aws.support.SamplePanel;
import kkdt.sample.aws.support.TextFieldUI;

public class SampleConsole extends JFrame implements ActionListener {
    private static final long serialVersionUID = 5257690315704217352L;
    private static final Dimension btnDimension = new Dimension(85, 25);
    
    private final ApplicationContext applicationContext;
    
    private JButton loginBtn = new JButton("Login");
    private JButton signupBtn = new JButton("Signup");
    private JButton guestBtn = new JButton("Guest");
    private JButton createBtn = new JButton("Create");
    private JTextField username = new JTextField();
    private JPasswordField password = new JPasswordField();
    private AuthenticationResultType authentication;
    private Credentials credentials;
    
    public SampleConsole(String title, ApplicationContext applicationContext) {
        super(title);
        this.applicationContext = applicationContext;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public AuthenticationResultType getAuthentication() {
        return authentication;
    }

    public void setAuthentication(AuthenticationResultType authentication) {
        this.authentication = authentication;
    }

    public SampleConsole layoutComponents() {
        SamplePanel contents = new SamplePanel().contents(p -> {
            username.setUI(new TextFieldUI("Email", false, Color.lightGray));
            password.setUI(new PasswordFieldUI("Password", false, Color.lightGray));
            
            JPanel buttons = new JPanel();
            buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
            buttons.add(loginBtn);
            buttons.add(signupBtn);
            buttons.add(guestBtn);
            buttons.add(createBtn);
            
            loginBtn.setPreferredSize(btnDimension);
            signupBtn.setPreferredSize(btnDimension);
            guestBtn.setPreferredSize(btnDimension);
            createBtn.setPreferredSize(btnDimension);
            
            p.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            int y = 0;
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(username, c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(Box.createVerticalStrut(5), c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(password, c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.WEST;
            p.add(buttons, c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(Box.createHorizontalStrut(300), c);
            
            loginBtn.addActionListener(this);
            signupBtn.addActionListener(this);
            guestBtn.addActionListener(this);
            createBtn.addActionListener(this);
            
        }).layoutComponents();
        
        setContentPane(contents);
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
        case "Signup":
            applicationContext.publishEvent(new SignUpEvent(applicationContext, this));
            break;
        case "Login":
            applicationContext.publishEvent(new LoginEvent(applicationContext, this, username.getText().trim(), password.getPassword()));
            break;
        case "Guest":
            applicationContext.publishEvent(new GuestAccessEvent(applicationContext, this));
            break;
        case "Create":
            applicationContext.publishEvent(new SignUpEvent(applicationContext, this, true));
            break;
        case "Signout":
            break;
        case "Forgot Password":
            break;
        }
    }
    
    public void enableLogin(boolean enable) {
        loginBtn.setEnabled(enable);
    }
    
    public void enableSignup(boolean enable) {
        signupBtn.setEnabled(enable);
    }
    
    public void enableGuest(boolean enable) {
        guestBtn.setEnabled(enable);
    }
    
    public void enableCreate(boolean enable) {
        createBtn.setEnabled(enable);
    }
}
