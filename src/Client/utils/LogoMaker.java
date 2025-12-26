package Client.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Can create and add icons at panels with any size and path given
 */
public class LogoMaker {

    public static JLabel createLogo(String path, int width, int height) {
        URL imgURL = LogoMaker.class.getResource(path);

        ImageIcon rawIcon;
        if (imgURL != null) {
            rawIcon = new ImageIcon(imgURL); //without scaling
        } else {
            System.err.println("Unable to find image: " + path);
            return new JLabel("No image");
        }

        Image scaledImage = rawIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaledImage));
    }

    public static void addLogoTo(JPanel panel, String path, int width, int height, float alignmentX) {
        JLabel logo = createLogo(path, width, height);
        logo.setAlignmentX(alignmentX);
        panel.add(logo);
    }
}
