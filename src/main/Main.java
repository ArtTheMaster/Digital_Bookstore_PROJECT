package main;

import javax.swing.*;
import main.ui.LoginFrame;
import main.util.UITheme;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel for native window decorations
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Apply global theme customizations to JOptionPane
        UIManager.put("OptionPane.background", UITheme.SECONDARY_BG);
        UIManager.put("Panel.background", UITheme.SECONDARY_BG);
        UIManager.put("OptionPane.messageForeground", UITheme.TEXT_PRIMARY);

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}