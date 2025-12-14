package Client.panels;


import Client.utils.LogoMaker;

import javax.swing.*;


public abstract class BaseAuthPanel extends JPanel {

    protected BaseAuthPanel() {
        //unchanged parts in panels
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        setOpaque(false);

        add(Box.createVerticalStrut(20));
        LogoMaker.addLogoTo(this, 220, 220, CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(20));

        build();
    }

    protected abstract void build(); //each panel going to add their own components
}