package main.ui;

import main.model.User;
import main.service.AuthService;
import main.util.UITheme;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private final AuthService authService;
    private final JFrame parentFrame;
    
    private JTextField txtFullName, txtEmail, txtUsername;
    private JPasswordField txtPassword, txtConfirmPassword;

    public RegisterFrame(JFrame parentFrame) {
        this.authService = new AuthService();
        this.parentFrame = parentFrame;
        setupFrame();
        initComponents();
    }

    private void setupFrame() {
        setTitle("Register Cashier Account");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.PRIMARY_BG);
        setLayout(new BorderLayout());
        
        // Handle window close event to reopen Login
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                parentFrame.setVisible(true);
            }
        });
    }

    private void initComponents() {
        JPanel mainPanel = UITheme.createCardPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Create Account");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.ACCENT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtFullName = createInput("Full Name", mainPanel);
        txtEmail = createInput("Email", mainPanel);
        txtUsername = createInput("Username", mainPanel);
        txtPassword = createPasswordInput("Password", mainPanel);
        txtConfirmPassword = createPasswordInput("Confirm Password", mainPanel);

        JButton btnRegister = new JButton("REGISTER");
        btnRegister.setMaximumSize(new Dimension(250, 40));
        btnRegister.setBackground(UITheme.ACCENT);
        btnRegister.setForeground(UITheme.PRIMARY_BG);
        btnRegister.setFocusPainted(false);
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(e -> attemptRegister());

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancel.setForeground(UITheme.TEXT_SECONDARY);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(e -> {
            this.dispose();
            parentFrame.setVisible(true);
        });

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(btnRegister);
        mainPanel.add(btnCancel);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.PRIMARY_BG);
        wrapper.add(mainPanel);
        add(wrapper, BorderLayout.CENTER);
    }

    private JTextField createInput(String label, JPanel panel) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(250, 30));
        tf.setBackground(UITheme.PRIMARY_BG);
        tf.setForeground(UITheme.TEXT_PRIMARY);
        tf.setCaretColor(UITheme.TEXT_PRIMARY);
        tf.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(lbl);
        panel.add(tf);
        panel.add(Box.createVerticalStrut(10));
        return tf;
    }

    private JPasswordField createPasswordInput(String label, JPanel panel) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPasswordField pf = new JPasswordField();
        pf.setMaximumSize(new Dimension(250, 30));
        pf.setBackground(UITheme.PRIMARY_BG);
        pf.setForeground(UITheme.TEXT_PRIMARY);
        pf.setCaretColor(UITheme.TEXT_PRIMARY);
        pf.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(lbl);
        panel.add(pf);
        panel.add(Box.createVerticalStrut(10));
        return pf;
    }

    private void attemptRegister() {
        String pass = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(0, txtUsername.getText().trim(), "", txtFullName.getText().trim(), txtEmail.getText().trim(), User.Role.CASHIER);

        try {
            if (authService.register(newUser, pass)) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
                this.dispose();
                parentFrame.setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}