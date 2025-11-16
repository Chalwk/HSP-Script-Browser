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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
    private List<ScriptMetadata> filteredScripts;

    private JComboBox<ScriptCategory> categoryComboBox;
    private JList<ScriptMetadata> scriptList;
    private JTextArea descriptionArea;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel statisticsLabel;
    private Buttons downloadButton;
    private Buttons viewOnGitHubButton;
    private JTextField searchField;

    public HSPScriptBrowser() {
        initializeUI();
        loadScripts();
        setupKeyboardNavigation();
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
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(new Color(59, 89, 152));

        // Category filter row
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        categoryPanel.setBackground(new Color(59, 89, 152));

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryPanel.add(categoryLabel);

        categoryComboBox = new JComboBox<>(ScriptCategory.values());
        categoryComboBox.setBackground(Color.WHITE);
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryComboBox.setPreferredSize(new Dimension(150, 35));
        categoryComboBox.addActionListener(e -> filterScripts());
        categoryPanel.add(categoryComboBox);

        // Search row
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.setBackground(new Color(59, 89, 152));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(searchLabel);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.getDocument().addDocumentListener(new SearchDocumentListener());

        // Add search icon
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setBackground(Color.WHITE);
        searchFieldPanel.add(searchField, BorderLayout.CENTER);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setBorder(new EmptyBorder(0, 5, 0, 5));
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);

        searchPanel.add(searchFieldPanel);

        panel.add(categoryPanel);
        panel.add(searchPanel);

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

        // Statistics label
        statisticsLabel = new JLabel("Loading statistics...");
        statisticsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statisticsLabel.setForeground(new Color(59, 89, 152));

        statusLabel = new JLabel("Loading scripts from GitHub...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setForeground(new Color(59, 89, 152));
        progressBar.setBackground(new Color(220, 220, 220));
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Copyright label
        JLabel copyrightLabel = new JLabel("HSP Script Browser © 2025 Jericho Crosby (Chalwk) - Licensed under MIT", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        copyrightLabel.setForeground(new Color(150, 150, 150));
        copyrightLabel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(245, 247, 250));

        // Top panel for statistics and status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.add(statisticsLabel, BorderLayout.WEST);
        topPanel.add(statusLabel, BorderLayout.EAST);

        statusPanel.add(topPanel, BorderLayout.NORTH);
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

    private void setupKeyboardNavigation() {
        // Set up keyboard shortcuts
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        // Ctrl+F for search
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "search");
        actionMap.put("search", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.requestFocusInWindow();
                searchField.selectAll();
            }
        });

        // Enter to download selected script
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "download");
        actionMap.put("download", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (downloadButton.isEnabled()) {
                    downloadButton.doClick();
                }
            }
        });

        // Escape to clear selection and search
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clear");
        actionMap.put("clear", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scriptList.clearSelection();
                searchField.setText("");
                searchField.requestFocusInWindow();
            }
        });

        // Add keyboard navigation to script list
        scriptList.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && downloadButton.isEnabled()) {
                    downloadButton.doClick();
                }
            }
        });
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
                        updateStatistics();
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

        // Apply search filter
        String searchText = searchField.getText().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            categoryScripts = applyRelevanceSearch(categoryScripts, searchText);
        } else {
            // Sort by title when no search
            categoryScripts.sort(Comparator.comparing(ScriptMetadata::getTitle));
        }

        filteredScripts = categoryScripts;
        scriptList.setListData(filteredScripts.toArray(new ScriptMetadata[0]));

        // Clear selection
        scriptList.clearSelection();
        descriptionArea.setText("");
        downloadButton.setEnabled(false);
        viewOnGitHubButton.setEnabled(false);

        updateStatistics();
    }

    private List<ScriptMetadata> applyRelevanceSearch(List<ScriptMetadata> scripts, String searchText) {
        return scripts.stream()
                .map(script -> calculateRelevance(script, searchText))
                .filter(result -> matchesSearch(result.script, searchText)) // Use matchesSearch to filter
                .sorted((r1, r2) -> Integer.compare(r2.score, r1.score)) // Descending order by relevance
                .map(result -> result.script)
                .collect(Collectors.toList());
    }

    private SearchResult calculateRelevance(ScriptMetadata script, String searchText) {
        int score = 0;
        String title = script.getTitle().toLowerCase();
        String filename = script.getFilenameWithoutExtension() != null ?
                script.getFilenameWithoutExtension().toLowerCase() : "";
        String description = script.getDescription() != null ? script.getDescription().toLowerCase() : "";
        String shortDescription = script.getShortDescription() != null ?
                script.getShortDescription().toLowerCase() : "";
        String category = script.getCategory().getDisplayName().toLowerCase();

        String[] searchTerms = searchText.toLowerCase().split("\\s+");

        for (String term : searchTerms) {
            // Exact matches (highest score)
            if (title.equals(term)) score += 100;
            if (filename.equals(term)) score += 100;

            // Contains matches (medium score)
            if (title.contains(term)) score += 50;
            if (filename.contains(term)) score += 50;
            if (description.contains(term)) score += 10;
            if (shortDescription.contains(term)) score += 10;
            if (category.contains(term)) score += 5;

            // Fuzzy matches (lowest score)
            if (fuzzyMatch(title, term)) score += 3;
            if (fuzzyMatch(filename, term)) score += 3;
            if (fuzzyMatch(description, term)) score += 1;
        }

        return new SearchResult(script, score);
    }

    private boolean matchesSearch(ScriptMetadata script, String searchText) {
        if (searchText.isEmpty()) return true;

        String[] searchTerms = searchText.toLowerCase().split("\\s+");
        String title = script.getTitle() != null ? script.getTitle().toLowerCase() : "";
        String description = script.getDescription() != null ? script.getDescription().toLowerCase() : "";
        String shortDescription = script.getShortDescription() != null ?
                script.getShortDescription().toLowerCase() : "";
        String category = script.getCategory().getDisplayName().toLowerCase();
        String filename = script.getFilenameWithoutExtension() != null ?
                script.getFilenameWithoutExtension().toLowerCase() : "";

        for (String term : searchTerms) {
            boolean matches =
                    // Exact matches (highest priority)
                    title.equals(term) ||
                            filename.equals(term) ||

                            // Contains matches (medium priority)
                            title.contains(term) ||
                            filename.contains(term) ||
                            description.contains(term) ||
                            shortDescription.contains(term) ||
                            category.contains(term) ||

                            // Fuzzy matches (lowest priority)
                            fuzzyMatch(title, term) ||
                            fuzzyMatch(filename, term);

            if (!matches) {
                return false;
            }
        }
        return true;
    }

    private boolean fuzzyMatch(String text, String pattern) {
        if (pattern.isEmpty()) return true;
        if (text.isEmpty()) return false;

        int patternIndex = 0;
        for (int i = 0; i < text.length() && patternIndex < pattern.length(); i++) {
            if (text.charAt(i) == pattern.charAt(patternIndex)) {
                patternIndex++;
            }
        }
        return patternIndex == pattern.length();
    }

    private void updateStatistics() {
        if (allScripts == null) return;

        long total = allScripts.size();
        long attractive = allScripts.stream().filter(s -> s.getCategory() == ScriptCategory.ATTRACTIVE).count();
        long customGames = allScripts.stream().filter(s -> s.getCategory() == ScriptCategory.CUSTOM_GAMES).count();
        long utility = allScripts.stream().filter(s -> s.getCategory() == ScriptCategory.UTILITY).count();

        long showing = filteredScripts != null ? filteredScripts.size() : 0;

        String statsText = String.format("Total: %d | Attractive: %d | Custom Games: %d | Utility: %d | Showing: %d",
                total, attractive, customGames, utility, showing);

        statisticsLabel.setText(statsText);
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

    private static class SearchResult {
        ScriptMetadata script;
        int score;

        SearchResult(ScriptMetadata script, int score) {
            this.script = script;
            this.score = score;
        }
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

    private class SearchDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            filterScripts();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            filterScripts();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            filterScripts();
        }
    }
}