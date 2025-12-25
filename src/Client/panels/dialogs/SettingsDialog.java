package Client.panels.dialogs;

import Client.frames.MainFrame;
import Client.utils.UIConstants;
import Client.utils.UIMaker;
import Model.AuthResult;
import Model.UserSession;
import Services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends BaseDialog {

    private JFrame frame;
    private final String currentUsername;
    private final AuthService authService;

    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JButton deleteAccountButton;

    public SettingsDialog(JFrame parent) {
        super(parent, new Dimension(400, 400), "Settings", "Save Changes");
        this.currentUsername = UserSession.getInstance().getUsername();
        authService = new AuthService();
        frame = parent;
    }

    @Override
    protected void addContent(Container container) {
        //Password change
        JLabel oldPasswordLabel = new JLabel("Old Password");
        JLabel newPasswordLabel = new JLabel("New Password");

        oldPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();

        UIMaker.styleLabel(oldPasswordLabel, Color.BLACK);
        UIMaker.styleLabel(newPasswordLabel, Color.BLACK);
        UIMaker.styleField(newPasswordField, false);
        UIMaker.styleField(oldPasswordField, false);

        container.add(oldPasswordLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(oldPasswordField);
        container.add(Box.createVerticalStrut(10));
        container.add(newPasswordLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(newPasswordField);
        container.add(Box.createVerticalStrut(10));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        container.add(separator);
        container.add(Box.createVerticalStrut(10));

        //Delete Account
        JLabel dangerLabel = new JLabel("Danger Zone");
        UIMaker.styleLabel(dangerLabel, UIConstants.DELETE);
        deleteAccountButton = new JButton("Delete Account");

        deleteAccountButton.setMaximumSize(new Dimension(270, 100));
        deleteAccountButton.setBorderPainted(false);
        deleteAccountButton.setFocusPainted(false);
        deleteAccountButton.setBackground(UIConstants.DELETE);
        deleteAccountButton.setAlignmentX(CENTER_ALIGNMENT);

        deleteAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteAccount();
            }
        });

        container.add(dangerLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(deleteAccountButton);
        container.add(Box.createVerticalGlue());
    }

    @Override
    protected void onConfirm() {
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());

        if (oldPass.isBlank() || newPass.isBlank()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AuthResult result = authService.changePassword(currentUsername, oldPass, newPass);

        switch (result) {
            case PASSWORD_UPDATED -> {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                dispose();
            }

            case WRONG_PASSWORD -> {
                JOptionPane.showMessageDialog(this, "Old password is incorrect!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            case SAME_PASSWORD -> {
                JOptionPane.showMessageDialog(this, "New password cannot be the same as old password.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }

            case WEAK_PASSWORD -> {
                JOptionPane.showMessageDialog(this,
                        """
                    Password must be at least 8 characters,
                    contain an uppercase letter and a number.
                    """,
                        "Weak Password", JOptionPane.WARNING_MESSAGE);
            }
            default -> JOptionPane.showMessageDialog(this,
                        "An error occurred.", "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void onDeleteAccount() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account?\nThis action cannot be undone!",
                "Delete Account",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean isDeleted = authService.deleteAccount(currentUsername);

            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Account deleted. Goodbye!");

                //dispose settings dialog
                dispose();

                if (frame != null) {
                    frame.dispose();
                }

                UserSession.getInstance().clearUserSession();
                new MainFrame();

            } else {
                JOptionPane.showMessageDialog(this, "Error deleting account.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}