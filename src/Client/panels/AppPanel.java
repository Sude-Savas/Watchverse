package Client.panels;

import Client.frames.AppFrame;
import Client.utils.UIMaker;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AppPanel extends JPanel {
    private JLabel watchlistLabel;
    private JLabel groupLabel;
    private JTextField searchBar;
    private JList<String> watchlists;
    private JList<String> groups;
    private String[] placeholders;
    private AppFrame frame;

    public AppPanel(AppFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        setBackground(Color.BLACK);
        placeholders = new String[5];

        Arrays.fill(placeholders, "Placeholder");

        setComponents();
        setComponentStyles();
        setComponentLayouts();
    }

    private void setComponents() {
        watchlistLabel = new JLabel("My Watchlists");
        groupLabel = new JLabel("My Groups");
        watchlists = new JList<>(placeholders);
        groups = new JList<>(placeholders);
        searchBar = new JTextField("Search movies or shows...");
    }

    private void setComponentLayouts() {

        //watchlist + group
        JPanel westScreen = new JPanel();
        westScreen.setOpaque(false);
        westScreen.setLayout(new BoxLayout(westScreen, BoxLayout.Y_AXIS));
        westScreen.setAlignmentX(CENTER_ALIGNMENT);

        westScreen.add(watchlistLabel);
        westScreen.add(Box.createVerticalStrut(20));
        westScreen.add(watchlists);
        westScreen.add(Box.createVerticalStrut(30));

        westScreen.add(groupLabel);
        westScreen.add(Box.createVerticalStrut(20));
        westScreen.add(groups);

        add(westScreen, BorderLayout.WEST);

        add(searchBar, BorderLayout.NORTH);

    }

    private void setComponentStyles() {
        watchlistLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        watchlistLabel.setForeground(Color.WHITE);
        groupLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        groupLabel.setForeground(Color.WHITE);

        watchlistLabel.setAlignmentX(CENTER_ALIGNMENT);
        watchlistLabel.setHorizontalAlignment(SwingConstants.LEFT);

        groupLabel.setAlignmentX(CENTER_ALIGNMENT);
        groupLabel.setHorizontalAlignment(SwingConstants.LEFT);

        watchlists.setMaximumSize(new Dimension(500, 300));
        groups.setMaximumSize(new Dimension(500, 300));

        watchlists.setAlignmentX(CENTER_ALIGNMENT);
        groups.setAlignmentX(CENTER_ALIGNMENT);


    }
}
