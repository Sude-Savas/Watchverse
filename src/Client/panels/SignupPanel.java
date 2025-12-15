package Client.panels;

import Client.frames.BaseFrame;
import Client.utils.UIBehavior;
import Client.utils.UIMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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

        enableBackButton(new Runnable() {
            @Override
            public void run() {
                frame.showScreen("LOGIN");
            }
        });

        setComponents();
        setComponentStyles();
        setEvents();

        add(username);
        add(Box.createVerticalStrut(5));
        add(usernameField);
        add(Box.createVerticalStrut(5));
        add(password);
        add(Box.createVerticalStrut(5));
        add(passwordField);
        add(Box.createVerticalStrut(10));
        add(passwordAgainField);
        add(Box.createVerticalStrut(20));

        add(signupButton);
    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField();
        usernameField.setText("Enter username...");
        password = new JLabel("Password");

        passwordField = new JPasswordField();
        passwordField.setText("Enter password...");
        passwordAgainField = new JPasswordField();
        passwordAgainField.setText("Enter password again...");

        signupButton = new JButton("Sign Up");

    }

    private void setComponentStyles() {
        UIMaker.styleLabel(username);
        UIMaker.styleLabel(password);

        UIMaker.styleButton(signupButton);
        signupButton.setAlignmentX(CENTER_ALIGNMENT);

        UIMaker.styleField(usernameField, true);


        UIMaker.stylePasswordField(passwordField, true);
        UIMaker.stylePasswordField(passwordAgainField, true);
    }


    private void setEvents() {
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSignup();
            }
        });

        UIBehavior.setTextFieldPlaceholder(usernameField, "Enter username...");
        UIBehavior.setPasswordPlaceholder(passwordField, "Enter password...");
        UIBehavior.setPasswordPlaceholder(passwordAgainField, "Enter password again...");
    }

    private void onSignup() {
        JOptionPane.showMessageDialog(frame, "Successfully registered");
        frame.showScreen("LOGIN");
    }
}

