package Client;

import Client.panels.LoginPanel;

import javax.swing.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {

        setSize(900,600);
        setResizable(false);
        setTitle("Watchverse");

        LoginPanel loginPanel = new LoginPanel();
        add(loginPanel);

        //At center
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
