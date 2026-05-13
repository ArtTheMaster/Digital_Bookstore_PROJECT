package main.util;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;

public class UITheme {
    // Colors
    public static final Color PRIMARY_BG = new Color(18, 25, 38);
    public static final Color SECONDARY_BG = new Color(26, 35, 50);
    public static final Color HOVER_BG = new Color(38, 50, 70); // New: Hover color
    public static final Color ACCENT = new Color(212, 175, 55);
    public static final Color ACCENT_HOVER = new Color(235, 198, 78); // New: Lighter gold
    public static final Color TEXT_PRIMARY = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(160, 175, 200);
    public static final Color SUCCESS = new Color(39, 174, 96);
    public static final Color DANGER = new Color(231, 76, 60);
    public static final Color WARNING = new Color(241, 196, 15);
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    public static Border createRoundedBorder(Color color, int radius, int thickness) {
        return BorderFactory.createLineBorder(color, thickness, true);
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SECONDARY_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(ACCENT, 10, 1), 
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    // New: Standardized Flat Button styling with Hover Effects
    public static void styleFlatButton(JButton btn, Color defaultBg, Color hoverBg, Color fg) {
        btn.setBackground(defaultBg);
        btn.setForeground(fg);
        btn.setFont(FONT_BODY.deriveFont(Font.BOLD));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(defaultBg);
            }
        });
    }
}