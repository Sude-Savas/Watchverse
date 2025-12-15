package Client.panels;


import Client.utils.LogoMaker;
import Client.utils.UIMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public abstract class BaseAuthPanel extends JPanel {
    private JButton backButton = new JButton("←");
    protected BaseAuthPanel() {
        //unchanged parts in panels
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        setOpaque(false);

        UIMaker.styleBackButton(backButton);
        backButton.setAlignmentX(LEFT_ALIGNMENT);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.setOpaque(false);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        top.add(backButton);
        top.add(Box.createHorizontalGlue());

        add(top);

        add(Box.createVerticalStrut(20));
        LogoMaker.addLogoTo(this, 220, 220, CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(20));

    }

    protected abstract void build(); //each panel going to add their own components

    //only some panels will use back button
    protected void enableBackButton(Runnable action) {
        backButton.setVisible(true);
        onBack(action);
    }

    protected void hideBackButton() {
        backButton.setVisible(false);
    }

    //listener for back button
    private void onBack(Runnable action) {
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}