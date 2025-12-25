package Client.panels.dialogs;

import Client.utils.UIMaker;
import javax.swing.*;
import java.awt.*;


public class AddWatchlist extends BaseDialog {

    private JTextField watchlistName;
    private JComboBox<String> typeBox;

    public AddWatchlist(JFrame frame) {
        super(frame, "Create Watchlist", "Create");
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
        String name = watchlistName.getText();
        String type = (String) typeBox.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a name.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Watchlist: " + name + " | Type: " + type + " is created.");
        dispose();
    }
}
