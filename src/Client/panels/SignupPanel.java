package Client.panels;

import Client.utils.LogoMaker;

import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {
    private JLabel username;
    private JTextField usernameField;

    private JLabel password;
    private JPasswordField passwordField;
    private JPasswordField passwordAgainField;

    private JButton signupButton;


    public SignupPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // inside padding
        setOpaque(false);

        setComponents();
        setComponentLayouts();
        setComponentStyles();
    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField("Enter username...");
        password = new JLabel("Password");

        passwordField = new JPasswordField("Enter password...");
        passwordAgainField = new JPasswordField("Enter password again...");

        signupButton = new JButton("Sign Up");


    }

    private void setComponentLayouts() {
        //login
        add(Box.createVerticalStrut(20));
        LogoMaker.addLogoTo(this, 220, 220, CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(20));

        username.setAlignmentX(CENTER_ALIGNMENT);
        username.setHorizontalAlignment(SwingConstants.LEFT);
        usernameField.setAlignmentX(CENTER_ALIGNMENT);


        password.setAlignmentX(CENTER_ALIGNMENT);
        password.setHorizontalAlignment(SwingConstants.LEFT);
        passwordField.setAlignmentX(CENTER_ALIGNMENT);
        passwordAgainField.setAlignmentX(CENTER_ALIGNMENT);

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

        signupButton.setAlignmentX(CENTER_ALIGNMENT);
        add(signupButton);
        add(Box.createVerticalStrut(10));


    }

    private void setComponentStyles() {
        username.setForeground(Color.WHITE);
        password.setForeground(Color.WHITE);

        signupButton.setFocusPainted(false);
        signupButton.setBorderPainted(false);

        username.setMaximumSize(new Dimension(300, username.getPreferredSize().height));
        password.setMaximumSize(new Dimension(300, password.getPreferredSize().height));
        usernameField.setMaximumSize(new Dimension(300, 30));
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordAgainField.setMaximumSize(new Dimension(300, 30));
        signupButton.setMaximumSize(new Dimension(300, 30));

        Color hintTextGray = new Color(170, 170, 170);

        usernameField.setForeground(hintTextGray);
        passwordField.setForeground(hintTextGray);
        passwordAgainField.setForeground(hintTextGray);

        passwordField.setEchoChar((char)0); //to see the text, without this it shows just dots not the text
        passwordAgainField.setEchoChar((char)0);

        usernameField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        passwordField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        passwordAgainField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        username.setFont(new Font("Segoe UI", Font.BOLD, 15));
        password.setFont(new Font("Segoe UI", Font.BOLD, 15));

    }
}
