/** 
 * Copyright (C) 2018 thinh ho
 * This file is part of 'sample-aws' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.sample.aws.support;

import java.awt.BorderLayout;
import java.util.function.Consumer;

import javax.swing.JPanel;

public class SamplePanel extends JPanel {
    private static final long serialVersionUID = 5257690315704217352L;
    
    private final JPanel content = new JPanel();
    
    public SamplePanel contents(Consumer<JPanel> c) {
        if(c != null) {
            c.accept(content);
        }
        return this;
    }
    
    public SamplePanel layoutComponents() {
        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
        return this;
    }
}
