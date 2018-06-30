package kkdt.sample.aws.support;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.JTextComponent;

/**
 * Idea taken from original source:
 * 
 * <blockquote>
 * https://stackoverflow.com/questions/1738966/java-jtextfield-with-input-hint
 * </blockquote>
 * 
 * @author thinh ho
 *
 */
public class PasswordFieldUI extends BasicPasswordFieldUI implements FocusListener {

    private final String hint;
    private final boolean hideOnFocus;
    private final Color color;
    
    public PasswordFieldUI() {
        this(null, false, null);
    }
    
    public PasswordFieldUI(String hint, boolean hideOnFocus) {
        this(hint, hideOnFocus, Color.lightGray);
    }
    
    public PasswordFieldUI(String hint, boolean hideOnFocus, Color color) {
        this.hint = hint;
        this.hideOnFocus = hideOnFocus;
        this.color = color;
    }

    private void repaint() {
        if(getComponent() != null) {
            getComponent().repaint();
        }
    }
    
    @Override
    protected void paintSafely(Graphics g) {
        super.paintSafely(g);
        JTextComponent comp = getComponent();
        if(hint != null && comp.getText().length() == 0 && (!(hideOnFocus && comp.hasFocus()))){
            if(color != null) {
                g.setColor(color);
            } else {
                g.setColor(comp.getForeground().brighter().brighter().brighter());
            }
            int padding = (comp.getHeight() - comp.getFont().getSize()) / 2;
            g.drawString(hint, 7, comp.getHeight() - padding - 1);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if(hideOnFocus) {
            repaint();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(hideOnFocus) {
            repaint();
        }
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        getComponent().addFocusListener(this);
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        getComponent().removeFocusListener(this);
    }

}
