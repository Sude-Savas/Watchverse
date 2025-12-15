package Client.panels;

import Client.frames.BaseFrame;
import Client.utils.UIBehavior;
import Client.utils.UIConstants;
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

    private BaseFrame frame;

    public ForgetPasswordPanel(BaseFrame frame) {
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
        add(password);
        add(Box.createVerticalStrut(10));
        add(newPassword);
        add(Box.createVerticalStrut(10));
        add(newPasswordAgain);
        add(Box.createVerticalStrut(20));

        add(resetButton);
    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField();

        password = new JLabel("Password");
        newPassword = new JPasswordField();
        newPassword.setText("Enter new password...");
        newPasswordAgain = new JPasswordField();
        newPasswordAgain.setText("Enter new password again...");

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
    }

}
