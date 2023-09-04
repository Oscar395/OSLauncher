package com.example.visual;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomScrollbarUI  extends BasicScrollBarUI {

    private final Color thumbRolloverColor = new Color(145, 147, 150);

    @Override
    protected void configureScrollBarColors() {
        thumbColor = Color.GRAY;            // Thumb color
        thumbHighlightColor = Color.GRAY;       // Thumb highlight color
        thumbDarkShadowColor = Color.GRAY;       // Thumb dark shadow color
        thumbLightShadowColor = Color.GRAY;      // Thumb light shadow color
        trackColor = Color.LIGHT_GRAY;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createEmptyButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createEmptyButton();
    }

    private JButton createEmptyButton() {
        JButton button = new JButton();
        button.setPreferredSize(new java.awt.Dimension(0, 0));
        button.setMinimumSize(new java.awt.Dimension(0, 0));
        button.setMaximumSize(new java.awt.Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        boolean isRollover = isThumbRollover();
        Color color = isRollover ? thumbRolloverColor : thumbColor;

        g.setColor(color);
        g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
    }
}
