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
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class VerificationPanel extends JPanel {
    private static final long serialVersionUID = 7126902707323271066L;
    
    private final JTextField username = new JTextField();
    private final JTextField temp = new JTextField();
    private final JPasswordField password1 = new JPasswordField();
    private final JPasswordField password2 = new JPasswordField();
    
    public VerificationPanel(String username) {
        this(username, "Code/Temp Password", "New Password", "Confirm Password");
    }
    
    public VerificationPanel(String user, String hint, String password1Hint, String password2Hint) {
        username.setUI(new TextFieldUI(hint, false));
        username.setText(user);
        username.setEnabled(false);
        temp.setUI(new TextFieldUI(hint, false));
        password1.setUI(new PasswordFieldUI(password1Hint, false));
        password2.setUI(new PasswordFieldUI(password2Hint, false));
        initComponents();
    }
    
    public void setRequirePassword(boolean require) {
        password1.setEnabled(require);
        password2.setEnabled(require);
    }
    
    public void resetInputs() {
        username.setText("");
        temp.setText("");
        password1.setText("");
        password2.setText("");
    }
    
    public String getTempValue() {
        return temp.getText().trim();
    }
    
    public char[] getPassword1() {
        return password1.getPassword();
    }
    
    public char[] getPassword2() {
        return password2.getPassword();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int y = 0;
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(username, c);
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(temp, c);
        
        c.gridx = 0;
        c.gridy = y++;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JSeparator(JSeparator.HORIZONTAL), c);
        
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
