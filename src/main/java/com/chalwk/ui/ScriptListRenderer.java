/**
 * HSP-Script-Browser
 * Copyright (c) 2025 Jericho Crosby (Chalwk)
 * <p>
 * This project is licensed under the MIT License.
 * See LICENSE file for details:
 * https://github.com/Chalwk/HSP-Script-Browser/blob/main/LICENSE
 */

package com.chalwk.ui;

import com.chalwk.model.ScriptMetadata;

import javax.swing.*;
import java.awt.*;

public class ScriptListRenderer extends JPanel implements ListCellRenderer<ScriptMetadata> {
    private final JLabel titleLabel;
    private final JLabel descriptionLabel;
    private boolean isSelected;

    public ScriptListRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setOpaque(true);

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(60, 60, 60));

        descriptionLabel = new JLabel();
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descriptionLabel.setForeground(new Color(120, 120, 120));

        add(titleLabel, BorderLayout.NORTH);
        add(descriptionLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ScriptMetadata> list, ScriptMetadata script,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        this.isSelected = isSelected;

        if (script != null) {
            titleLabel.setText(script.getTitle());
            descriptionLabel.setText(script.getShortDescription());
        }

        // Set background and foreground colors based on selection
        if (isSelected) {
            setBackground(new Color(59, 89, 152));
            titleLabel.setForeground(Color.WHITE);
            descriptionLabel.setForeground(new Color(220, 220, 255));
        } else {
            setBackground(index % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
            titleLabel.setForeground(new Color(60, 60, 60));
            descriptionLabel.setForeground(new Color(120, 120, 120));
        }

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Add a subtle border for selected items
        if (isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(59, 89, 152));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 6, 6);
            g2.dispose();
        }
    }
}