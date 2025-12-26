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
    // Variables to keep track of the currently opened list for refresh and delete operations
    private String currentViewedListName;
    private String currentViewedListOwner;
    private int currentViewedListId = -1; // If -1 its personal list if not its a group list

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

    //For right panel
    private JLabel detailPoster;
    private JLabel detailTitle;
    private JLabel detailGenre;
    private JButton deleteButton;
    private Item currentSelectedItem;

    //for searchable public watchlists
    private JList<PublicWatchlist> publicWatchlists;
    private DefaultListModel<PublicWatchlist> publicListModel;
    private JTextField discoverSearchBar;


    private JPanel centerScreen;
    private CardLayout centerLayout;
    private JPanel centerPanel;

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
        refreshGroups();

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

        //Starting the new components
        detailPoster = new JLabel();
        detailPoster.setAlignmentX(CENTER_ALIGNMENT);

        detailTitle = new JLabel("Title");
        detailTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detailTitle.setAlignmentX(CENTER_ALIGNMENT);
        detailTitle.setHorizontalAlignment(SwingConstants.CENTER);

        detailGenre = new JLabel("Genre");
        detailGenre.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        detailGenre.setForeground(Color.GRAY);
        detailGenre.setAlignmentX(CENTER_ALIGNMENT);

        // Red delete button for removing items
        deleteButton = new JButton("Delete Item");
        deleteButton.setBackground(new Color(212, 34, 53));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteButton.setAlignmentX(CENTER_ALIGNMENT);
        deleteButton.setMaximumSize(new Dimension(200, 40));
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
        westScreen.setPreferredSize(new Dimension(310, 0)); // Sidebar width
        westScreen.setOpaque(false);
        westScreen.setLayout(new BoxLayout(westScreen, BoxLayout.Y_AXIS));
        westScreen.setAlignmentX(CENTER_ALIGNMENT);

        westScreen.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 15));

        // Added the help text instead of null.
        // This will automatically create the layout: Title - (?) - (+)
        westScreen.add(titleWithAdButton("My Watchlists", () -> {
            new AddWatchlist(frame).setVisible(true);
            refreshWatchlists();
        }, "Right-click on a list to delete it."));

        westScreen.add(Box.createVerticalStrut(10));

        //Slideable lists
        JScrollPane scrollWatchlist = new JScrollPane(watchlists);
        scrollWatchlist.setOpaque(false);
        scrollWatchlist.getViewport().setOpaque(false);
        scrollWatchlist.setBorder(null);
        westScreen.add(scrollWatchlist);

        westScreen.add(Box.createVerticalStrut(30));

        //groups
        //Info bubbles
        westScreen.add(titleWithAdButton("My Groups", () -> {
            //Options
            Object[] options = {"Create New Group", "Join with Code"};
            int choice = JOptionPane.showOptionDialog(frame,
                    "Do you want to create a new group or join an existing one?",
                    "Manage Groups",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == JOptionPane.YES_OPTION) {
                // 1. Create New Group
                new AddGroup(frame).setVisible(true);
                refreshGroups();
            } else if (choice == JOptionPane.NO_OPTION) {
                // 2. Join with Code
                String code = JOptionPane.showInputDialog(frame, "Enter the 6-digit Invite Code:");
                if (code != null && !code.trim().isEmpty()) {
                    //Freezing occurs so thread is used
                    new Thread(() -> {
                        String username = UserSession.getInstance().getUsername();
                        String command = "JOIN_GROUP###" + username + "###" + code.trim();

                        Object response = sendRequestToServer(command);
                        String resStr = (String) response;

                        SwingUtilities.invokeLater(() -> {
                            if (resStr != null && resStr.startsWith("SUCCESS")) {
                                String joinedGroupName = resStr.split(":")[1];
                                JOptionPane.showMessageDialog(frame, "You joined the group: " + joinedGroupName);
                                refreshGroups();
                            } else if ("NOT_FOUND".equals(resStr)) {
                                JOptionPane.showMessageDialog(frame, "Invalid code!", "Error", JOptionPane.ERROR_MESSAGE);
                            } else if ("ALREADY_MEMBER".equals(resStr)) {
                                JOptionPane.showMessageDialog(frame, "You are already in this group.");
                            } else {
                                JOptionPane.showMessageDialog(frame, "Failed to join.");
                            }
                        });
                    }).start();
                }
            }
        }, "Right-click to add a watchlist, get invite code, or DELETE the group."));

        westScreen.add(Box.createVerticalStrut(10));
        JScrollPane scrollGroup = new JScrollPane(groups);
        scrollGroup.setOpaque(false);
        scrollGroup.getViewport().setOpaque(false);
        scrollGroup.setBorder(null);
        westScreen.add(scrollGroup);

        westScreen.add(Box.createVerticalStrut(30));

        // Discover section for public watchlists
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

        // Default screen shown when no list is selected
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

        //Content state
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
        eastPanel.setPreferredSize(new Dimension(280, 0));

        //Empty state
        JPanel emptyState = new JPanel();
        emptyState.setOpaque(false);

        //Watchlist state
        watchlistTitle = new JLabel("Watchlist Name");
        watchlistType = new JLabel("Private");
        watchlistType.setForeground(Color.GRAY);
        watchlistCount = new JLabel("0 Items");
        watchlistCount.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel watchlistPanel = createDetailsPanel(watchlistTitle, watchlistType, watchlistCount);

        //Item detail state
        JPanel itemDetailPanel = createItemDetailPanel();

        eastPanel.add(emptyState, "EMPTY");
        eastPanel.add(watchlistPanel, "WATCHLIST_SUMMARY");
        eastPanel.add(itemDetailPanel, "ITEM_DETAILS");

        //First state is empty
        eastLayout.show(eastPanel, "EMPTY");

        add(eastPanel, BorderLayout.EAST);
    }

    //Panel maker method
    private JPanel createItemDetailPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        //Poster
        panel.add(detailPoster);
        panel.add(Box.createVerticalStrut(20));

        //Title
        panel.add(detailTitle);
        panel.add(Box.createVerticalStrut(10));

        //Genre
        panel.add(detailGenre);
        panel.add(Box.createVerticalStrut(40));

        //Delete Button
        panel.add(deleteButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    //This helper method creates the east of app which is details panels
    private JPanel createDetailsPanel(JLabel title, JLabel info1, JLabel info2) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        //Optimization
        panel.setBorder(BorderFactory.createEmptyBorder(50, 20, 0, 20));

        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(CENTER_ALIGNMENT);

        info1.setAlignmentX(Component.CENTER_ALIGNMENT);
        info1.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        info2.setAlignmentX(Component.CENTER_ALIGNMENT);
        info2.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(240, 2));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(25));
        panel.add(info1);
        panel.add(Box.createVerticalStrut(10));
        panel.add(info2);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    //this helper method will create watchlist and group list label
    private JPanel titleWithAdButton(String title, Runnable onAddAction, String helpText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);

        //For "?" and "+" button to be next to each other
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonContainer.setOpaque(false);

        //"?" helper
        if (helpText != null && !helpText.isEmpty()) {
            JLabel helpIcon = new JLabel("(?)");
            // Font enlarged
            helpIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
            helpIcon.setForeground(Color.GRAY);
            helpIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

            //Pop up text enlarged with HTML
            helpIcon.setToolTipText("<html><p style='font-size:15px; font-weight:bold; color:black;'>" + helpText + "</p></html>");

            buttonContainer.add(helpIcon);
        }

        JButton addButton = new JButton("+");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        addButton.setBackground(UIConstants.ADD_BUTTON_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);

        //Fixing the size issue
        addButton.setMargin(new Insets(0, 0, 0, 0));

        addButton.setPreferredSize(new Dimension(50, 40)); //Setting the button's size

        addButton.addActionListener(e -> {
            if (onAddAction != null) {
                onAddAction.run();
            }
        });

        buttonContainer.add(addButton);

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(buttonContainer, BorderLayout.EAST);

        return panel;
    }

    private void setEvents() {
        UIBehavior.setTextFieldPlaceholder(searchBar, SEARCH_HINT);
        UIBehavior.setTextFieldPlaceholder(discoverSearchBar, "Discover other people's watchlists...");

        // Search (Enter)
        searchBar.addActionListener(e -> {
            String query = searchBar.getText().trim();
            if (!query.isEmpty() && !query.equals(SEARCH_HINT)) {
                performSearch(query);
            }
        });

        profileButton.addActionListener(e -> popupMenu.show(profileButton, 0, profileButton.getHeight()));

        settingsItem.addActionListener(e -> new SettingsDialog(frame).setVisible(true));

        logoutItem.addActionListener(e -> {
            frame.dispose();
            new MainFrame();
            UserSession.getInstance().clearUserSession();
        });

        //Delete button
        deleteButton.addActionListener(e -> {
            if (currentSelectedItem == null) return;

            //If watchlist title is empty, no operation is done
            if (currentViewedListName == null || currentViewedListName.isEmpty()) return;

            // Ask user for confirmation before deleting
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete '" + currentSelectedItem.getTitle() + "' from list?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String username = UserSession.getInstance().getUsername();
                //Use memory(currectViewedListName)
                String command = "REMOVE_ITEM###" + username + "###" + currentViewedListName + "###" + currentSelectedItem.getApiId();
                Object response = sendRequestToServer(command);

                if ("SUCCESS".equals(response)) {
                    JOptionPane.showMessageDialog(this, "Item deleted.");

                    //Refresh
                    if (currentViewedListId != -1) {
                        loadSharedList(currentViewedListId, currentViewedListName, currentViewedListOwner);
                    } else {
                        loadWatchlist(currentViewedListName);
                    }

                    eastLayout.show(eastPanel, "WATCHLIST_SUMMARY");

                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        watchlists.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Left click (Selection)
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = watchlists.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        watchlists.setSelectedIndex(index);

                        groups.clearSelection();
                        publicWatchlists.clearSelection();

                        String selectedWatchlist = watchlists.getModel().getElementAt(index);
                        if (selectedWatchlist != null) {
                            loadWatchlist(selectedWatchlist);
                        }
                    }
                }
                // Open a popup menu to delete the selected watchlist
                else if (SwingUtilities.isRightMouseButton(e)) {
                    int index = watchlists.locationToIndex(e.getPoint());
                    if (index < 0) return;

                    // Auto select the item under cursor
                    watchlists.setSelectedIndex(index);
                    String selectedList = watchlists.getModel().getElementAt(index);

                    JPopupMenu listMenu = new JPopupMenu();
                    JMenuItem deleteItem = new JMenuItem("Delete List");

                    deleteItem.addActionListener(event -> {
                        int confirm = JOptionPane.showConfirmDialog(AppPanel.this,
                                "Are you sure you want to delete '" + selectedList + "'?",
                                "Delete Watchlist", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            //Thread to prevent freezing
                            new Thread(() -> {
                                String username = UserSession.getInstance().getUsername();
                                String command = "DELETE_LIST###" + username + "###" + selectedList;

                                Object response = sendRequestToServer(command);

                                SwingUtilities.invokeLater(() -> {
                                    if ("SUCCESS".equals(response)) {
                                        JOptionPane.showMessageDialog(AppPanel.this, "Watchlist deleted.");
                                        refreshWatchlists();
                                        eastLayout.show(eastPanel, "EMPTY");
                                        centerLayout.show(centerPanel, "EMPTY");
                                    } else {
                                        JOptionPane.showMessageDialog(AppPanel.this, "Failed to delete list.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                });
                            }).start();
                        }
                    });

                    listMenu.add(deleteItem);
                    listMenu.show(watchlists, e.getX(), e.getY());
                }
            }
        });


        groups.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = groups.locationToIndex(e.getPoint());
                if (index < 0) return;

                groups.setSelectedIndex(index);
                String selectedGroup = groups.getModel().getElementAt(index);

                //left click normal
                if (SwingUtilities.isLeftMouseButton(e)) {
                    watchlists.clearSelection();
                    publicWatchlists.clearSelection();
                    if (selectedGroup != null) loadGroup(selectedGroup);
                }

                //right click opens a menu to add the link-only watchlist to the group
                else if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu groupMenu = new JPopupMenu();

                    //Get invite code
                    JMenuItem inviteItem = new JMenuItem("Get Invite Code");
                    inviteItem.addActionListener(event -> {
                        String username = UserSession.getInstance().getUsername();
                        String command = "GET_GROUP_CODE###" + username + "###" + selectedGroup;
                        Object response = sendRequestToServer(command);

                        if (response instanceof String && !response.equals("ERROR") && !response.equals("null")) {
                            JTextArea textArea = new JTextArea((String) response);
                            textArea.setEditable(false);
                            JOptionPane.showMessageDialog(AppPanel.this, textArea, "Invite Code for " + selectedGroup, JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(AppPanel.this, "You are not the owner of this group (or error).");
                        }
                    });

                    //Add watchlist
                    JMenuItem addItem = new JMenuItem("Add Watchlist to Group");
                    addItem.addActionListener(event -> {
                        String username = UserSession.getInstance().getUsername();
                        Object response = sendRequestToServer("GET_LINK_ONLY_LISTS###" + username);

                        if (response instanceof java.util.List) {
                            java.util.List<String> eligibleLists = (java.util.List<String>) response;

                            if (eligibleLists.isEmpty()) {
                                JOptionPane.showMessageDialog(AppPanel.this, "You don't have any LINK_ONLY watchlists.");
                                return;
                            }

                            String selectedList = (String) JOptionPane.showInputDialog(
                                    AppPanel.this,
                                    "Select a watchlist to add to " + selectedGroup + ":",
                                    "Add Watchlist",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    eligibleLists.toArray(),
                                    eligibleLists.get(0));

                            if (selectedList != null) {
                                String command = "ADD_LIST_TO_GROUP###" + username + "###" + selectedGroup + "###" + selectedList;
                                Object res = sendRequestToServer(command);

                                if ("SUCCESS".equals(res)) {
                                    JOptionPane.showMessageDialog(AppPanel.this, "Watchlist added to group! ✓");
                                    loadGroup(selectedGroup);
                                } else {
                                    JOptionPane.showMessageDialog(AppPanel.this, "Failed to add.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    });

                    //Delete group
                    JMenuItem deleteItem = new JMenuItem("Delete Group");
                    deleteItem.addActionListener(event -> {
                        int confirm = JOptionPane.showConfirmDialog(AppPanel.this,
                                "Are you sure you want to delete group '" + selectedGroup + "'?",
                                "Delete Group", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            new Thread(() -> {
                                String username = UserSession.getInstance().getUsername();
                                String command = "DELETE_GROUP###" + username + "###" + selectedGroup;
                                Object response = sendRequestToServer(command);

                                SwingUtilities.invokeLater(() -> {
                                    if ("SUCCESS".equals(response)) {
                                        JOptionPane.showMessageDialog(AppPanel.this, "Group deleted.");
                                        refreshGroups();
                                        eastLayout.show(eastPanel, "EMPTY");
                                        centerLayout.show(centerPanel, "EMPTY");
                                    } else {
                                        JOptionPane.showMessageDialog(AppPanel.this, "Failed to delete group.");
                                    }
                                });
                            }).start();
                        }
                    });

                    groupMenu.add(inviteItem);
                    groupMenu.addSeparator();
                    groupMenu.add(addItem);
                    groupMenu.add(deleteItem);

                    groupMenu.show(groups, e.getX(), e.getY());
                }
            }
        });

        discoverSearchBar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String query = discoverSearchBar.getText().toLowerCase();
                publicListModel.clear();
                Object response = sendRequestToServer("GET_PUBLIC_LISTS");

                if (response instanceof java.util.List) {
                    java.util.List<PublicWatchlist> all = (java.util.List<PublicWatchlist>) response;
                    for (PublicWatchlist pw : all) {
                        if (pw.getName().toLowerCase().contains(query)) {
                            publicListModel.addElement(pw);
                        }
                    }
                }
            }
        });

        publicWatchlists.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                PublicWatchlist selected = publicWatchlists.getSelectedValue();
                if (selected != null) {

                    watchlists.clearSelection();
                    groups.clearSelection();

                    loadPublicListItems(selected.getId(), selected.getName());
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
        String command = "GET_MY_LISTS###" + currentUsername;

        java.util.List<String> myLists = (java.util.List<String>) sendRequestToServer(command);

        if (myLists != null) {
            DefaultListModel<String> model = new DefaultListModel<>();
            for (String listName : myLists) {
                model.addElement(listName);
            }
            watchlists.setModel(model); //Update Jlist
        }
    }

    public void refreshGroups() {
        String currentUsername = UserSession.getInstance().getUsername();
        String command = "GET_MY_GROUPS###" + currentUsername;

        java.util.List<String> myGroups = (java.util.List<String>) sendRequestToServer(command);

        if (myGroups != null) {
            DefaultListModel<String> model = new DefaultListModel<>();
            for (String gName : myGroups) {
                model.addElement(gName);
            }
            groups.setModel(model); // Update Jlist
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
                out.writeObject("SEARCH###" + query);
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

                itemButton.setPreferredSize(new Dimension(200, 320));
                itemButton.setLayout(new BorderLayout());
                itemButton.setMargin(new Insets(0, 0, 0, 0));
                itemButton.setContentAreaFilled(false);
                itemButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

                if (item.getPosterUrl() != null && !item.getPosterUrl().isEmpty()) {
                    ImageIcon icon = loadIconFromURL(item.getPosterUrl());
                    if (icon != null) {
                        itemButton.setIcon(icon);
                        itemButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                        itemButton.setHorizontalTextPosition(SwingConstants.CENTER);
                    }
                }

                String titleShort = item.getTitle().length() > 25 ? item.getTitle().substring(0, 22) + "..." : item.getTitle();
                String htmlText = "<html><center>" +
                        "<div style='padding-top:5px;'>" +
                        "<b style='font-size:12px; color:#333333;'>" + titleShort + "</b><br>" +
                        "<span style='font-size:10px; color:#777777;'>" + item.getGenres() + "</span>" +
                        "</div></center></html>";
                itemButton.setText(htmlText);
                itemButton.setFocusPainted(false);
                itemButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

                itemButton.addActionListener(ev -> {
                    // For search
                    if (isSearch) {
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

                        String command = "ADD_ITEM###" +
                                username + "###" +
                                selectedWatchlist + "###" +
                                item.getTitle() + "###" +
                                normalizedType + "###" +
                                item.getGenres() + "###" +
                                item.getApiId() + "###" +
                                (item.getPosterUrl() != null ? item.getPosterUrl() : "null");

                        Object response = sendRequestToServer(command);

                        if ("SUCCESS".equals(response)) {
                            JOptionPane.showMessageDialog(this, "Added to watchlist ✓");
                            loadWatchlist(selectedWatchlist);
                        } else if ("ALREADY_EXISTS".equals(response)) {
                            JOptionPane.showMessageDialog(this, "Item already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    //Looking at the list
                    else {
                        //If Public deletion button is active, if not show the details only

                        //Looking into watchlist (show detail)
                        currentSelectedItem = item;
                        detailTitle.setText("<html><center>" + item.getTitle() + "</center></html>");
                        detailGenre.setText(item.getGenres());
                        if (item.getPosterUrl() != null) {
                            ImageIcon icon = loadIconFromURL(item.getPosterUrl());
                            detailPoster.setIcon(icon);
                        } else {
                            detailPoster.setIcon(null);
                        }

                        //If user is the creator of the watchlist show delete button, if not don't.
                        String currentUser = UserSession.getInstance().getUsername();
                        boolean isOwner = currentUser.equals(currentViewedListOwner);

                        deleteButton.setVisible(isOwner);
                        deleteButton.setEnabled(isOwner);

                        eastLayout.show(eastPanel, "ITEM_DETAILS");
                    }
                });

                centerScreen.add(itemButton);
            }
        }

        if (centerLayout != null && centerPanel != null) {
            centerLayout.show(centerPanel, "CONTENT");
        }
        centerScreen.revalidate();
        centerScreen.repaint();
    }

    private void loadWatchlist(String watchlistName) {
        String username = UserSession.getInstance().getUsername();

        //Save into memory (for personal list)
        this.currentViewedListName = watchlistName;
        this.currentViewedListOwner = username;
        this.currentViewedListId = -1; //Because -1 means it is a personal list

        //Items Request
        String command = "GET_LIST_ITEMS###" + username + "###" + watchlistName;

        new Thread(() -> {
            // Get Items
            Object response = sendRequestToServer(command);

            //Visibility Request
            String visCommand = "GET_LIST_VISIBILITY###" + username + "###" + watchlistName;
            Object visResponse = sendRequestToServer(visCommand);
            String realVisibility = (visResponse instanceof String) ? (String) visResponse : "Private";

            if (response instanceof java.util.List) {
                java.util.List<Item> items =
                        (java.util.List<Item>) response;

                SwingUtilities.invokeLater(() -> {
                    watchlistTitle.setText(watchlistName);


                    // "LINK_ONLY" formatting
                    String cleanVisibility = realVisibility.replace("_", " ").toLowerCase();
                    String displayVisibility = cleanVisibility.substring(0, 1).toUpperCase() + cleanVisibility.substring(1);
                    watchlistType.setText(displayVisibility);

                    watchlistCount.setText(items.size() + " Items");

                    eastLayout.show(eastPanel, "WATCHLIST_SUMMARY");

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

        this.currentViewedListName = watchlistName;
        this.currentViewedListOwner = null;
        this.currentViewedListId = -1;

        String command = "GET_PUBLIC_LIST_ITEMS###" + watchlistId;

        new Thread(() -> {

            Object response = sendRequestToServer(command);

            if (response instanceof java.util.List) {

                java.util.List<Item> items =
                        (java.util.List<Item>) response;

                SwingUtilities.invokeLater(() -> {

                    // EAST PANEL
                    watchlistTitle.setText(watchlistName);
                    watchlistType.setText("Public");
                    watchlistCount.setText(items.size() + " Items");

                    eastLayout.show(eastPanel, "WATCHLIST_SUMMARY");

                    // Center panel
                    updateResultsUI(items, true, false);
                });
            }
        }).start();
    }

    private ImageIcon loadIconFromURL(String urlString) {
        if (urlString == null || urlString.isEmpty() || urlString.equals("null")) {
            return null;
        }

        try {
            URL url = new URL(urlString);
            Image image = ImageIO.read(url);
            if (image != null) {
                Image scaledImage = image.getScaledInstance(160, 240, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            //if is null, there is no image pass
        }
        return null;
    }

    private void loadGroup(String groupName) {
        String username = UserSession.getInstance().getUsername();

        // Update right panel
        SwingUtilities.invokeLater(() -> {
            watchlistTitle.setText(groupName);
            watchlistType.setText("Group Space");
            eastLayout.show(eastPanel, "WATCHLIST_SUMMARY");
        });

        centerScreen.removeAll();

        // Request watchlist from the server
        new Thread(() -> {
            Object response = sendRequestToServer("GET_GROUP_WATCHLISTS###" + username + "###" + groupName);

            if (response instanceof java.util.List) {
                java.util.List<String> groupLists = (java.util.List<String>) response;

                SwingUtilities.invokeLater(() -> {
                    watchlistCount.setText(groupLists.size() + " Linked Lists"); // Show numbers on the right panel

                    if (groupLists.isEmpty()) {
                        JLabel emptyLabel = new JLabel("No watchlists linked to this group yet.");
                        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                        emptyLabel.setForeground(Color.GRAY);
                        centerScreen.add(emptyLabel);
                    } else {

                        for (String entry : groupLists) {
                            String[] parts = entry.split(":");

                            if (parts.length < 3) continue;

                            int listId = Integer.parseInt(parts[0]);
                            String listName = parts[1];
                            String ownerName = parts[2];

                            JButton listButton = new JButton();
                            listButton.setPreferredSize(new Dimension(200, 150));
                            listButton.setLayout(new BorderLayout());
                            listButton.setFocusPainted(false);
                            listButton.setBackground(Color.WHITE);
                            listButton.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
                            listButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // El işareti çıksın

                            String html = "<html><center>" +
                                    "<h3 style='margin-bottom:5px; color:#333;'>" + listName + "</h3>" +
                                    "<span style='color:#777; font-size:10px;'>by " + ownerName + "</span>" +
                                    "</center></html>";

                            listButton.setText(html);

                            listButton.addActionListener(e -> {
                                loadSharedList(listId, listName, ownerName);
                            });

                            centerScreen.add(listButton);
                        }
                    }

                    //Update the Screen
                    if (centerLayout != null && centerPanel != null) {
                        centerLayout.show(centerPanel, "CONTENT");
                    }
                    centerScreen.revalidate();
                    centerScreen.repaint();
                });
            }
        }).start();
    }

    private void loadSharedList(int listId, String listName, String ownerName) {
        //Save into memory (group list)
        this.currentViewedListName = listName;
        this.currentViewedListOwner = ownerName;
        this.currentViewedListId = listId; //save ID


        SwingUtilities.invokeLater(() -> {
            watchlistTitle.setText(listName);
            watchlistType.setText("Shared by " + ownerName);
            eastLayout.show(eastPanel, "WATCHLIST");
        });

        new Thread(() -> {
            Object response = sendRequestToServer("GET_SHARED_LIST_ITEMS###" + listId);

            if (response instanceof java.util.List) {
                java.util.List<Item> items = (java.util.List<Item>) response;

                SwingUtilities.invokeLater(() -> {
                    watchlistCount.setText(items.size() + " Items");
                    updateResultsUI(items, true, false);
                });
            }
        }).start();
    }

}