/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.support;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegistrationPanel extends JPanel {
    private static final long serialVersionUID = -6568136871025679121L;
    
    private final JPasswordField password1 = new JPasswordField();
    private final JPasswordField password2 = new JPasswordField();
    private final JTextField email = new JTextField();
    private final JTextField firstName = new JTextField();
    private final JTextField lastName = new JTextField();
    private final JTextField dob = new JTextField();
    
    public RegistrationPanel() {
        initComponents();
    }
    
    public char[] getPassword1() {
        return password1.getPassword();
    }
    
    public char[] getPassword2() {
        return password2.getPassword();
    }
    
    public String getEmail() {
        return email.getText().trim();
    }
    
    public String getFirstName() {
        return firstName.getText().trim();
    }
    
    public String getLastName() {
        return lastName.getText().trim();
    }
    
    public String getBirthdate() {
        return dob.getText().trim();
    }
    
    private void initComponents() {
        password1.setUI(new PasswordFieldUI("Password", false));
        password2.setUI(new PasswordFieldUI("Confirm Password", false));
        email.setUI(new TextFieldUI("Email", false));
        firstName.setUI(new TextFieldUI("First Name", false));
        lastName.setUI(new TextFieldUI("Last Name", false));
        dob.setUI(new TextFieldUI("Birthdate MM/DD/YYY", false));
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int y = 0;
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(email, c);
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(firstName, c);
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lastName, c);
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dob, c);
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(password1, c);
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(password2, c);
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(Box.createHorizontalStrut(300), c);
        
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }
}
