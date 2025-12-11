package Client;

import Client.panels.BackgroundImage;
import Client.panels.LoginPanel;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {

        setSize(900,600);
        setResizable(false);
        setTitle("Watchverse");

        //login panel on background image
        BackgroundImage bg = new BackgroundImage();
        setContentPane(bg);
        bg.setLayout(new BorderLayout());
        bg.add(new LoginPanel(), BorderLayout.CENTER);

        //At center of the screen
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
