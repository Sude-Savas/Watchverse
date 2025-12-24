package Client.panels;

import Client.frames.AppFrame;
import Client.utils.UIMaker;
import Model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AppPanel extends JPanel {
    String currentUser;
    private JLabel watchlistLabel;
    private JLabel groupLabel;
    private JTextField searchBar;
    private JLabel welcomeLabel;
    private JButton profileButton;
    private JList<String> watchlists;
    private JList<String> groups;
    private String[] placeholders;
    private AppFrame frame;

    public AppPanel(AppFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        placeholders = new String[5];

        Arrays.fill(placeholders, "Placeholder");

        setComponents();
        setComponentStyles();
        setComponentLayouts();
    }

    private void setComponents() {
        currentUser = UserSession.getInstance().getUsername();
        watchlistLabel = new JLabel("My Watchlists");
        groupLabel = new JLabel("My Groups");
        watchlists = new JList<>(placeholders);
        groups = new JList<>(placeholders);
        searchBar = new JTextField("Search movies or shows...");
        welcomeLabel = new JLabel("Welcome " + currentUser);

        char userFirstLetter = UserSession.getInstance().getUsername().toUpperCase().charAt(0);

        profileButton = new JButton(String.valueOf(userFirstLetter));


    }

    private void setComponentLayouts() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        //prevents stretching to sides
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchContainer.setOpaque(false);

        searchBar.setPreferredSize(new Dimension(500, 40));
        searchBar.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        searchContainer.add(searchBar);
        headerPanel.add(searchContainer, BorderLayout.CENTER);

        JPanel profileContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        profileContainer.setOpaque(false);
        profileContainer.add(profileButton);

        headerPanel.add(profileContainer, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
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


    }

    private void setComponentStyles() {
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        welcomeLabel.setAlignmentX(LEFT_ALIGNMENT);
        welcomeLabel.setHorizontalAlignment(SwingConstants.LEFT);

        profileButton.setPreferredSize(new Dimension(50, 50));
        profileButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        profileButton.setForeground(Color.WHITE);
        profileButton.setBackground(Color.DARK_GRAY);
        profileButton.setFocusPainted(false);
        profileButton.setBorderPainted(false);

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
