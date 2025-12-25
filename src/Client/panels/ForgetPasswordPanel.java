package Client.panels;


/**
 * ForgetPasswordPanel starts the process of resetting password
 * User only enters their username, if user exists security question pops up
 * Finally, ResetPasswordPanel is opened to enter new password
 */

import Client.frames.BaseFrame;
import Client.utils.UIBehavior;
import Client.utils.UIConstants;
import Client.utils.UIMaker;
import Model.AuthResult;
import Services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ForgetPasswordPanel extends BaseAuthPanel {
    private JLabel username;
    private JTextField usernameField;
    private JButton verifyButton;
    private AuthService authService;

    private final String USER_HINT = "Enter username...";

    //static username to save user and move to reset password panel
    public static String verifiedUsername = null;

    private final BaseFrame frame;

    public ForgetPasswordPanel(BaseFrame frame) {
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
        add(Box.createVerticalStrut(10));
        add(usernameField);
        add(Box.createVerticalStrut(20));

        add(verifyButton);
    }

    @Override
    protected void resetFields() {
        UIMaker.resetField(usernameField, USER_HINT);
        verifiedUsername = null;
    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField();
        usernameField.setText(USER_HINT);

        verifyButton = new JButton("Reset password");

    }

    private void setComponentStyles() {
        UIMaker.styleLabel(username, UIConstants.LABEL_COLOR);

        UIMaker.styleField(usernameField, true);

        UIMaker.styleButton(verifyButton);
        verifyButton.setAlignmentX(CENTER_ALIGNMENT);

        setErrorLabel();
    }

    private void setEvents() {
        UIBehavior.setTextFieldPlaceholder(usernameField, USER_HINT);
        verifyButton.addActionListener((ActionEvent e) -> onVerify());

        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEnter();
            }
        });
    }

    private void onEnter() {
        verifyButton.doClick();
    }
    private void onVerify() {
        hideError();
        String username = usernameField.getText();

        if (username.isBlank() || username.equals(USER_HINT)) {
            showError("Please enter your username.");
            return;
        }

        if (!authService.isUserExists(username)) {
            showError("User not found");
            return;
        }

        String question = authService.getSecurityQuestion(username);
        String answer = JOptionPane.showInputDialog(frame,
                "Security Question: " + question + "\nAnswer:");

        //if user doesn't enter anything
        if (answer == null) {
            return;
        }

        AuthResult result = authService.verifySecurityAnswer(username, answer);

        switch (result) {
            case SUCCESS -> {
                verifiedUsername = username;
                frame.showScreen("RESET");
            }
            case WRONG_SECURITY_ANSWER -> showError("Security answer is wrong.");
        }
    }

}
