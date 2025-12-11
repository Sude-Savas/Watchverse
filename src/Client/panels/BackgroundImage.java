package Client.panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BackgroundImage extends JComponent {
    private Image background_image;

    public BackgroundImage() {
        try {
            background_image = ImageIO.read(new File("src/Client/assets/movie-background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth(); //backgroundImage component width
        int height = getHeight();

        g.drawImage(background_image, 0, 0, width, height, this);

    }

}
