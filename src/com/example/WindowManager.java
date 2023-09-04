package com.example;

import com.example.visual.CustomComboBoxUI;
import com.example.visual.CustomTabbedPaneUI;
import com.example.visual.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowManager extends JFrame{
    public JButton settings, playButton, accountBtn;
    public JLabel label, playerNameLb, selectedVersion, vTypeLb;
    public JProgressBar progressBar;
    public JTextField playerNameField;

    public JPanel panel1;
    public  JPanel panel2;
    public JTabbedPane jTabbedPane;
    public JComboBox versionsList, versionType;
    public static WindowManager Instance;

    private ImagePanel imagePanel;

    private final Color buttonsColor = new Color(110, 110, 110);

    //JSON writer
    private JsonWriterAndReader jsonWriterAndReader = new JsonWriterAndReader();
    public WindowManager() {

        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Utils.WIDTH, Utils.HEIGHT);
        setResizable(false);
        setTitle("McLauncher");
        setVisible(true);
        setLocationRelativeTo(null);
        Instance = this;

        imagePanel = new ImagePanel(new ImageIcon("images/background.png").getImage());

        panel1 = new JPanel();
        panel1.setLayout(null);
        panel1.setBackground(Color.darkGray);

        //panel1.setBorder(new EmptyBorder(10, 10,10 ,10));

        panel2 = new JPanel();
        panel2.setLayout(null);

        Console console = new Console();
        panel2.add(console.scrollPane);

        initComponents();

        jTabbedPane = new JTabbedPane();

        jTabbedPane.setBounds(0,0, Utils.WIDTH - 5, Utils.HEIGHT - this.getInsets().top);
        jTabbedPane.setBackground(new Color(178, 182, 185));
        jTabbedPane.setForeground(Color.WHITE);
        jTabbedPane.setUI(new CustomTabbedPaneUI());
        jTabbedPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));

        jTabbedPane.add("Main Page", panel1);
        jTabbedPane.add("Console", panel2);
        add(jTabbedPane);

        panel1.add(imagePanel);
    }

    private void initComponents() {
        playerNameLb = new JLabel("Player Name:");
        playerNameLb.setBounds(680, 5, 150, 25);
        playerNameLb.setForeground(Color.white);
        playerNameField = new JTextField();
        playerNameField.setBounds(765, 5, 120, 25);
        playerNameField.setFont(new Font(Font.SANS_SERIF,  Font.BOLD, 12));
        playerNameField.setText(Utils.auth_player_name);

        panel1.add(playerNameField);
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
        versionsList.setBounds(660, 505, 220, 25);
        versionsList.setUI(new CustomComboBoxUI());
        versionsList.setMaximumRowCount(12);
        InitVersions(versionsList);
        panel1.add(versionsList);

        selectedVersion = new JLabel("Selected Version: ");
        selectedVersion.setBounds(550, 505, 130, 25 );
        selectedVersion.setForeground(Color.white);
        panel1.add(selectedVersion);

        versionType = new JComboBox<>();
        versionType.setBounds(660, 475, 220, 25);
        versionType.addItem("release");
        versionType.addItem("snapshot");
        versionType.addItem("old_beta");
        versionType.addItem("old_alpha");
        versionType.setUI(new CustomComboBoxUI());
        versionType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils.VersionType = versionType.getSelectedItem().toString();
                jsonWriterAndReader.updateVersionsList(versionsList);
                //System.out.println(versionType.getSelectedItem().toString());
            }
        });
        panel1.add(versionType);

        vTypeLb = new JLabel("Type: ");
        vTypeLb.setBounds(550, 475, 130, 25);
        vTypeLb.setForeground(Color.white);
        panel1.add(vTypeLb);
    }

    private void InitVersions(JComboBox versionsBox) {
        jsonWriterAndReader.readVersionsList(versionsBox);
    }

    public void addButtons(JPanel panelToAdd) {

        settings = new JButton("Settings");
        settings.setBounds(10, 475, 220, 25);
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        settings.setFont(new Font("Arial", Font.BOLD, 12));
        settings.setBackground(buttonsColor);
        settings.setForeground(Color.WHITE);
        panelToAdd.add(settings);

        accountBtn = new JButton("Account");
        accountBtn.setBounds(10, 505, 220, 25);
        accountBtn.setFont(new Font("Arial", Font.BOLD, 12));
        accountBtn.setBackground(buttonsColor);
        accountBtn.setForeground(Color.WHITE);
        panelToAdd.add(accountBtn);

        playButton = new JButton("Play");
        playButton.setBounds((int) (Utils.WIDTH * 0.5) - 70, 482, 150, 40);
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
        playButton.setFont(new Font("Arial", Font.BOLD, 14));
        playButton.setBackground(buttonsColor);
        playButton.setForeground(Color.WHITE);
        panelToAdd.add(playButton);
    }

}
