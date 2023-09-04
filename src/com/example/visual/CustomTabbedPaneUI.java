package com.example.visual;

import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class CustomTabbedPaneUI extends BasicTabbedPaneUI {
    private Color selectedColor = new Color(89, 128, 95);

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (isSelected) {
            g.setColor(selectedColor);
            g.fillRect(x, y, w, h);
        } else {
            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
        }
    }
}
