package Client.panels.dialogs;

import javax.swing.*;
import java.awt.*;

public class AddGroup extends BaseDialog {

    private JTextField groupName;

    public AddGroup(JFrame frame) {
        super(frame, "Create Group", "Add Group");
    }

    @Override
    protected void addContent(Container container) {
        JLabel nameLabel = new JLabel("Group Name");
        groupName  = new JTextField();
    }

    @Override
    protected void onConfirm() {

    }
}
