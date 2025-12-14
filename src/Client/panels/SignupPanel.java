package Client.panels;

import Client.frames.BaseFrame;
import Client.utils.UIMaker;
import Client.utils.UIConstants;

import javax.swing.*;
import java.awt.*;

public class SignupPanel extends BaseAuthPanel {
    private JLabel username;
    private JTextField usernameField;

    private JLabel password;
    private JPasswordField passwordField;
    private JPasswordField passwordAgainField;

    private JButton signupButton;

    private BaseFrame frame;

    public SignupPanel(BaseFrame frame) {
        super();
        this.frame = frame;
        build();
    }

    @Override
    protected void build() {
        setComponents();
        setComponentStyles();
        setEvents();

        add(username);
        add(Box.createVerticalStrut(10));
        add(usernameField);
        add(Box.createVerticalStrut(10));

        add(password);
        add(Box.createVerticalStrut(10));
        add(passwordField);
        add(Box.createVerticalStrut(10));
        add(passwordAgainField);
        add(Box.createVerticalStrut(20));

        add(signupButton);
        add(Box.createVerticalStrut(10));
    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField("Enter username...");
        password = new JLabel("Password");

        passwordField = new JPasswordField("Enter password...");
        passwordAgainField = new JPasswordField("Enter password again...");

        signupButton = new JButton("Sign Up");

    }

    private void setComponentStyles() {
        UIMaker.styleLabel(username);
        UIMaker.styleLabel(password);

        UIMaker.styleButton(signupButton);
        signupButton.setAlignmentX(CENTER_ALIGNMENT);

        UIMaker.styleField(usernameField, true);

        UIMaker.stylePasswordField(passwordField, false, true);
        UIMaker.stylePasswordField(passwordAgainField, false, true);
    }


    private void setEvents() {
    }

}
