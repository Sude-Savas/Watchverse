package Client.panels.dialogs;

import Client.utils.UIMaker;

import javax.swing.*;
import java.awt.*;

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

    }
}
