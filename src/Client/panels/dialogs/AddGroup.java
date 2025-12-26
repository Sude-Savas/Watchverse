package Client.panels.dialogs;

import Client.utils.UIMaker;
import Model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AddGroup extends BaseDialog {

    private JTextField groupName;

    public AddGroup(JFrame frame) {
        super(frame, new Dimension(400, 250) ,"Create Group", "Add Group");
    }

    @Override
    protected void addContent(Container container) {
        JLabel nameLabel = new JLabel("Group Name");
        groupName  = new JTextField();

        UIMaker.styleLabel(nameLabel, Color.BLACK);
        UIMaker.styleField(groupName, false);

        container.add(Box.createVerticalGlue());
        container.add(nameLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(groupName);
        container.add(Box.createVerticalGlue());
    }

    @Override
    protected void onConfirm() {
        String name = groupName.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Group name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = UserSession.getInstance().getUsername();
        //server command
        String command = "CREATE_GROUP:" + username + ":" + name;

        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(command);
            out.flush();

            String response = (String) in.readObject();

            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Group created successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create group.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}