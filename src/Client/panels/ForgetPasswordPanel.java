package Client.panels;

import Client.frames.BaseFrame;
import Client.utils.UIBehavior;
import Client.utils.UIMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ForgetPasswordPanel extends BaseAuthPanel {
    private JLabel username;
    private JLabel password;
    private JTextField usernameField;
    private JPasswordField newPassword;
    private JPasswordField newPasswordAgain;
    private JButton resetButton;

    private final String USER_HINT = "Enter username...";
    private final String PASS_HINT = "Enter new password...";
    private final String CONFIRM_HINT = "Enter new password again...";

    private final BaseFrame frame;

    public ForgetPasswordPanel(BaseFrame frame) {
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
        add(newPassword);
        add(Box.createVerticalStrut(10));
        add(newPasswordAgain);
        add(Box.createVerticalStrut(20));

        add(resetButton);
    }

    @Override
    protected void resetFields() {
        UIMaker.resetField(usernameField, USER_HINT);
        UIMaker.resetPasswordField(newPassword, PASS_HINT);
        UIMaker.resetPasswordField(newPasswordAgain, CONFIRM_HINT);
    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField();
        usernameField.setText(USER_HINT);

        password = new JLabel("Password");
        newPassword = new JPasswordField();
        newPassword.setText(PASS_HINT);

        newPasswordAgain = new JPasswordField();
        newPasswordAgain.setText(CONFIRM_HINT);

        resetButton = new JButton("Reset password");

    }

    private void setComponentStyles() {
        UIMaker.styleLabel(username);
        UIMaker.styleLabel(password);

        UIMaker.styleField(usernameField, false);

        UIMaker.stylePasswordField(newPassword, true);
        UIMaker.stylePasswordField(newPasswordAgain, true);

        UIMaker.styleButton(resetButton);
        resetButton.setAlignmentX(CENTER_ALIGNMENT);

    }

    private void setEvents() {
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onReset();
            }
        });

        //focus listeners for password fields
        UIBehavior.setPasswordPlaceholder(newPassword, "Enter new password...");
        UIBehavior.setPasswordPlaceholder(newPasswordAgain, "Enter new password again...");
    }

    private void onReset() {
        JOptionPane.showMessageDialog(frame, "Password successfully reset");
        frame.showScreen("LOGIN");
    }

}
