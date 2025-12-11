package Client;

import Client.panels.BackgroundImage;
import Client.panels.WelcomePanel;

import javax.swing.*;
import java.awt.*;

public class WelcomeFrame extends JFrame {
    public WelcomeFrame() {
        setTitle("Watchverse");
        setSize(900, 600);
        setResizable(false); //unchangeable size

        //same background used on login, signup and forgot password frames
        BackgroundImage bg = new BackgroundImage();
        setContentPane(bg);
        bg.setLayout(new BorderLayout());
        bg.add(new WelcomePanel(), BorderLayout.CENTER);

        setLocationRelativeTo(null); //center of screen
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
