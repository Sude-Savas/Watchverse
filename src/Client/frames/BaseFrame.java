package Client.frames;

import Client.panels.*;

import javax.swing.*;
import java.awt.*;

public abstract class BaseFrame extends JFrame {
    //to switch panels in frame when an event occurred
    protected CardLayout cardLayout;
    protected JPanel container;

    //kendi başına çağrılmasın sadece kullanan sınıflar çağırsın diye protected
    protected BaseFrame() {
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Watchverse");

        BackgroundImage bg = new BackgroundImage();
        setContentPane(bg);
        bg.setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        container.setOpaque(false);

        //panels and which frame they run
        WelcomePanel welcome = new WelcomePanel(this);
        LoginPanel login = new LoginPanel(this);
        SignupPanel signup = new SignupPanel(this);
        ForgetPasswordPanel forgetPassword = new ForgetPasswordPanel(this);
        ResetPasswordPanel resetPassword = new ResetPasswordPanel(this);


        container.add(login, "LOGIN");
        container.add(signup, "SIGNUP");
        container.add(welcome, "WELCOME");
        container.add(resetPassword, "RESET");
        container.add(forgetPassword, "FORGOT");

        //panels on top of background
        bg.add(container, BorderLayout.CENTER);
            cardLayout.show(container, "WELCOME"); //first card, starting point of app

        //At center of the screen
        setLocationRelativeTo(null);
        setVisible(true);

    }

    // to change panels
    public void showScreen(String name) {
        cardLayout.show(container, name);
    }
}


