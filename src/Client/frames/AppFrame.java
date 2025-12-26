package Client.frames;

import Client.panels.AppPanel;

import javax.swing.*;

public class AppFrame extends JFrame {
    public AppFrame() {
        setTitle("Watchverse");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//full screen

        add(new AppPanel(this));
        setVisible(true);

    }
}
