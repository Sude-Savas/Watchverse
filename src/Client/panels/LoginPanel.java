package Client.panels;
import Client.utils.UIConstants;
import Client.utils.UIMaker;

import javax.swing.*;
import java.awt.*;


public class LoginPanel extends BaseAuthPanel {
    private JLabel username;
    private JTextField usernameField;

    private JLabel password;
    private JPasswordField passwordField;

    private JButton loginButton;
    private JLabel signupLabel;
    private JLabel forgotLabel;


    @Override
    protected void build() {

        setComponents();
        setComponentStyles();

        //username label + username text field
        add(username);
        add(Box.createVerticalStrut(10));
        add(usernameField);
        add(Box.createVerticalStrut(10));

        //(password label + forgot label) + password text field
        JPanel passwordRow = new JPanel(new BorderLayout());
        passwordRow.setMaximumSize(new Dimension(UIConstants.COMP_SIZE));
        passwordRow.setAlignmentX(CENTER_ALIGNMENT);
        passwordRow.setOpaque(false); //panel has own color, to see the actual background disabling it

        passwordRow.add(password, BorderLayout.WEST);
        passwordRow.add(forgotLabel, BorderLayout.EAST);

        add(passwordRow);
        add(passwordField);
        add(Box.createVerticalStrut(20));

        add(loginButton);
        add(Box.createVerticalStrut(10));

        JPanel signupRow = new JPanel(new BorderLayout());
        signupRow.setMaximumSize(new Dimension(UIConstants.COMP_SIZE));
        signupRow.setOpaque(false);

        JLabel haveAccount = new JLabel("Don't you have an account?");
        haveAccount.setForeground(UIConstants.LABEL_COLOR);

        signupRow.add(haveAccount, BorderLayout.WEST);
        signupRow.add(signupLabel, BorderLayout.EAST);
        signupRow.setAlignmentX(CENTER_ALIGNMENT);

        add(signupRow);

    }
    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField();

        password = new JLabel("Password");
        passwordField = new JPasswordField();

        loginButton = new JButton("Log In");
        forgotLabel = new JLabel("Forgot password?");
        signupLabel = new JLabel("Sign Up");

    }

    private void setComponentStyles() {
        //label colors
        UIMaker.styleLabel(username);
        UIMaker.styleLabel(password);

        UIMaker.styleField(usernameField, false);

        UIMaker.stylePasswordField(passwordField, true, false);

        //clickable labels
        UIMaker.styleLinkLabel(forgotLabel);
        UIMaker.styleLinkLabel(signupLabel);

        UIMaker.styleButton(loginButton);
        loginButton.setAlignmentX(CENTER_ALIGNMENT);

    }

}
