package com.example;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.*;

public class Console {
    public JTextArea textArea;
    public JScrollPane scrollPane;

    public Console() {
        textArea = new JTextArea();
        //textArea.setSize(800, 600);
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.GREEN);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(0, 0, 895, 540);
        scrollPane.setBackground(Color.gray);
        scrollPane.setForeground(Color.DARK_GRAY);
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                textArea.append(String.valueOf((char) b));
                textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll to the bottom
            }
        }));
    }
}
