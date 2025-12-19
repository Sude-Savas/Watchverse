package Client.utils;

import javax.swing.*;

import java.awt.*;

import static Client.utils.UIConstants.*;

public final class UIMaker {


    private UIMaker() {
    }

    public static void styleLabel(JLabel label) {
        label.setForeground(LABEL_COLOR);
        label.setFont(LABEL_FONT);

        //label content left aligned
        label.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.LEFT);

        //without scaling the label, it doesn't align to left for some reason
        label.setMaximumSize(new Dimension(COMP_SIZE.width, label.getPreferredSize().height));
    }

    public static void styleLinkLabel(JLabel label) {
        label.setForeground(LINK_COLOR);
        label.setFont(LINK_FONT);
    }

    public static void styleField(JTextField field, boolean hasHintText) {
        field.setMaximumSize(COMP_SIZE);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setFont(FIELD_FONT);

        if (hasHintText) {
            field.setForeground(HINT_GRAY);
        }
    }

    public static void stylePasswordField(JPasswordField field, boolean hasHintText) {
        field.setMaximumSize(COMP_SIZE);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setFont(PASSWORDFIELD_FONT);

        //start state
        if (hasHintText) {
            field.setEchoChar((char) 0);
            field.setForeground(HINT_GRAY);
        }
    }


    public static void styleButton(JButton button) {
        button.setMaximumSize(COMP_SIZE);

        //default grey border looks ugly
        button.setFocusPainted(false);
        button.setBorderPainted(false);
    }

    public static void styleBackButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setForeground(LINK_COLOR);
        button.setFont(new Font("Segoe UI", Font.BOLD, 38));

    }
}
