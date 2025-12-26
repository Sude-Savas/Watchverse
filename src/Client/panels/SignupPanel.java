package Client.panels;

import Client.frames.BaseFrame;
import Client.utils.SecurityConstants;
import Client.utils.UIBehavior;
import Client.utils.UIConstants;
import Client.utils.UIMaker;
import Model.AuthResult;
import Services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SignupPanel extends BaseAuthPanel {
    //STEP 1: getting the username and password
    private JPanel step1Panel;
    private JLabel username;
    private JTextField usernameField;
    private JLabel password;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton nextButton;

    //STEP 2: getting the security answer and question
    private JPanel step2Panel;
    private JComboBox<String> securityQuestionBox;
    private JTextField securityAnswerField;
    private JButton signupButton;

    //To change between step1 and step2
    private JPanel cardPanel;
    private CardLayout cardLayout;

    //Temporary variable from step 1 to step 2
    private String tempUsername;
    private String tempPassword;

    private final BaseFrame frame;
    private AuthService authService;

    private final String USER_HINT = "Enter username...";
    private final String PASS_HINT = "Enter password...";
    private final String CONFIRM_HINT = "Enter password again...";
    private final String ANSWER_HINT = "Enter your answer...";


    public SignupPanel(BaseFrame frame) {
        super();
        this.frame = frame;
        authService = new AuthService();

        build();
    }

    @Override
    protected void build() {

        setComponents();
        setComponentStyles();
        setEvents();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        prepareStep1Panel();
        prepareStep2Panel();

        cardPanel.add(step1Panel, "STEP1");
        cardPanel.add(step2Panel, "STEP2");

        add(cardPanel);
        showStep1();
    }

    private void prepareStep1Panel() {
        step1Panel = new JPanel();
        step1Panel.setLayout(new BoxLayout(step1Panel, BoxLayout.Y_AXIS));
        step1Panel.setOpaque(false);

        step1Panel.add(username);
        step1Panel.add(Box.createVerticalStrut(4));
        step1Panel.add(usernameField);
        step1Panel.add(Box.createVerticalStrut(4));
        step1Panel.add(password);
        step1Panel.add(Box.createVerticalStrut(4));
        step1Panel.add(passwordField);
        step1Panel.add(Box.createVerticalStrut(8));
        step1Panel.add(confirmPasswordField);
        step1Panel.add(Box.createVerticalStrut(10));
        step1Panel.add(nextButton);

    }

    private void prepareStep2Panel() {
        step2Panel = new JPanel();
        step2Panel.setLayout(new BoxLayout(step2Panel, BoxLayout.Y_AXIS));
        step2Panel.setOpaque(false);

        step2Panel.add(Box.createVerticalStrut(20));
        step2Panel.add(securityQuestionBox);
        step2Panel.add(Box.createVerticalStrut(15));
        step2Panel.add(securityAnswerField);
        step2Panel.add(Box.createVerticalStrut(20));
        step2Panel.add(signupButton);


    }

    private void showStep1() {
        cardLayout.show(cardPanel, "STEP1");
        //back to log in screen
        enableBackButton(() -> frame.showScreen("LOGIN"));
    }

    private void showStep2() {
        cardLayout.show(cardPanel, "STEP2");

        //back to step1 panel
        enableBackButton(this::showStep1);
    }

    @Override
    protected void resetFields() {
        UIMaker.resetField(usernameField, USER_HINT);
        UIMaker.resetPasswordField(passwordField, PASS_HINT);
        UIMaker.resetPasswordField(confirmPasswordField, CONFIRM_HINT);

        if (securityQuestionBox.getItemCount() > 0) securityQuestionBox.setSelectedIndex(0);
        UIMaker.resetField(securityAnswerField, ANSWER_HINT);

        tempUsername = null;
        tempPassword = null;

        showStep1();
    }

    private void setComponents() {
        username = new JLabel("Username");
        usernameField = new JTextField();
        usernameField.setText(USER_HINT);

        password = new JLabel("Password");
        passwordField = new JPasswordField();
        passwordField.setText(PASS_HINT);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setText(CONFIRM_HINT);

        nextButton = new JButton("Next Step →");


        securityQuestionBox = new JComboBox<>(SecurityConstants.SECURITY_QUESTIONS);
        securityAnswerField = new JTextField(ANSWER_HINT);
        signupButton = new JButton("Sign Up");
    }

    private void setComponentStyles() {
        //Step 1 comps
        UIMaker.styleLabel(username, UIConstants.LABEL_COLOR);
        UIMaker.styleLabel(password, UIConstants.LABEL_COLOR);

        UIMaker.styleField(usernameField, true);
        UIMaker.stylePasswordField(passwordField, true);
        UIMaker.stylePasswordField(confirmPasswordField, true);

        UIMaker.styleButton(nextButton);
        nextButton.setAlignmentX(CENTER_ALIGNMENT);

        //Step 2 comps
        securityQuestionBox.setMaximumSize(Client.utils.UIConstants.COMP_SIZE);
        securityQuestionBox.setBackground(java.awt.Color.WHITE);

        UIMaker.styleField(securityAnswerField, true);

        UIMaker.styleButton(signupButton);
        signupButton.setAlignmentX(CENTER_ALIGNMENT);

        setErrorLabel();
    }


    private void setEvents() {

        UIBehavior.setTextFieldPlaceholder(usernameField, USER_HINT);
        UIBehavior.setPasswordPlaceholder(passwordField, PASS_HINT);
        UIBehavior.setPasswordPlaceholder(confirmPasswordField, CONFIRM_HINT);
        UIBehavior.setTextFieldPlaceholder(securityAnswerField, ANSWER_HINT);

        nextButton.addActionListener((ActionEvent e) -> onNextStep());
        signupButton.addActionListener((ActionEvent e) -> onSignup());
        confirmPasswordField.addActionListener((ActionEvent e) -> nextButton.doClick());
        securityAnswerField.addActionListener((ActionEvent e) -> signupButton.doClick());

    }


    private void onNextStep() {
        hideError();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        boolean isUsernameEmpty = username.isBlank() || username.equals(USER_HINT);
        boolean isPasswordEmpty = password.isBlank() || password.equals(PASS_HINT);

        if (isUsernameEmpty || isPasswordEmpty) {
            showError("Please fill in all fields.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }
        if (!authService.isPasswordStrong(password)) {
            JOptionPane.showMessageDialog(frame, "Password must be 8+ chars, 1 Upper, 1 Number.");
            return;
        }
        if (authService.isUserExists(username)) {
            showError("Username is already taken.");
            return;
        }

        this.tempUsername = username;
        this.tempPassword = password;

        showStep2();
    }

    private void onSignup() {
        hideError();
        String question = (String) securityQuestionBox.getSelectedItem();
        String answer = securityAnswerField.getText();

        if (securityQuestionBox.getSelectedIndex() == 0) {
            showError("Please select a security question.");
            return;
        }
        if (answer.isBlank() || answer.equals(ANSWER_HINT)) {
            showError("Please enter an answer.");
            return;
        }

        AuthResult result = authService.register(tempUsername, tempPassword, question, answer);

        switch (result) {
            case SUCCESS -> {
                JOptionPane.showMessageDialog(frame, "Account successfully created!");
                resetFields();
                frame.showScreen("LOGIN");
            }
            case ERROR -> showError("Registration failed. Try again.");
        }
    }
}



