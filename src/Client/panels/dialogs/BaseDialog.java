package Client.panels.dialogs;

import Client.utils.UIConstants;
import Client.utils.UIMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class BaseDialog extends JDialog {

    protected JButton confirmButton;
    protected JButton cancelButton;

    public BaseDialog(JFrame parent, Dimension size, String title, String confirmButtonText) {
        super(parent, title, true);
        setSize(size);
        setResizable(false);
        setLocationRelativeTo(parent);

        //Changed from default HIDE_ON_CLOSE to DISPOSE to free up memory resources when closed
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        ((JComponent) container).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //child class own contents
        addContent(container);
        container.add(Box.createVerticalStrut(20));
        addButtons(container, confirmButtonText);
    }

    private void addButtons(Container container, String confirmText) {

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        buttonPanel.setAlignmentX(CENTER_ALIGNMENT);

        cancelButton = new JButton("Cancel");
        confirmButton = new JButton(confirmText);

        styleButtons(confirmButton);
        confirmButton.setBackground(UIConstants.ADD_BUTTON_COLOR);

        styleButtons(cancelButton);
        cancelButton.setBackground(Color.GRAY);

        //same at all dialogs
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onConfirm(); //child class' own confirm
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        container.add(buttonPanel);

    }

    private void styleButtons(JButton button) {
        Dimension buttonSize = new Dimension(120, 40);
        button.setMaximumSize(buttonSize);

        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
    }

    protected abstract void addContent(Container container);
    protected abstract void onConfirm();
}
