package Client.panels;

import Client.frames.BaseFrame;
import Client.utils.LogoMaker;
import Client.utils.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePanel extends JPanel {
    private JLabel welcomeLabel;
    private JButton signupButton;
    private JButton loginButton;

    private BaseFrame frame;


    public WelcomePanel(BaseFrame frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setOpaque(false);

        setComponents();
        setComponentLayouts();
        setComponentStyles();
        setEvents();

    }

    private void setComponents() {
        welcomeLabel = new JLabel("Welcome to Watchverse!");
        loginButton = new JButton("Log In");
        signupButton = new JButton("Sign Up");
    }

    private void setComponentLayouts() {
        //left panel = logo + welcome label
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 20));

        leftPanel.add(Box.createVerticalStrut(20));
        LogoMaker.addLogoTo(leftPanel, "/Client/assets/logo_placeholder.png", 300, 300, LEFT_ALIGNMENT);
        leftPanel.add(Box.createVerticalStrut(20));

        welcomeLabel.setAlignmentX(LEFT_ALIGNMENT);
        leftPanel.add(welcomeLabel);

        //right panel = login button + signup button
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 40));


        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        signupButton.setAlignmentX(CENTER_ALIGNMENT);

        rightPanel.add(Box.createVerticalStrut(265));
        rightPanel.add(loginButton);
        rightPanel.add(Box.createVerticalStrut(40));
        rightPanel.add(signupButton);
        rightPanel.add(Box.createVerticalGlue()); //bottom space automatic

        leftPanel.setAlignmentX(CENTER_ALIGNMENT);
        rightPanel.setAlignmentX(CENTER_ALIGNMENT);

        //this makes the leftPanel and rightPanel same size
        JPanel center = new JPanel(new GridLayout(1, 2));
        center.setOpaque(false);
        center.add(leftPanel);
        center.add(rightPanel);

        add(center, BorderLayout.CENTER);
    }

    private void setComponentStyles() {

        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));

        styleButtons(loginButton);
        styleButtons(signupButton);

    }

    private void styleButtons(JButton button) {
        Color buttonPurple = UIConstants.BUTTON_COLOR;
        Dimension buttonSize = new Dimension(200,80);
        button.setBackground(buttonPurple);
        button.setMaximumSize(buttonSize);

        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
    }

    private void setEvents() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.showScreen("LOGIN");
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.showScreen("SIGNUP");
            }
        });
    }
}
