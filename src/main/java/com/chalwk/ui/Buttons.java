/**
 * HSP-Script-Browser
 * Copyright (c) 2025 Jericho Crosby (Chalwk)
 * <p>
 * This project is licensed under the MIT License.
 * See LICENSE file for details:
 * https://github.com/Chalwk/HSP-Script-Browser/blob/main/LICENSE
 */

package com.chalwk.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Buttons extends JButton {
    private Color backgroundColor = new Color(59, 89, 152);
    private final Color hoverColor = new Color(45, 75, 138);
    private final Color pressedColor = new Color(35, 65, 128);

    public Buttons(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        Color textColor = Color.WHITE;
        setForeground(textColor);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setPreferredSize(new Dimension(140, 40));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backgroundColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backgroundColor = new Color(59, 89, 152);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                backgroundColor = pressedColor;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                backgroundColor = hoverColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint background
        g2.setColor(backgroundColor);
        int cornerRadius = 8;
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Paint text
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}