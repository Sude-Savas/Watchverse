package Client.panels;

import Client.frames.AppFrame;
import Client.frames.MainFrame;
import Client.panels.dialogs.AddGroup;
import Client.panels.dialogs.AddWatchlist;
import Client.panels.dialogs.SettingsDialog;
import Client.utils.LogoMaker;
import Client.utils.UIBehavior;
import Client.utils.UIConstants;
import Model.Item;
import Model.UserSession;
import Model.PublicWatchlist;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;

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
    private CardLayout eastLayout;
    private JPanel eastPanel;

    //watchlist info's for east panel
    private JLabel watchlistTitle;
    private JLabel watchlistType;
    private JLabel watchlistCount;

    //for searchable public watchlists
    private JList<PublicWatchlist> publicWatchlists;
    private DefaultListModel<PublicWatchlist> publicListModel;
    private JTextField discoverSearchBar;


    // --- YENİ EKLENEN DEĞİŞKENLER (Arama sonuçlarını yönetmek için gerekli) ---
    private JPanel centerScreen;       // Arama sonuçlarının listeleneceği panel (Grid yapısında)
    private CardLayout centerLayout;   // Ekranlar arası geçişi (Logo <-> Sonuçlar) sağlayan düzen
    private JPanel centerPanel;        // Ortadaki ana panel (CardLayout kullanan)
    // --------------------------------------------------------------------------

    private final String SEARCH_HINT = "Search movies or shows...";

    public AppPanel(AppFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(UIConstants.MAIN_APP_COLOR);
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
        loadPublicWatchlists();

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

        publicWatchlists = new JList<>();
        publicListModel = new DefaultListModel<>();
        publicWatchlists.setModel(publicListModel);

        discoverSearchBar = new JTextField();
        discoverSearchBar.setMaximumSize(new Dimension(UIConstants.COMP_SIZE));
        discoverSearchBar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        discoverSearchBar.setText("Discover other people's watchlists...");

    }

    private void setComponentLayouts() {
        buildNorthPanel();
        buildWestPanel();
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

    private void buildNorthPanel() {
        profileButton.setFocusPainted(false);

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

    }

    private void buildWestPanel() {
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

        westScreen.add(Box.createVerticalStrut(30));

        JLabel discoverTitle = new JLabel("Discover Watchlists");
        discoverTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        discoverTitle.setForeground(Color.DARK_GRAY);
        discoverTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        westScreen.add(discoverTitle);
        westScreen.add(Box.createVerticalStrut(10));
        westScreen.add(discoverSearchBar);
        westScreen.add(Box.createVerticalStrut(10));

        JScrollPane publicScroll = new JScrollPane(publicWatchlists);
        publicScroll.setOpaque(false);
        publicScroll.getViewport().setOpaque(false);
        publicScroll.setBorder(null);

        westScreen.add(publicScroll);

        add(westScreen, BorderLayout.WEST);

    }

    private void buildCenterPanel() {
        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setOpaque(false);

        // 1. EMPTY STATE PANEL
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

        // 2. CONTENT STATE
        centerScreen = new JPanel(new GridLayout(0, 4, 20, 20));
        centerScreen.setOpaque(false);
        centerScreen.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel alignerPanel = new JPanel(new BorderLayout());
        alignerPanel.setOpaque(false);
        alignerPanel.add(centerScreen, BorderLayout.NORTH);

        JScrollPane contentScroll = new JScrollPane(alignerPanel);
        contentScroll.setOpaque(false);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.setBorder(null);

        centerPanel.add(emptyStatePanel, "EMPTY");
        centerPanel.add(contentScroll, "CONTENT");

        centerLayout.show(centerPanel, "EMPTY");
        add(centerPanel, BorderLayout.CENTER);
    }

    private void buildEastPanel() {
        eastLayout = new CardLayout();
        eastPanel = new JPanel(eastLayout);
        eastPanel.setOpaque(false);
        eastPanel.setPreferredSize(new Dimension(250, 0));

        // 1. EMPTY STATE
        JPanel emptyState = new JPanel();
        emptyState.setOpaque(false);

        // 2. WATCHLIST STATE
        watchlistTitle = new JLabel("Watchlist Name");
        watchlistType = new JLabel("Private");
        watchlistType.setForeground(Color.GRAY);
        watchlistCount = new JLabel("0 Items");
        watchlistCount.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel watchlistPanel = createDetailsPanel(watchlistTitle, watchlistType, watchlistCount);

        eastPanel.add(emptyState, "EMPTY");
        eastPanel.add(watchlistPanel, "WATCHLIST");

        // First state is empty
        eastLayout.show(eastPanel, "EMPTY");

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
        UIBehavior.setTextFieldPlaceholder(
                discoverSearchBar,
                "Discover other people's watchlists..."
        );

        // Search (Enter)
        searchBar.addActionListener(e -> {
            String query = searchBar.getText().trim();
            if (!query.isEmpty() && !query.equals(SEARCH_HINT)) {
                performSearch(query);
            }
        });

        profileButton.addActionListener(e ->
                popupMenu.show(profileButton, 0, profileButton.getHeight())
        );

        settingsItem.addActionListener(e ->
                new SettingsDialog(frame).setVisible(true)
        );

        logoutItem.addActionListener(e -> {
            frame.dispose();
            new MainFrame();
            UserSession.getInstance().clearUserSession();
        });

        // My watchlists
        watchlists.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {

                publicWatchlists.clearSelection();

                String selectedWatchlist = watchlists.getSelectedValue();
                if (selectedWatchlist != null) {
                    loadWatchlist(selectedWatchlist);
                }
            }
        });


        // Discover search (public watchlists)
        discoverSearchBar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {

                String query = discoverSearchBar.getText().toLowerCase();
                publicListModel.clear();

                Object response = sendRequestToServer("GET_PUBLIC_LISTS");

                if (response instanceof java.util.List) {

                    java.util.List<PublicWatchlist> all =
                            (java.util.List<PublicWatchlist>) response;

                    for (PublicWatchlist pw : all) {
                        if (pw.getName().toLowerCase().contains(query)) {
                            publicListModel.addElement(pw);
                        }
                    }
                }
            }
        });

        // Public watchlist click
        publicWatchlists.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {

                //clears the watchlist selection
                watchlists.clearSelection();

                PublicWatchlist selected =
                        publicWatchlists.getSelectedValue();

                if (selected != null) {
                    loadPublicListItems(
                            selected.getId(),
                            selected.getName()
                    );
                }
            }
        });
    }


    private Object sendRequestToServer(String request) {
        try (Socket socket = new java.net.Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

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
        String currentUsername = UserSession.getInstance().getUsername();
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
            try (Socket socket = new Socket("localhost", 12345);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                //Command to server
                out.writeObject("SEARCH:" + query);
                out.flush();

                //Get the response
                Object response = in.readObject();

                if (response instanceof java.util.List) {
                    java.util.List<Item> results = (java.util.List<Item>) response;

                    SwingUtilities.invokeLater(() -> {
                        updateResultsUI(results, false, true);
                    });
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage())
                );
            }
        }).start();
    }

    //Return the request to the screen

    private void updateResultsUI(java.util.List<Item> items, boolean isPublic, boolean isSearch) {
        centerScreen.removeAll();

        if (items == null || items.isEmpty()) {
            JLabel noResult = new JLabel("No content found.");
            noResult.setFont(new Font("Segoe UI", Font.BOLD, 18));
            noResult.setForeground(Color.DARK_GRAY);
            centerScreen.add(noResult);
        } else {
            for (Model.Item item : items) {
                JButton itemButton = new JButton();

                // --- GÖRSEL AYARLAR ---
                itemButton.setPreferredSize(new Dimension(200, 320));
                itemButton.setLayout(new BorderLayout());
                itemButton.setMargin(new Insets(0, 0, 0, 0));
                itemButton.setContentAreaFilled(false);
                itemButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

                // Poster Yükleme
                if (item.getPosterUrl() != null && !item.getPosterUrl().isEmpty()) {
                    ImageIcon icon = loadIconFromURL(item.getPosterUrl());
                    if (icon != null) {
                        itemButton.setIcon(icon);
                        itemButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                        itemButton.setHorizontalTextPosition(SwingConstants.CENTER);
                    }
                }

                // HTML Yazı
                String titleShort = item.getTitle().length() > 25 ? item.getTitle().substring(0, 22) + "..." : item.getTitle();
                String htmlText = "<html><center>" +
                        "<div style='padding-top:5px;'>" +
                        "<b style='font-size:12px; color:#333333;'>" + titleShort + "</b><br>" +
                        "<span style='font-size:10px; color:#777777;'>" + item.getGenres() + "</span>" +
                        "</div></center></html>";
                itemButton.setText(htmlText);
                itemButton.setFocusPainted(false);
                itemButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // --- 1. SOL TIK (SADECE ARAMA MODUNDA EKLEME YAPAR) ---
                itemButton.addActionListener(ev -> {
                    if (!isSearch) return; // Kendi listemizdeysek sol tık işlevsiz

                    if (isPublic) {
                        JOptionPane.showMessageDialog(this, "Cannot modify public lists.");
                        return;
                    }

                    String selectedWatchlist = watchlists.getSelectedValue();
                    if (selectedWatchlist == null) {
                        JOptionPane.showMessageDialog(this, "Select a watchlist first.");
                        return;
                    }

                    String username = UserSession.getInstance().getUsername();
                    String normalizedType = (item.getType() != null && item.getType().toLowerCase().contains("tv")) ? "SERIES" : "MOVIE";

                    // Resim linkini de gönderiyoruz
                    String command = "ADD_ITEM:" +
                            username + ":" +
                            selectedWatchlist + ":" +
                            item.getTitle() + ":" +
                            normalizedType + ":" +
                            item.getGenres() + ":" +
                            item.getApiId() + ":" +
                            (item.getPosterUrl() != null ? item.getPosterUrl() : "null");

                    Object response = sendRequestToServer(command);

                    if ("SUCCESS".equals(response)) {
                        JOptionPane.showMessageDialog(this, "Added to watchlist ✅");
                    } else if ("ALREADY_EXISTS".equals(response)) {
                        JOptionPane.showMessageDialog(this, "Item already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                    }
                });

                // --- 2. SAĞ TIK MENÜSÜ (SİLME - SADECE KENDİ LİSTEMİZDE) ---
                if (!isSearch && !isPublic) {
                    JPopupMenu contextMenu = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Delete from List");
                    deleteItem.setForeground(Color.RED);
                    deleteItem.setFont(new Font("Segoe UI", Font.BOLD, 14));

                    deleteItem.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + item.getTitle() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            String selectedWatchlist = watchlists.getSelectedValue();
                            String username = UserSession.getInstance().getUsername();

                            String command = "REMOVE_ITEM:" + username + ":" + selectedWatchlist + ":" + item.getApiId();
                            Object response = sendRequestToServer(command);

                            if ("SUCCESS".equals(response)) {
                                JOptionPane.showMessageDialog(this, "Deleted.");
                                loadWatchlist(selectedWatchlist); // Listeyi yenile
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });

                    // Menüyü butona bağlıyoruz
                    itemButton.setComponentPopupMenu(contextMenu);
                }

                centerScreen.add(itemButton);
            }
        }
        // Ekranı tazele
        if (centerLayout != null && centerPanel != null) {
            centerLayout.show(centerPanel, "CONTENT");
        }
        centerScreen.revalidate();
        centerScreen.repaint();
    }

    private void loadWatchlist(String watchlistName) {

        String username = UserSession.getInstance().getUsername();
        String command = "GET_LIST_ITEMS:" + username + ":" + watchlistName;

        new Thread(() -> {
            Object response = sendRequestToServer(command);
            if (response instanceof java.util.List) {

                java.util.List<Item> items =
                        (java.util.List<Item>) response;

                SwingUtilities.invokeLater(() -> {

                    watchlistTitle.setText(watchlistName);
                    watchlistType.setText("Private");
                    watchlistCount.setText(items.size() + " Items");

                    eastLayout.show(eastPanel, "WATCHLIST");

                    updateResultsUI(items, false, false);
                });
            }
        }).start();
    }


    private void loadPublicWatchlists() {

        Object response = sendRequestToServer("GET_PUBLIC_LISTS");

        if (response instanceof java.util.List) {

            java.util.List<PublicWatchlist> lists =
                    (java.util.List<PublicWatchlist>) response;

            publicListModel.clear();
            for (PublicWatchlist pw : lists) {
                publicListModel.addElement(pw);
            }
        }
    }

    private void loadPublicListItems(int watchlistId, String watchlistName) {

        String command = "GET_PUBLIC_LIST_ITEMS:" + watchlistId;

        new Thread(() -> {

            Object response = sendRequestToServer(command);

            if (response instanceof java.util.List) {

                java.util.List<Item> items =
                        (java.util.List<Item>) response;

                SwingUtilities.invokeLater(() -> {

                    // EAST PANEL (detaylar)
                    watchlistTitle.setText(watchlistName);
                    watchlistType.setText("Public");
                    watchlistCount.setText(items.size() + " Items");
                    eastLayout.show(eastPanel, "WATCHLIST");

                    // CENTER PANEL (içerik)
                    updateResultsUI(items, true, false);
                });
            }
        }).start();
    }

    //Helper method
    // AppPanel.java içinde en alttaki yardımcı metod
    private ImageIcon loadIconFromURL(String urlString) {
        if (urlString == null || urlString.isEmpty() || urlString.equals("null")) {
            return null;
        }
        try {
            URL url = new URL(urlString);
            Image image = ImageIO.read(url);
            if (image != null) {
                // --- DEĞİŞİKLİK: Resmi daha büyük ölçekliyoruz ---
                // Kartın genişliğini dolduracak kadar büyük olmalı (Örn: 160x240)
                Image scaledImage = image.getScaledInstance(160, 240, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            // Hata olursa null döner, sorun yok
        }
        return null;
    }
}