package Client.panels;

import Client.frames.BaseFrame;
import Client.utils.UIMaker;

import javax.swing.*;
import java.awt.*;

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
        newPassword = new JPasswordField("Enter new password...");
        newPasswordAgain = new JPasswordField("Enter new password again...");

        resetButton = new JButton("Reset password");

    }

    private void setComponentStyles() {
        UIMaker.styleLabel(username);
        UIMaker.styleLabel(password);

        UIMaker.styleField(usernameField, false);

        UIMaker.stylePasswordField(newPassword, false, true);
        UIMaker.stylePasswordField(newPasswordAgain, false, true);

        UIMaker.styleButton(resetButton);
        resetButton.setAlignmentX(CENTER_ALIGNMENT);

    }

    private void setEvents() {

    }

}
