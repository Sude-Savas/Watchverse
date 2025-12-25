package Client.panels;

import Client.frames.AppFrame;
import Client.frames.BaseFrame;
import Client.utils.UIConstants;
import Client.utils.UIMaker;
import Model.AuthResult;
import Services.AuthService;
import Model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class LoginPanel extends BaseAuthPanel {
    private JLabel username;
    private JTextField usernameField;

    private JLabel password;
    private JPasswordField passwordField;

    private JButton loginButton;
    private JLabel signupLabel;
    private JLabel forgotLabel;

    private AuthService authService;

    private BaseFrame frame; //only way to use class BaseFrame's showScreen

    public LoginPanel(BaseFrame frame) {
        super();
        this.frame = frame;
        authService = new AuthService();
        build();
    }

    @Override
    protected void build() {

        hideBackButton();

        setComponents();
        setComponentStyles();
        setEvents();

        //username label + username text field
        add(username);
        add(Box.createVerticalStrut(10));
        add(usernameField);
        add(Box.createVerticalStrut(10));

        //(password label + forgot label) + password text field
        JPanel passwordRow = new JPanel(new BorderLayout());
        passwordRow.setMaximumSize(new Dimension(UIConstants.COMP_SIZE));
        passwordRow.setAlignmentX(CENTER_ALIGNMENT);
        passwordRow.setOpaque(false); //panel has own color, to see the actual background disabling it

        passwordRow.add(password, BorderLayout.WEST);
        passwordRow.add(forgotLabel, BorderLayout.EAST);

        add(passwordRow);
        add(passwordField);
        add(Box.createVerticalStrut(10));

        add(loginButton);

        JPanel signupRow = new JPanel(new BorderLayout());
        signupRow.setMaximumSize(new Dimension(UIConstants.COMP_SIZE));
        signupRow.setOpaque(false);

        JLabel haveAccount = new JLabel("Don't have an account?");
        haveAccount.setForeground(UIConstants.LABEL_COLOR);

        signupRow.add(haveAccount, BorderLayout.WEST);
        signupRow.add(signupLabel, BorderLayout.EAST);
        signupRow.setAlignmentX(CENTER_ALIGNMENT);

        add(signupRow);

    }

    @Override
    protected void resetFields() {
        UIMaker.clearField(usernameField);
        UIMaker.clearPasswordField(passwordField);
    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField();

        password = new JLabel("Password");
        passwordField = new JPasswordField();

        loginButton = new JButton("Log In");
        forgotLabel = new JLabel("Forgot password?");
        signupLabel = new JLabel("Sign Up");

        setErrorLabel();

    }

    private void setComponentStyles() {
        //label colors
        UIMaker.styleLabel(username, UIConstants.LABEL_COLOR);
        UIMaker.styleLabel(password, UIConstants.LABEL_COLOR);

        UIMaker.styleField(usernameField, false);

        UIMaker.stylePasswordField(passwordField, false);

        //clickable labels
        UIMaker.styleLinkLabel(forgotLabel);
        UIMaker.styleLinkLabel(signupLabel);

        UIMaker.styleButton(loginButton);
        loginButton.setAlignmentX(CENTER_ALIGNMENT);

    }

    private void setEvents() {
        //it seems labels cannot use action listener, so I implemented mouse listener
        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onForgot();
            }
        });

        signupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onSignup();
            }

        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });

        //if user press enter at the password field, it will automatically click the login button
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEnter();
            }
        });

    }

    private void onForgot() {
        frame.showScreen("FORGOT");
    }

    private void onSignup() {
        frame.showScreen("SIGNUP");
    }

    private void onEnter() {
        loginButton.doClick();
    }

    private void onLogin() {
        hideError();

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword()); //getPassword returns char[]

        AuthResult result = authService.login(username, password);

        switch (result) {
            case SUCCESS -> {
                UserSession.getInstance().setUsername(username); //saves user to user session
                JOptionPane.showMessageDialog(frame, "Successful login");
                frame.dispose();
                new AppFrame();
            }
            case EMPTY_FIELDS -> showError("Please fill all the fields.");
            //for security, we didn't write which is wrong
            case USER_NOT_FOUND, WRONG_PASSWORD -> showError("Invalid username or password.");
            case ERROR -> showError("Something went wrong, try again.");
            default -> showError("An unexpected error occurred");
        }
    }
}
