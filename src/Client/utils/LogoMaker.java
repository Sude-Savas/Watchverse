package Client.utils;

import javax.swing.*;
import java.awt.*;

public class LogoMaker {

    public static JLabel createLogo(int width, int height) {
        ImageIcon rawIcon = new ImageIcon("src/Client/assets/logo_placeholder.png"); //without scaling
        Image scaledImage = rawIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaledImage));
    }

    public static void addLogoTo(JPanel panel, int width, int height, float alignmentX) {
        JLabel logo = createLogo(width, height);
        logo.setAlignmentX(alignmentX);
        panel.add(logo);
    }
}
