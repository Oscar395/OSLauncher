package com.example.visual;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ActionListener;

public class CustomComboBoxUI extends BasicComboBoxUI {

    private final Color BackColour = new Color(150, 150, 150);

    @Override
    protected ComboPopup createPopup() {

        return new BasicComboPopup(comboBox) {
            @Override
            protected JScrollPane createScroller() {
                JScrollPane scroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scroller.getVerticalScrollBar().setUI(new CustomScrollbarUI());

                list.setBackground(BackColour);
                list.setForeground(Color.WHITE);
                list.setFont(new Font("Arial", Font.BOLD, 12));

                comboBox.setBackground(BackColour);
                comboBox.setForeground(Color.WHITE);
                comboBox.setFont(new Font("Arial", Font.BOLD, 12));

                return scroller;
            }

        };

    }

    @Override
    protected JButton createArrowButton() {
        return new JButton(){
            @Override public int getWidth() {
                return 0;
            }
            @Override
            public void setFocusable(boolean focusable) {
                super.setFocusable(false);
            }
        };
    }

}
