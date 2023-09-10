package com.example.visual;

import com.example.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

public class SettingsFrame extends JFrame {

    private JPanel mainPanel,jvmPanel, gamePanel;
    private JTextField jvmArgs, LegacyPathField, runtimeGammaField, resolutionXField, resolutionYField;
    private JButton jreFileChooser, jdkFileChooser, SaveBtn, CancelBtn, DefaultsBtn;
    private TitledBorder border, gameBorder;
    private Border blackline, gameLine;

    private JComboBox dedicatedMemory;
    private JLabel memory , Args, LegacyPathLbl, GammaPathLbl, separator;
    private JCheckBox resolutionCheck;

    private String XmxRam = "";

    private final Color buttonsColor = new Color(110, 110, 110);

    public SettingsFrame() {
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Settings");
        setSize(400, 465);
        setBackground(Color.darkGray);
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);

        border = BorderFactory.createTitledBorder("JVM Settings");
        border.setTitleColor(Color.WHITE);
        blackline = border;

        gameBorder = BorderFactory.createTitledBorder("Game Settings");
        gameBorder.setTitleColor(Color.WHITE);
        gameLine = gameBorder;

        mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 400, 465);
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.darkGray);

        setUpButtons();
        InitBox();

        gamePanel = new JPanel();
        gamePanel.setBounds(5, 280, 385, 100);
        gamePanel.setLayout(null);
        gamePanel.setBackground(Color.darkGray);
        gamePanel.setBorder(gameLine);

        InitGameSettings();

        memory = new JLabel("Dedicated Memory");
        memory.setBounds(20, 20, 300, 25);
        memory.setForeground(Color.WHITE);

        Args = new JLabel("JVM Arguments");
        Args.setBounds(20, 80, 300, 25);
        Args.setForeground(Color.WHITE);

        setJvmArgs();
        setupJrePath();

        jvmPanel = new JPanel();
        jvmPanel.setBounds(5, 5, 385, 270);
        jvmPanel.setLayout(null);
        jvmPanel.setBackground(Color.darkGray);
        jvmPanel.setBorder(blackline);

        jvmPanel.add(dedicatedMemory);
        jvmPanel.add(memory);
        jvmPanel.add(Args);
        jvmPanel.add(jvmArgs);
        jvmPanel.add(LegacyPathLbl);
        jvmPanel.add(LegacyPathField);
        jvmPanel.add(jreFileChooser);

        jvmPanel.add(runtimeGammaField);
        jvmPanel.add(GammaPathLbl);
        jvmPanel.add(jdkFileChooser);

        //mainPanel.add(resolutionLbl);
        mainPanel.add(resolutionXField);
        mainPanel.add(resolutionYField);
        mainPanel.add(separator);
        mainPanel.add(resolutionCheck);

        mainPanel.add(SaveBtn);
        mainPanel.add(CancelBtn);
        mainPanel.add(DefaultsBtn);

        mainPanel.add(jvmPanel);
        mainPanel.add(gamePanel);
        add(mainPanel);
    }

    private void setJvmArgs() {
        jvmArgs = new JTextField(Utils.jvmArguments);
        jvmArgs.setBounds(20, 105, 345, 25);
        jvmArgs.setCaretPosition(0);
        jvmArgs.setToolTipText("The JVM arguments used to start the game. Be careful!! or the game won't launch");
        jvmArgs.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SaveBtn.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SaveBtn.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SaveBtn.setEnabled(true);
            }
        });
    }

    private void saveJvmArgs() {
        Utils.jvmArguments = jvmArgs.getText();
    }

    private void InitBox() {

        dedicatedMemory = new JComboBox<>();
        dedicatedMemory.setBounds(20, 45, 345, 25);

        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        // Get the total physical memory (RAM) size in bytes
        try {
            Method m = operatingSystemMXBean.getClass().
                    getDeclaredMethod("getTotalPhysicalMemorySize");

            m.setAccessible(true);

            Object value = m.invoke(operatingSystemMXBean);

            if (value != null)
            {
                long totalRam = (long) value;
                double totalRamGB = totalRam / (1024.0 * 1024 * 1024);

                long roundedTotal = Math.round(totalRamGB);

                for (int i = 1; i <= roundedTotal; i++) {
                    dedicatedMemory.addItem(i + "GB");
                }

                System.out.println("Total RAM: " + roundedTotal + " GB");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dedicatedMemory.setMaximumRowCount(14);
        dedicatedMemory.setSelectedItem(Utils.selectedRam);
        dedicatedMemory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveBtn.setEnabled(true);
                XmxRam = (String) dedicatedMemory.getSelectedItem();
                assert XmxRam != null;
                String formattedRam = XmxRam.replace("B", "");
                jvmArgs.setText(" -Xmx" + formattedRam + " -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20" +
                " -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M");
                jvmArgs.setCaretPosition(0);
                Utils.selectedRam = XmxRam;
            }
        });
        dedicatedMemory.setUI(new CustomComboBoxUI());
    }

    private void setupJrePath() {
        LegacyPathLbl = new JLabel("Jre Legacy or Java 8 Path");
        LegacyPathLbl.setBounds(20, 135, 300, 25);
        LegacyPathLbl.setForeground(Color.WHITE);

        LegacyPathField = new JTextField(Utils.LegacyPath);
        LegacyPathField.setBounds(20, 160, 315, 25);
        LegacyPathField.setCaretPosition(0);
        LegacyPathField.setToolTipText("The Jre Path, Used for versions like 1.16.5 or lower");
        LegacyPathField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveBtn.setEnabled(true);
                DefaultsBtn.setEnabled(true);
            }
        });

        jreFileChooser = new JButton("...");
        jreFileChooser.setBounds(335, 160, 30, 25);
        jreFileChooser.setBackground(Color.LIGHT_GRAY);
        jreFileChooser.setForeground(Color.WHITE);
        jreFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenFileViaExplorer(LegacyPathField);
            }
        });

        GammaPathLbl = new JLabel("Java 17 or higher Path");
        GammaPathLbl.setBounds(20, 190, 300, 25);
        GammaPathLbl.setForeground(Color.WHITE);

        runtimeGammaField = new JTextField(Utils.GammaPath);
        runtimeGammaField.setBounds(20, 215, 315, 25);
        runtimeGammaField.setCaretPosition(0);
        runtimeGammaField.setToolTipText("The overall path of the Java version, make sure this is Java 17 or higher.");
        runtimeGammaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveBtn.setEnabled(true);
                DefaultsBtn.setEnabled(true);
            }
        });

        jdkFileChooser = new JButton("...");
        jdkFileChooser.setBounds(335, 215, 30, 25);
        jdkFileChooser.setBackground(Color.LIGHT_GRAY);
        jdkFileChooser.setForeground(Color.WHITE);
        jdkFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenFileViaExplorer(runtimeGammaField);
            }
        });

    }

    public boolean OpenFileViaExplorer(JTextField textField) {
        try {
            JFileChooser fileChooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Exe files", "exe");

            fileChooser.setFileFilter(filter);

            fileChooser.setCurrentDirectory(new File("."));

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                System.out.println("File Path: " + selectedFile);
                textField.setText(selectedFile.toString());
                textField.setCaretPosition(0);
                SaveBtn.setEnabled(true);
                DefaultsBtn.setEnabled(true);

                return true;
            } else if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("Cancelled");
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    private void InitGameSettings() {
        //resolutionLbl = new JLabel("Game Resolution");
        //resolutionLbl.setBounds(25, 300, 130, 25);
        //resolutionLbl.setForeground(Color.WHITE);

        resolutionCheck = new JCheckBox("Game Resolution");
        resolutionCheck.setBounds(21, 295, 130, 30);
        resolutionCheck.setBackground(Color.darkGray);
        resolutionCheck.setForeground(Color.WHITE);
        resolutionCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    resolutionXField.setEnabled(true);
                    resolutionYField.setEnabled(true);
                } else {
                    resolutionYField.setEnabled(false);
                    resolutionXField.setEnabled(false);
                }
            }
        });

        resolutionXField = new JTextField(String.valueOf(Utils.resolutionX));
        resolutionXField.setBounds(25, 325, 140, 25);
        resolutionXField.setEnabled(false);
        resolutionXField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SaveBtn.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SaveBtn.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SaveBtn.setEnabled(true);
            }
        });

        separator = new JLabel("X");
        separator.setBounds(194, 325, 50, 25);
        separator.setForeground(Color.WHITE);

        resolutionYField = new JTextField(String.valueOf(Utils.resolutionY));
        resolutionYField.setBounds(230, 325, 140, 25);
        resolutionYField.setEnabled(false);
        resolutionYField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SaveBtn.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SaveBtn.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SaveBtn.setEnabled(true);
            }
        });

    }

    private void setUpButtons() {
        SaveBtn = new JButton("Save");
        SaveBtn.setBounds(210, 402, 80, 25);
        SaveBtn.setBackground(buttonsColor);
        SaveBtn.setForeground(Color.WHITE);
        SaveBtn.setEnabled(false);
        SaveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveBtn.setEnabled(false);
                Utils.LegacyPath = LegacyPathField.getText();
                Utils.GammaPath = runtimeGammaField.getText();
                Utils.resolutionX = Integer.parseInt(resolutionXField.getText());
                Utils.resolutionY = Integer.parseInt(resolutionYField.getText());
                saveJvmArgs();
                Utils.saveUserPrefs();
            }
        });

        CancelBtn = new JButton("Cancel");
        CancelBtn.setBounds(300, 402, 80, 25);
        CancelBtn.setBackground(buttonsColor);
        CancelBtn.setForeground(Color.WHITE);
        CancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        DefaultsBtn = new JButton("Default");
        DefaultsBtn.setBounds(10, 402, 80, 25);
        DefaultsBtn.setBackground(buttonsColor);
        DefaultsBtn.setForeground(Color.WHITE);
        DefaultsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultsBtn.setEnabled(false);
                SaveBtn.setEnabled(true);
                Utils.LegacyPath = Utils.getWorkingDirectory() + "\\.minecraft\\runtime\\jre-legacy\\bin\\javaw.exe";
                Utils.GammaPath = Utils.getWorkingDirectory() + "\\.minecraft\\runtime\\java-runtime-gamma\\bin\\javaw.exe";
                Utils.selectedRam = "2GB";
                Utils.resolutionX = 854;
                Utils.resolutionY = 480;
                resolutionXField.setText("854");
                resolutionYField.setText("480");
                dedicatedMemory.setSelectedItem(Utils.selectedRam);
                Utils.saveUserPrefs();
                runtimeGammaField.setText(Utils.GammaPath);
                LegacyPathField.setText(Utils.LegacyPath);
            }
        });

    }
}
