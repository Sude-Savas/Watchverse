package Client.panels;

import Client.frames.BaseFrame;
import Client.utils.UIBehavior;
import Client.utils.UIConstants;
import Client.utils.UIMaker;
import Model.AuthResult;
import Services.AuthService;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Users update their passwords here,
 * after answering the security question correct
 */
public class ResetPasswordPanel extends BaseAuthPanel {
    private JLabel password;
    private JLabel confirm;

    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton resetButton;

    private final String PASS_HINT = "Enter new password...";
    private final String CONFIRM_HINT = "Enter new password again...";

    private AuthService authService;
    private BaseFrame frame;

    public ResetPasswordPanel(BaseFrame frame) {
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

        add(password);
        add(Box.createVerticalStrut(10));
        add(newPasswordField);
        add(Box.createVerticalStrut(10));
        add(confirm);
        add(Box.createVerticalStrut(10));
        add(confirmPasswordField);
        add(Box.createVerticalStrut(20));

        add(resetButton);
    }

    @Override
    protected void resetFields() {
        UIMaker.resetPasswordField(newPasswordField, PASS_HINT);
        UIMaker.resetPasswordField(confirmPasswordField, CONFIRM_HINT);
    }

    private void setComponents() {
        password = new JLabel("Password");
        confirm = new JLabel("Confirm Password");

        newPasswordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        newPasswordField.setText(PASS_HINT);
        confirmPasswordField.setText(CONFIRM_HINT);

        resetButton = new JButton("Reset password");

    }

    private void setComponentStyles() {
        UIMaker.styleLabel(password, UIConstants.LABEL_COLOR);
        UIMaker.styleLabel(confirm, UIConstants.LABEL_COLOR);
        UIMaker.stylePasswordField(newPasswordField, true);
        UIMaker.stylePasswordField(confirmPasswordField, true);

        UIMaker.
                styleButton(resetButton);
        resetButton.setAlignmentX(CENTER_ALIGNMENT);

        setErrorLabel();
    }

    private void setEvents() {
        UIBehavior.setPasswordPlaceholder(newPasswordField, PASS_HINT);
        UIBehavior.setPasswordPlaceholder(confirmPasswordField, CONFIRM_HINT);

        resetButton.addActionListener((ActionEvent e) -> onReset());
    }

    private void onReset() {
        hideError();

        //this gives which user will be used
        String targetUser = ForgetPasswordPanel.verifiedUsername;

        if (targetUser == null) {
            showError("Something unexpected happened. Please try again.");
            frame.showScreen("LOGIN");
            return;
        }

        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (newPassword.isBlank() || newPassword.equals(PASS_HINT) ||
                confirmPassword.isBlank() || confirmPassword.equals(CONFIRM_HINT)) {
            showError("Please fill in all fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        if (!authService.isPasswordStrong(newPassword)) {
            JOptionPane.showMessageDialog(frame,
                    """
                    Password must be at least 8 characters,
                    contain an uppercase letter and a number.
                    """);
            return;
        }

        //actual password resetting
        AuthResult result = authService.forgotPassword(targetUser, newPassword);

        switch (result) {
            case PASSWORD_UPDATED -> {
                JOptionPane.showMessageDialog(frame, "Password reset successfully!");
                ForgetPasswordPanel.verifiedUsername = null;
                frame.showScreen("LOGIN");
            }
            case ERROR -> showError("Could not update password. Try again.");
        }
    }
}
