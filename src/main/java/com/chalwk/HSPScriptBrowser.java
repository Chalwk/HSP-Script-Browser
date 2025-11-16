/**
 * HSP-Script-Browser
 * Copyright (c) 2025 Jericho Crosby (Chalwk)
 * <p>
 * This project is licensed under the MIT License.
 * See LICENSE file for details:
 * https://github.com/Chalwk/HSP-Script-Browser/blob/main/LICENSE
 */

package com.chalwk;

import com.chalwk.model.ScriptCategory;
import com.chalwk.model.ScriptMetadata;
import com.chalwk.service.ScriptService;
import com.chalwk.ui.Buttons;
import com.chalwk.ui.ScriptListRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HSPScriptBrowser extends JFrame {
    private List<ScriptMetadata> allScripts;
    private Map<ScriptCategory, List<ScriptMetadata>> scriptsByCategory;

    private JComboBox<ScriptCategory> categoryComboBox;
    private JList<ScriptMetadata> scriptList;
    private JTextArea descriptionArea;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private Buttons downloadButton;
    private Buttons viewOnGitHubButton;

    public HSPScriptBrowser() {
        initializeUI();
        loadScripts();
    }

    private void initializeUI() {
        setTitle("HSP Script Browser");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create main panel with modern styling
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Add content
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);

        // Add status panel
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
        applyModernStyling();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(59, 89, 152));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        headerPanel.setPreferredSize(new Dimension(0, 120));

        // Title
        JLabel titleLabel = new JLabel("HSP Script Browser");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Browse and download SAPP Lua scripts from the HALO SCRIPT PROJECTS GitHub Repository");
        subtitleLabel.setForeground(new Color(200, 220, 255));
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(59, 89, 152));
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(new Color(59, 89, 152));
        controlPanel.add(createControlPanel());

        headerPanel.add(textPanel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(new Color(59, 89, 152));

        // Category filter
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(categoryLabel);

        categoryComboBox = new JComboBox<>(ScriptCategory.values());
        categoryComboBox.setBackground(Color.WHITE);
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryComboBox.setPreferredSize(new Dimension(150, 35));
        categoryComboBox.addActionListener(e -> filterScripts());
        panel.add(categoryComboBox);

        return panel;
    }

    private JSplitPane createContentPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);
        splitPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Left panel - script list
        JPanel leftPanel = createScriptListPanel();

        // Right panel - script details
        JPanel rightPanel = createDetailsPanel();

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        return splitPane;
    }

    private JPanel createScriptListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Available Scripts");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(59, 89, 152));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        scriptList = new JList<>();
        scriptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scriptList.setCellRenderer(new ScriptListRenderer());
        scriptList.addListSelectionListener(new ScriptSelectionListener());
        scriptList.setBackground(Color.WHITE);
        scriptList.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane listScrollPane = new JScrollPane(scriptList);
        listScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        listScrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(listScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Script Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(59, 89, 152));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Description area
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setBackground(new Color(250, 250, 250));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setBorder(null);
        panel.add(descriptionScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        downloadButton = new Buttons("Download Script");
        viewOnGitHubButton = new Buttons("View on GitHub");

        downloadButton.setEnabled(false);
        viewOnGitHubButton.setEnabled(false);

        downloadButton.addActionListener(new DownloadButtonListener());
        viewOnGitHubButton.addActionListener(new ViewOnGitHubListener());

        buttonPanel.add(downloadButton);
        buttonPanel.add(viewOnGitHubButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        statusLabel = new JLabel("Loading scripts from GitHub...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setForeground(new Color(59, 89, 152));
        progressBar.setBackground(new Color(220, 220, 220));
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel copyrightLabel = new JLabel("HSP Script Browser © 2025 Jericho Crosby (Chalwk) - Licensed under MIT", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        copyrightLabel.setForeground(new Color(150, 150, 150));
        copyrightLabel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(245, 247, 250));
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(copyrightLabel, BorderLayout.SOUTH);

        panel.add(statusPanel, BorderLayout.CENTER);
        return panel;
    }

    private void applyModernStyling() {
        // Set UI defaults for modern look
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", new Color(60, 60, 60));
        UIManager.put("ComboBox.selectionBackground", new Color(59, 89, 152));
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);

        UIManager.put("List.background", Color.WHITE);
        UIManager.put("List.foreground", new Color(60, 60, 60));
        UIManager.put("List.selectionBackground", new Color(59, 89, 152));
        UIManager.put("List.selectionForeground", Color.WHITE);
    }

    private void loadScripts() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Loading scripts from GitHub...");
                    progressBar.setIndeterminate(true);
                    progressBar.setVisible(true);
                });

                allScripts = ScriptService.loadScriptsMetadata();
                organizeScriptsByCategory();

                return null;
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                    if (allScripts != null && !allScripts.isEmpty()) {
                        statusLabel.setText("Loaded " + allScripts.size() + " scripts from GitHub");
                        filterScripts();
                    } else {
                        statusLabel.setText("Failed to load scripts from GitHub");
                        JOptionPane.showMessageDialog(HSPScriptBrowser.this,
                                "Failed to load scripts from GitHub. Please check your internet connection.",
                                "Loading Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        }.execute();
    }

    private void organizeScriptsByCategory() {
        scriptsByCategory = allScripts.stream()
                .collect(Collectors.groupingBy(ScriptMetadata::getCategory));
    }

    private void filterScripts() {
        if (scriptsByCategory == null) return;

        ScriptCategory selectedCategory = (ScriptCategory) categoryComboBox.getSelectedItem();
        List<ScriptMetadata> categoryScripts = scriptsByCategory.getOrDefault(selectedCategory, new ArrayList<>());

        // Sort by title
        categoryScripts.sort(Comparator.comparing(ScriptMetadata::getTitle));

        scriptList.setListData(categoryScripts.toArray(new ScriptMetadata[0]));

        // Clear selection
        scriptList.clearSelection();
        descriptionArea.setText("");
        downloadButton.setEnabled(false);
        viewOnGitHubButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new HSPScriptBrowser().setVisible(true);
        });
    }

    private class ScriptSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) return;

            ScriptMetadata selectedScript = scriptList.getSelectedValue();
            if (selectedScript != null) {
                descriptionArea.setText(buildDescriptionText(selectedScript));
                downloadButton.setEnabled(true);
                viewOnGitHubButton.setEnabled(true);
            } else {
                descriptionArea.setText("");
                downloadButton.setEnabled(false);
                viewOnGitHubButton.setEnabled(false);
            }
        }

        private String buildDescriptionText(ScriptMetadata script) {
            return "Title: " + script.getTitle() + "\n\n" +
                    "Category: " + script.getCategory().getDisplayName() + "\n\n" +
                    "Filename: " + script.getFilename() + "\n\n" +
                    "Description:\n" + script.getDescription();
        }
    }

    private class DownloadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ScriptMetadata selectedScript = scriptList.getSelectedValue();
            if (selectedScript == null) return;

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose Download Location");
            fileChooser.setSelectedFile(new File(selectedScript.getFilename()));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int result = fileChooser.showSaveDialog(HSPScriptBrowser.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                downloadScript(selectedScript, outputFile);
            }
        }
    }

    private class ViewOnGitHubListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ScriptMetadata selectedScript = scriptList.getSelectedValue();
            if (selectedScript != null) {
                try {
                    Desktop.getDesktop().browse(new URI(selectedScript.getGitHubUrl()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(HSPScriptBrowser.this,
                            "Failed to open browser: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void downloadScript(ScriptMetadata script, File outputFile) {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                downloadButton.setEnabled(false);
                statusLabel.setText("Downloading " + script.getFilename() + "...");
                progressBar.setVisible(true);
                progressBar.setValue(0);
            });

            boolean success = ScriptService.downloadScript(script, outputFile, progressBar, statusLabel);

            SwingUtilities.invokeLater(() -> {
                downloadButton.setEnabled(true);
                progressBar.setVisible(false);
                if (success) {
                    statusLabel.setText("Successfully downloaded " + script.getFilename());
                    JOptionPane.showMessageDialog(HSPScriptBrowser.this,
                            "Script '" + script.getTitle() + "' downloaded successfully!\n\n" +
                                    "Location: " + outputFile.getAbsolutePath(),
                            "Download Complete", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    statusLabel.setText("Download failed for " + script.getFilename());
                }
            });
        }).start();
    }
}