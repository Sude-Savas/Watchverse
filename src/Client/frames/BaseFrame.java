package Client.frames;

import Client.panels.BackgroundImage;
import javax.swing.*;
import java.awt.*;

public abstract class BaseFrame extends JFrame {
    //kendi başına çağrılmasın sadece kullanan sınıflar çağırsın diye protected
    protected BaseFrame(JPanel panel) {
        setSize(900,600);
        setResizable(false);
        setTitle("Watchverse");

        //login panel on background image
        BackgroundImage bg = new BackgroundImage();
        setContentPane(bg);
        bg.setLayout(new BorderLayout());
        bg.add(panel, BorderLayout.CENTER);

        //At center of the screen
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}

