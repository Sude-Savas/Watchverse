package Client.panels.dialogs;

import Client.utils.UIMaker;
import Model.UserSession; // Kullanıcı adını almak için ekledik

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AddWatchlist extends BaseDialog {

    private JTextField watchlistName;
    private JComboBox<String> typeBox;

    public AddWatchlist(JFrame frame) {
        super(frame, new Dimension(400, 250),"Create Watchlist", "Create");
    }

    @Override
    protected void addContent(Container container) {
        JLabel nameLabel = new JLabel("Watchlist Name");
        watchlistName = new JTextField();

        JLabel typeLabel = new JLabel("Type");
        typeBox = new JComboBox<>(new String[]{"Private", "Public", "Link-Only"});
        typeBox.setSelectedItem("Private"); //default

        UIMaker.styleLabel(nameLabel, Color.BLACK);
        UIMaker.styleField(watchlistName, false);

        UIMaker.styleLabel(typeLabel, Color.BLACK);
        UIMaker.styleComboBox(typeBox);

        //Info about link-only
        typeBox.addActionListener(e -> {
            String selected = (String) typeBox.getSelectedItem();

            if ("Link-Only".equals(selected)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Link-Only watchlists can be shared via link.\n" +
                                "You can create a group later if you want.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        container.add(nameLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(watchlistName);
        container.add(Box.createVerticalStrut(10));
        container.add(typeLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(typeBox);

    }

    @Override
    protected void onConfirm() {
        String name = watchlistName.getText().trim();
        String type = (String) typeBox.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a name.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Getting the username
        String currentUser = UserSession.getInstance().getUsername();

        // Type format
        String visibilityToSend = type.toUpperCase().replace("-", "_");

        //CREATE_LIST:username:listName:visibility
        String command = "CREATE_LIST:" + currentUser + ":" + name + ":" + visibilityToSend;

        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            //Sending to the server
            out.writeObject(command);
            out.flush();

            //Getting the response
            Object response = in.readObject();

            if ("SUCCESS".equals(response)) {
                //If its successful"
                System.out.println("Watchlist: " + name + " | Type: " + type + " is created.");
                JOptionPane.showMessageDialog(this, "List created successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create list.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}