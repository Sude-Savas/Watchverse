package Client.panels;

import Client.frames.AppFrame;
import Client.utils.LogoMaker;
import Client.utils.UIBehavior;
import Model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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

    private JPanel centerPanel;
    private CardLayout centerLayout;
    private JPanel movieGridPanel;

    private final String SEARCH_HINT = "Search movies or shows...";

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
        searchBar = new JTextField();
        welcomeLabel = new JLabel("Welcome " + currentUser);

        char userFirstLetter = UserSession.getInstance().getUsername().toUpperCase().charAt(0);

        profileButton = new JButton(String.valueOf(userFirstLetter));


    }

    private void setComponentLayouts() {

        setBackground(new Color(230, 230, 250));
        //NORTH of the APP panel
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

        //WEST of the APP panel
        //watchlists
        JPanel westScreen = new JPanel();
        westScreen.setPreferredSize(new Dimension(280, 0)); //fixed width
        westScreen.setOpaque(false);
        westScreen.setLayout(new BoxLayout(westScreen, BoxLayout.Y_AXIS));
        westScreen.setAlignmentX(CENTER_ALIGNMENT);

        westScreen.add(watchlistLabel);
        westScreen.add(Box.createVerticalStrut(10));

        //slideable lists
        JScrollPane scrollWatchlist = new JScrollPane(watchlists);
        scrollWatchlist.setOpaque(false); //removing the color of the component
        scrollWatchlist.getViewport().setOpaque(false);
        scrollWatchlist.setBorder(null);
        westScreen.add(scrollWatchlist);

        westScreen.add(Box.createVerticalStrut(30));

        //groups

        westScreen.add(groupLabel);
        westScreen.add(Box.createVerticalStrut(10));
        JScrollPane scrollGroup = new JScrollPane(groups);
        scrollGroup.setOpaque(false);
        scrollGroup.getViewport().setOpaque(false);
        scrollGroup.setBorder(null);
        westScreen.add(scrollGroup);

        add(westScreen, BorderLayout.WEST);

        prepareCenterPanel();
    }

    private void setComponentStyles() {
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));

        profileButton.setPreferredSize(new Dimension(50, 50));
        profileButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        profileButton.setForeground(Color.WHITE);
        profileButton.setBackground(Color.DARK_GRAY);
        profileButton.setFocusPainted(false);
        profileButton.setBorderPainted(false);

        watchlistLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        groupLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));

        watchlists.setAlignmentX(CENTER_ALIGNMENT);
        groups.setAlignmentX(CENTER_ALIGNMENT);

    }

    private void setEvents() {
        UIBehavior.setTextFieldPlaceholder(searchBar, SEARCH_HINT);
    }

    private void prepareCenterPanel() {
        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setOpaque(false);

        //empty state, user didn't choose any watchlist or group
        JPanel emptyStatePanel = new JPanel();
        emptyStatePanel.setLayout(new BoxLayout(emptyStatePanel, BoxLayout.Y_AXIS));
        emptyStatePanel.setOpaque(false);

        JLabel emptyText = new JLabel("Select a watchlist or group to view content");
        emptyText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        emptyText.setForeground(Color.GRAY);
        emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyStatePanel.add(Box.createVerticalGlue());

        LogoMaker.addLogoTo(emptyStatePanel, "/Client/assets/film-strip.png", 100, 100, Component.CENTER_ALIGNMENT);

        emptyStatePanel.add(Box.createVerticalStrut(20));
        emptyStatePanel.add(emptyText);

        emptyStatePanel.add(Box.createVerticalGlue());


        //Content State
        movieGridPanel = new JPanel(new GridLayout(0, 4, 20, 20));

        movieGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane contentScroll = new JScrollPane(movieGridPanel);
        contentScroll.setOpaque(false);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.setBorder(null);

        centerPanel.add(emptyStatePanel, "EMPTY");
        centerPanel.add(contentScroll, "CONTENT");

        //starts with empty state
        centerLayout.show(centerPanel, "EMPTY");

        add(centerPanel, BorderLayout.CENTER);
    }
}
