package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowManager extends JFrame{
    public JButton button, button2, playButton;
    public JLabel label, playerNameLb, selectedVersion, vTypeLb;
    public JProgressBar progressBar;
    public JTextField playerNameField;

    public JPanel panel1;
    public  JPanel panel2;
    public JTabbedPane jTabbedPane;
    public JComboBox versionsList, versionType;
    public static WindowManager Instance;

    //JSON writer
    private JsonWriterAndReader jsonWriterAndReader = new JsonWriterAndReader();
    public WindowManager() {
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setResizable(false);
        setTitle("McLauncher");
        setVisible(true);
        setLocationRelativeTo(null);
        Instance = this;

        panel1 = new JPanel();
        panel1.setBackground(Color.darkGray);
        panel1.setLayout(null);
        //panel1.setBorder(new EmptyBorder(10, 10,10 ,10));

        panel2 = new JPanel();
        panel2.setLayout(null);

        Console console = new Console();
        panel2.add(console.scrollPane);

        initComponents();

        jTabbedPane = new JTabbedPane();

        jTabbedPane.setBounds(0,0, 895, 600 - this.getInsets().top);
        jTabbedPane.setBackground(new Color(162, 180, 210));
        jTabbedPane.setForeground(Color.WHITE);
        jTabbedPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

        jTabbedPane.add("Main Page", panel1);
        jTabbedPane.add("Console", panel2);
        add(jTabbedPane);
    }

    private void initComponents() {
        label = new JLabel("Texto");
        label.setBounds(10, 25, 80, 30);
        label.setForeground(Color.white);
        playerNameLb = new JLabel("Player Name:");
        playerNameLb.setBounds(680, 0, 150, 25);
        playerNameLb.setForeground(Color.white);
        playerNameField = new JTextField();
        playerNameField.setBounds(762, 0, 116, 25);
        playerNameField.setText(Utils.auth_player_name);

        panel1.add(playerNameField);
        panel1.add(label);
        panel1.add(playerNameLb);
        addButtons(panel1);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("");
        progressBar.setSize(890, 20);
        progressBar.setLocation(0,445);
        progressBar.setBackground(Color.DARK_GRAY);
        progressBar.setForeground(new Color(105, 157, 94));
        panel1.add(progressBar);

        versionsList = new JComboBox<>();
        versionsList.setBounds(680, 505, 200, 25);
        InitVersions(versionsList);
        panel1.add(versionsList);

        selectedVersion = new JLabel("Selected Version: ");
        selectedVersion.setBounds(570, 505, 130, 25 );
        selectedVersion.setForeground(Color.white);
        panel1.add(selectedVersion);

        versionType = new JComboBox<>();
        versionType.setBounds(680, 475, 200, 25);
        versionType.addItem("release");
        versionType.addItem("snapshot");
        versionType.addItem("old_beta");
        versionType.addItem("old_alpha");
        versionType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils.VersionType = versionType.getSelectedItem().toString();
                jsonWriterAndReader.updateVersionsList(versionsList);
                //System.out.println(versionType.getSelectedItem().toString());
            }
        });
        panel1.add(versionType);

        vTypeLb = new JLabel("Type");
        vTypeLb.setBounds(570, 475, 130, 25);
        vTypeLb.setForeground(Color.white);
        panel1.add(vTypeLb);
    }

    private void InitVersions(JComboBox versionsBox) {
        jsonWriterAndReader.readVersionsList(versionsBox);
    }

    public void addButtons(JPanel panelToAdd) {

        button = new JButton("Panel 1");
        button.setBounds(0, 0, 100, 25);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText("Button 1 Pressed");
                progressBar.setValue(progressBar.getValue() + 1);
                System.out.println("Hello world");
                System.out.println(Utils.getWorkingDirectory());
            }
        });
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBackground(Color.lightGray);
        button.setForeground(Color.WHITE);
        panelToAdd.add(button);

        button2 = new JButton("Panel 2");
        button2.setBounds(100, 0, 100, 25);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText("Button 2 Pressed");
            }
        });
        button2.setFont(new Font("Arial", Font.PLAIN, 12));
        button2.setBackground(Color.lightGray);
        button2.setForeground(Color.WHITE);
        panelToAdd.add(button2);

        playButton = new JButton("Play");
        playButton.setBounds(330, 482, 150, 40);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playButton.setEnabled(false);
                progressBar.setString("Copying Java Runtime Environment...");
                Main.tryToCopyJre(Instance);
                progressBar.setString("Getting Version...");
                jsonWriterAndReader.downloadSelectedVersion(versionsList, progressBar);
            }
        });
        playButton.setFont(new Font("Arial", Font.PLAIN, 14));
        playButton.setBackground(Color.lightGray);
        playButton.setForeground(Color.WHITE);
        panelToAdd.add(playButton);
    }

}
