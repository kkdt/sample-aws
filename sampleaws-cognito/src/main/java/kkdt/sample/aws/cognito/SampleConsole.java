/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.cognito;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.springframework.context.ApplicationContext;

import kkdt.sample.aws.cognito.event.GuestAccessEvent;
import kkdt.sample.aws.cognito.event.LoginEvent;
import kkdt.sample.aws.cognito.event.ResetPasswordEvent;
import kkdt.sample.aws.cognito.event.SignUpEvent;
import kkdt.sample.aws.support.PasswordFieldUI;
import kkdt.sample.aws.support.SamplePanel;
import kkdt.sample.aws.support.TextFieldUI;

public class SampleConsole extends JFrame implements ActionListener {
    private static final long serialVersionUID = 5257690315704217352L;
    private static final Dimension btnDimension = new Dimension(150, 25);
    
    private final ApplicationContext applicationContext;
    private final AWS aws;
    
    private JButton loginBtn = new JButton("Login");
    private JButton signupBtn = new JButton("Signup");
    private JButton guestBtn = new JButton("Guest");
    private JButton createBtn = new JButton("Create");
    private JButton resetPwdBtn = new JButton("Reset Password");
    private JTextField username = new JTextField();
    private JPasswordField password = new JPasswordField();
    private JComboBox<Entry<String, String>> providers = new JComboBox<>();
    
    public SampleConsole(String title, ApplicationContext applicationContext, AWS aws) {
        super(title);
        this.applicationContext = applicationContext;
        this.aws = aws;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    @SuppressWarnings("unchecked")
    public SampleConsole layoutComponents() {
        providers.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = -703245426245515436L;

            @Override
            public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus)
            {
                JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Entry<String, String> e = (Entry<String, String>)value;
                l.setText(e.getKey());
                return this;
            }
        });
        
        Set<Entry<String, String>> supportedProviders = aws.providers();
        supportedProviders.forEach(providers::addItem);
        Entry<String, String> def = supportedProviders.stream()
            .filter(e -> aws.getDefaultProvider().equals(e.getKey()))
            .findFirst()
            .orElse(null);
        providers.setSelectedItem(def);
        
        SamplePanel contents = new SamplePanel().contents(p -> {
            username.setUI(new TextFieldUI("Email", false));
            password.setUI(new PasswordFieldUI("Password", false));
            
            JPanel buttons1 = new JPanel();
            buttons1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            buttons1.add(loginBtn);
            buttons1.add(signupBtn);
            
            JPanel buttons2 = new JPanel();
            buttons2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            buttons2.add(guestBtn);
            buttons2.add(createBtn);
            buttons2.add(resetPwdBtn);
            
            loginBtn.setPreferredSize(btnDimension);
            signupBtn.setPreferredSize(btnDimension);
            guestBtn.setPreferredSize(btnDimension);
            createBtn.setPreferredSize(btnDimension);
            resetPwdBtn.setPreferredSize(btnDimension);
            
            p.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            int y = 0;
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 3;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(new JSeparator(JSeparator.HORIZONTAL), c);
            
            c.gridx = 0;
            c.gridy = y;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(new JLabel("Login As (ID Provider): "), c);
            
            c.gridx = 1;
            c.gridy = y;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(providers, c);
            
            c.gridx = 2;
            c.gridy = y++;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(Box.createHorizontalStrut(50), c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 3;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(Box.createVerticalStrut(5), c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 3;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(username, c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 3;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(Box.createVerticalStrut(5), c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 3;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(password, c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 3;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(new JSeparator(JSeparator.HORIZONTAL), c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 3;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.WEST;
            p.add(buttons1, c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 3;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.WEST;
            p.add(buttons2, c);
            
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth = 3;
            c.fill = GridBagConstraints.HORIZONTAL;
            p.add(Box.createHorizontalStrut(300), c);
            
            loginBtn.addActionListener(this);
            signupBtn.addActionListener(this);
            guestBtn.addActionListener(this);
            createBtn.addActionListener(this);
            resetPwdBtn.addActionListener(this);
            
            providers.addItemListener(e -> {
                switch(e.getStateChange()) {
                case ItemEvent.SELECTED:
                    Entry<String,String> entry = (Entry<String,String>)e.getItem();
                    boolean enable = true;
                    if(!entry.getKey().equals(aws.getDefaultProvider())) {
                        username.setText(entry.getKey());
                        password.setText("");
                        enable = false;
                    } else {
                        username.setText("");
                    }
                    username.setEnabled(enable);
                    password.setEnabled(enable);
                    
                    resetPwdBtn.setEnabled(enable);
                    guestBtn.setEnabled(enable);
                    createBtn.setEnabled(enable);
                    signupBtn.setEnabled(enable);
                    break;
                }
            });
            
        }).layoutComponents();
        
        setContentPane(contents);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
        case "Signup":
            applicationContext.publishEvent(new SignUpEvent(applicationContext, this));
            break;
        case "Login":
            Entry<String, String> selected = (Entry<String, String>)providers.getSelectedItem();
            applicationContext.publishEvent(new LoginEvent(applicationContext, this, 
                selected.getKey(),
                selected.getValue(),
                username.getText().trim(), 
                password.getPassword()));
            break;
        case "Guest":
            applicationContext.publishEvent(new GuestAccessEvent(applicationContext, this));
            break;
        case "Create":
            applicationContext.publishEvent(new SignUpEvent(applicationContext, this, true));
            break;
        case "Reset Password":
            applicationContext.publishEvent(new ResetPasswordEvent(applicationContext, this));
            break;
        }
    }
    
    public void enableActions(boolean enable) {
        loginBtn.setEnabled(enable);
        signupBtn.setEnabled(enable);
        guestBtn.setEnabled(enable);
        createBtn.setEnabled(enable);
        resetPwdBtn.setEnabled(enable);
    }
    
    public void enableInputs(boolean enable) {
        username.setEnabled(enable);
        password.setEnabled(enable);
        providers.setEnabled(enable);
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
