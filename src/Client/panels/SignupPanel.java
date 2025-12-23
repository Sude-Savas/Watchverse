package Client.panels;

import Client.frames.BaseFrame;
import Client.utils.UIBehavior;
import Client.utils.UIMaker;
import Model.AuthResult;
import Services.AuthService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SignupPanel extends BaseAuthPanel {
    private JLabel username;
    private JTextField usernameField;

    private JLabel password;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JButton signupButton;

    private final BaseFrame frame;
    private AuthService authService;

    private final String PASS_HINT = "Enter password...";
    private final String CONFIRM_HINT = "Enter password again...";

    public SignupPanel(BaseFrame frame) {
        super();
        this.frame = frame;
        authService = new AuthService();
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
        add(confirmPasswordField);
        add(Box.createVerticalStrut(20));

        add(signupButton);

    }

    @Override
    protected void resetFields() {
        UIMaker.clearField(usernameField);
        UIMaker.resetPasswordField(passwordField, PASS_HINT);
        UIMaker.resetPasswordField(confirmPasswordField, CONFIRM_HINT);
    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField();

        password = new JLabel("Password");
        passwordField = new JPasswordField();
        passwordField.setText(PASS_HINT);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setText(CONFIRM_HINT);

        signupButton = new JButton("Sign Up");

    }

    private void setComponentStyles() {
        UIMaker.styleLabel(username);
        UIMaker.styleLabel(password);

        UIMaker.styleButton(signupButton);
        signupButton.setAlignmentX(CENTER_ALIGNMENT);

        UIMaker.styleField(usernameField, true);

        UIMaker.stylePasswordField(passwordField, true);
        UIMaker.stylePasswordField(confirmPasswordField, true);

        setErrorLabel();
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
        UIBehavior.setPasswordPlaceholder(confirmPasswordField, "Enter password again...");
    }

    private void onSignup() {
        hideError();

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        // 1. Placeholder check, if they are default hint texts, accept them as empty
        boolean isUsernameEmpty = username.isBlank() || username.equals("Enter username...");
        boolean isPasswordEmpty = password.isBlank() || password.equals("Enter password...");
        boolean isConfirmEmpty = confirm.isBlank() || confirm.equals("Enter password again...");

        if (isUsernameEmpty || isPasswordEmpty || isConfirmEmpty) {
            showError("Please fill in all fields.");
            return;
        }

        //We didn't use switch-case because it didn't cover all errors for some reason

        //password match control
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        if (!authService.isPasswordStrong(password)) {
            JOptionPane.showMessageDialog(frame,
                    """
                    Password must be at least 8 characters,
                    contain an uppercase letter and a number.
                    """
            );
            return;
        }

        if (authService.isUserExists(username)) {
            showError("This username is already taken");
            return;
        }

        String securityQuestion = JOptionPane.showInputDialog(frame, "Enter your security question:");
        if (securityQuestion == null || securityQuestion.isBlank()) {
            showError("Security question cannot be empty.");
            return;
        }

        String securityAnswer = JOptionPane.showInputDialog(frame, "Enter your answer:");
        if (securityAnswer == null || securityAnswer.isBlank()) {
            showError("Security answer cannot be empty.");
            return;
        }

        AuthResult result = authService.register(username, password, securityQuestion, securityAnswer);

        if (result == AuthResult.SUCCESS) {
            JOptionPane.showMessageDialog(frame, "Successfully registered");
            frame.showScreen("LOGIN");
        } else {
            showError("Unexpected error occurred. Please try again.");
        }
    }
}


