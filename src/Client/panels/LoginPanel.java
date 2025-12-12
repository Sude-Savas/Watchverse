package Client.panels;
import Client.utils.LogoMaker;

import javax.swing.*;
import java.awt.*;


public class LoginPanel extends JPanel {
    private JLabel username;
    private JTextField usernameField;

    private JLabel password;
    private JPasswordField passwordField;

    private JButton loginButton;
    private JLabel signupLabel;
    private JLabel forgotLabel;

    public LoginPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // inside padding
        setOpaque(false); //to see our background image

        setComponents();
        setComponentLayouts();
        setComponentStyles();

    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField();

        password = new JLabel("Password");
        passwordField = new JPasswordField();

        loginButton = new JButton("Log In");
        forgotLabel = new JLabel("Forgot password?");
        signupLabel = new JLabel("Sign Up");

    }

    private void setComponentLayouts() {
        //with helper class, adding the logo
        add(Box.createVerticalStrut(20));
        LogoMaker.addLogoTo(this, 220, 220, CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(20));

        //username label + username text field
        username.setAlignmentX(CENTER_ALIGNMENT);
        username.setHorizontalAlignment(SwingConstants.LEFT); //label content left aligned
        usernameField.setAlignmentX(CENTER_ALIGNMENT);

        add(username);
        add(Box.createVerticalStrut(10));
        add(usernameField);
        add(Box.createVerticalStrut(10));

        //(password label + forgot label) + password text field
        JPanel passwordRow = new JPanel(new BorderLayout());
        passwordRow.setMaximumSize(new Dimension(300, 30));
        passwordRow.setOpaque(false); //panel has own color, to see the actual background disabling it

        passwordRow.add(password, BorderLayout.WEST);
        passwordRow.add(forgotLabel, BorderLayout.EAST);
        passwordField.setAlignmentX(CENTER_ALIGNMENT);

        add(passwordRow);
        add(passwordField);
        add(Box.createVerticalStrut(20));

        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        add(loginButton);
        add(Box.createVerticalStrut(10));

        JPanel signupRow = new JPanel(new BorderLayout());
        signupRow.setMaximumSize(new Dimension(300, 30));
        signupRow.setOpaque(false);

        JLabel haveAccount = new JLabel("Don't you have an account?");
        haveAccount.setForeground(Color.WHITE);

        signupRow.add(haveAccount, BorderLayout.WEST);
        signupRow.add(signupLabel, BorderLayout.EAST);

        signupRow.setAlignmentX(CENTER_ALIGNMENT);
        add(signupRow);

    }

    private void setComponentStyles() {
        //label colors
        username.setForeground(Color.WHITE);
        password.setForeground(Color.WHITE);

        Color labelBlue = new Color(46, 207, 255); //our own color
        forgotLabel.setForeground(labelBlue);
        signupLabel.setForeground(labelBlue);

        loginButton.setFocusPainted(false); //default grey border looks ugly
        loginButton.setBorderPainted(false);

        //without scaling the label, it doesn't align to left for some reason
        username.setMaximumSize(new Dimension(300, username.getPreferredSize().height));
        usernameField.setMaximumSize(new Dimension(300, 30));
        passwordField.setMaximumSize(new Dimension(300, 30));
        loginButton.setMaximumSize(new Dimension(300, 30));

        usernameField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        passwordField.setFont(new Font("Segoe UI", Font.BOLD, 17));
        username.setFont(new Font("Segoe UI", Font.BOLD, 15));
        password.setFont(new Font("Segoe UI", Font.BOLD, 15));
        forgotLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

    }

}
