package Client.panels;

import Client.frames.AppFrame;
import Client.frames.MainFrame;
import Client.panels.dialogs.AddGroup;
import Client.panels.dialogs.AddWatchlist;
import Client.panels.dialogs.SettingsDialog;
import Client.utils.LogoMaker;
import Client.utils.UIBehavior;
import Client.utils.UIConstants;
import Model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class AppPanel extends JPanel {
    private JLabel watchlistLabel;
    private JLabel groupLabel;
    private JTextField searchBar;
    private JLabel welcomeLabel;
    private JButton profileButton;
    private JList<String> watchlists;
    private JList<String> groups;
    private JPopupMenu popupMenu;
    private JMenuItem settingsItem;
    private JMenuItem logoutItem;
    private AppFrame frame;

    // --- YENİ EKLENEN DEĞİŞKENLER (Arama sonuçlarını yönetmek için gerekli) ---
    private JPanel centerScreen;       // Arama sonuçlarının listeleneceği panel (Grid yapısında)
    private CardLayout centerLayout;   // Ekranlar arası geçişi (Logo <-> Sonuçlar) sağlayan düzen
    private JPanel centerPanel;        // Ortadaki ana panel (CardLayout kullanan)
    // --------------------------------------------------------------------------

    private final String SEARCH_HINT = "Search movies or shows...";

    public AppPanel(AppFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        setComponents();
        setComponentStyles();
        setComponentLayouts();
        setEvents();


        /*
         * focus from search bar to app panel
         * for some reason app starts with focused on search bar
         * and search bar placeholder text can't be seen because of this
         */
        this.setFocusable(true);
        this.requestFocusInWindow();
        refreshWatchlists();
    }

    private void setComponents() {
        String currentUser = UserSession.getInstance().getUsername();
        watchlistLabel = new JLabel("My Watchlists");
        groupLabel = new JLabel("My Groups");
        watchlists = new JList<>();
        groups = new JList<>();
        searchBar = new JTextField();
        searchBar.setText(SEARCH_HINT);
        //first letter uppercase other letters same
        String formattedName = currentUser.substring(0, 1).toUpperCase() + currentUser.substring(1);

        welcomeLabel = new JLabel("Welcome " + formattedName);
        profileButton = new JButton(String.valueOf(formattedName.charAt(0)));

        popupMenu = new JPopupMenu();
        settingsItem = new JMenuItem("Settings");
        logoutItem = new JMenuItem("Logout");

        popupMenu.add(settingsItem);
        popupMenu.addSeparator();
        popupMenu.add(logoutItem);


    }

    private void setComponentLayouts() {

        profileButton.setFocusPainted(false);

        setBackground(UIConstants.MAIN_APP_COLOR);
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

        westScreen.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 15));

        westScreen.add(titleWithAdButton("My Watchlists", () -> {
            new AddWatchlist(frame).setVisible(true);
            refreshWatchlists();
        }));

        westScreen.add(Box.createVerticalStrut(10));

        //slideable lists
        JScrollPane scrollWatchlist = new JScrollPane(watchlists);
        scrollWatchlist.setOpaque(false);
        scrollWatchlist.getViewport().setOpaque(false);
        scrollWatchlist.setBorder(null);
        westScreen.add(scrollWatchlist);

        westScreen.add(Box.createVerticalStrut(30));

        //groups
        westScreen.add(titleWithAdButton("My Groups", () -> {
            new AddGroup(frame).setVisible(true);
        }));

        westScreen.add(Box.createVerticalStrut(10));
        JScrollPane scrollGroup = new JScrollPane(groups);
        scrollGroup.setOpaque(false);
        scrollGroup.getViewport().setOpaque(false);
        scrollGroup.setBorder(null);
        westScreen.add(scrollGroup);

        add(westScreen, BorderLayout.WEST);

        buildCenterPanel();
        buildEastPanel();
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


    private void buildCenterPanel() {
        // --- GÜNCELLEME: Yerel değişken yerine sınıf değişkenlerini kullanıyoruz ---
        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout); // Sınıf değişkeni atandı
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
        // --- GÜNCELLEME: Buradaki paneli 'centerScreen' değişkenine atadık ki her yerden erişebilelim ---
        centerScreen = new JPanel(new GridLayout(0, 4, 20, 20));

        centerScreen.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerScreen.setOpaque(false); // Arka planı şeffaf yaptık

        JScrollPane contentScroll = new JScrollPane(centerScreen);
        contentScroll.setOpaque(false);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.setBorder(null);

        centerPanel.add(emptyStatePanel, "EMPTY");
        centerPanel.add(contentScroll, "CONTENT");

        //starts with empty state
        centerLayout.show(centerPanel, "EMPTY");

        add(centerPanel, BorderLayout.CENTER);
    }

    private void buildEastPanel() {
        CardLayout eastLayout = new CardLayout();
        JPanel eastPanel = new JPanel(eastLayout);
        eastPanel.setOpaque(false);
        eastPanel.setPreferredSize(new Dimension(250, 0));

        // 1. EMPTY STATE
        JPanel emptyState = new JPanel();
        emptyState.setOpaque(false);

        // 2. WATCHLIST STATE
        JLabel watchlistTitle = new JLabel("Watchlist Name");
        JLabel watchlistType = new JLabel("Private");
        watchlistType.setForeground(Color.GRAY);
        JLabel watchlistCount = new JLabel("0 Items");
        watchlistCount.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel watchlistPanel = createDetailsPanel(watchlistTitle, watchlistType, watchlistCount);

        // 3. GROUP STATE
        JLabel groupTitle = new JLabel("Group Name");
        JLabel groupAdmin = new JLabel("Admin: ...");
        JLabel groupMemberCount = new JLabel("1 Member");
        groupMemberCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel groupPanel = createDetailsPanel(groupTitle, groupAdmin, groupMemberCount);


        eastPanel.add(emptyState, "EMPTY");
        eastPanel.add(watchlistPanel, "WATCHLIST");
        eastPanel.add(groupPanel, "GROUP");

        // First state is empty
        eastLayout.show(eastPanel, "WATCHLIST");

        add(eastPanel, BorderLayout.EAST);
    }

    //this helper method creates the east of app which is details panels
    private JPanel createDetailsPanel(JLabel title, JLabel info1, JLabel info2) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 10, 0, 15));

        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(CENTER_ALIGNMENT);

        info1.setAlignmentX(Component.LEFT_ALIGNMENT);
        info2.setAlignmentX(Component.LEFT_ALIGNMENT);

        //separates infos
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(200, 2));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(15));
        panel.add(info1);
        panel.add(Box.createVerticalStrut(10));
        panel.add(info2);

        panel.add(Box.createVerticalGlue());

        return panel;

    }

    //this helper method will create watchlist and group list label
    private JPanel titleWithAdButton(String title, Runnable onAddAction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(CENTER_ALIGNMENT);

        //The panel covers every empty space it sees, fixing this with setting fixed height
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(Color.DARK_GRAY);

        JButton addButton = new JButton("+");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        addButton.setBackground(UIConstants.ADD_BUTTON_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);

        //listener for add buttons
        addButton.addActionListener(e -> {
            if (onAddAction != null) {
                onAddAction.run();
            }
        });

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.EAST);

        return panel;
    }

    private void setEvents() {

        UIBehavior.setTextFieldPlaceholder(searchBar, SEARCH_HINT);

        // --- YENİ EKLENEN EVENT: Arama çubuğunda Enter'a basılınca çalışır ---
        searchBar.addActionListener(e -> {
            String query = searchBar.getText().trim();

            if (!query.isEmpty() && !query.equals(SEARCH_HINT)) {
                performSearch(query);
            }
        });

        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(profileButton, 0, profileButton.getHeight());
            }
        });

        settingsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsDialog(frame).setVisible(true);
            }
        });

        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new MainFrame();
                UserSession.getInstance().clearUserSession();
            }
        });
    }

    private Object sendRequestToServer(String request) {
        try (java.net.Socket socket = new java.net.Socket("localhost", 12345);
             java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(socket.getOutputStream());
             java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream())) {

            out.writeObject(request);
            out.flush();
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Refreshes the watchlist and shows on the screen
    public void refreshWatchlists() {
        String currentUsername = Model.UserSession.getInstance().getUsername();
        //Command to the server
        String command = "GET_MY_LISTS:" + currentUsername;

        java.util.List<String> myLists = (java.util.List<String>) sendRequestToServer(command);

        if (myLists != null) {
            DefaultListModel<String> model = new DefaultListModel<>();
            for (String listName : myLists) {
                model.addElement(listName);
            }
            watchlists.setModel(model); //Update Jlist
        }
    }

    //Send server a search request
    private void performSearch(String query) {
        //Clean the screen while searching
        centerScreen.removeAll();
        centerScreen.revalidate();
        centerScreen.repaint();

        new Thread(() -> {
            try (java.net.Socket socket = new java.net.Socket("localhost", 12345);
                 java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(socket.getOutputStream());
                 java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream())) {

                //Command to server
                out.writeObject("SEARCH:" + query);
                out.flush();

                //Get the response
                Object response = in.readObject();

                if (response instanceof java.util.List) {
                    java.util.List<Model.Item> results = (java.util.List<Model.Item>) response;

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        updateResultsUI(results);
                    });
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                javax.swing.SwingUtilities.invokeLater(() ->
                        javax.swing.JOptionPane.showMessageDialog(this, "Arama hatası: " + ex.getMessage())
                );
            }
        }).start();
    }

    //Return the request to the screen
    private void updateResultsUI(java.util.List<Model.Item> items) {
        centerScreen.removeAll();

        //Grid Layout
        centerScreen.setLayout(new java.awt.GridLayout(0, 3, 15, 15));

        if (items == null || items.isEmpty()) {
            javax.swing.JLabel noResult = new javax.swing.JLabel("Sonuç bulunamadı.");
            noResult.setForeground(java.awt.Color.WHITE);
            noResult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            centerScreen.add(noResult);
        } else {

            for (Model.Item item : items) {

                //Set buttons
                javax.swing.JButton itemButton = new javax.swing.JButton();

                String buttonText = item.getTitle() + "  (" + item.getGenres() + ")";
                itemButton.setText(buttonText);

                //Download the picture and add
                if (item.getPosterUrl() != null && !item.getPosterUrl().isEmpty()) {
                    javax.swing.ImageIcon icon = loadIconFromURL(item.getPosterUrl());
                    if (icon != null) {
                        itemButton.setIcon(icon);
                        // Resmi yazının soluna, yazıyı sağa koy
                        itemButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
                        itemButton.setIconTextGap(15);
                    }
                }

                itemButton.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
                itemButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                itemButton.setFocusPainted(false);
                itemButton.setPreferredSize(new java.awt.Dimension(250, 110));

                itemButton.addActionListener(ev -> {
                    System.out.println("Seçilen: " + item.getTitle() + " (ID: " + item.getApiId() + ")");
                });

                //Add to the panel
                centerScreen.add(itemButton);
            }
        }

        //Change layout when there is results
        if (centerLayout != null && centerPanel != null) {
            centerLayout.show(centerPanel, "CONTENT");
        }

        //Refresh the screen to show buttons
        centerScreen.revalidate();
        centerScreen.repaint();
    }

    //Helper method
    private javax.swing.ImageIcon loadIconFromURL(String urlString) {
        if (urlString == null || urlString.isEmpty()) {
            return null;
        }
        try {
            java.net.URL url = new java.net.URL(urlString);
            java.awt.Image image = javax.imageio.ImageIO.read(url);
            if (image != null) {
                //Scaling the picture
                java.awt.Image scaledImage = image.getScaledInstance(50, 75, java.awt.Image.SCALE_SMOOTH);
                return new javax.swing.ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            //If there is no picture pass
        }
        return null;
    }
}