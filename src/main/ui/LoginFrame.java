package main.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import main.model.User;
import main.service.AuthService;
import main.util.UITheme;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;

    public LoginFrame() {
        this.authService = new AuthService();
        setupFrame();
        initComponents();
    }

    private void setupFrame() {
        setTitle("The Book Nook POS - Login");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.PRIMARY_BG);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        JPanel mainPanel = UITheme.createCardPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("THE BOOK NOOK");
        lblTitle.setFont(UITheme.FONT_TITLE.deriveFont(28f));
        lblTitle.setForeground(UITheme.ACCENT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Point of Sale System");
        lblSubtitle.setFont(UITheme.FONT_BODY);
        lblSubtitle.setForeground(UITheme.TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Corrected method names and added explicit centering
        txtUsername = createStyledTextField();
        txtUsername.setAlignmentX(Component.CENTER_ALIGNMENT); 

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT); 

        JButton btnLogin = new JButton("L O G I N");
        UITheme.styleFlatButton(btnLogin, UITheme.ACCENT, UITheme.ACCENT_HOVER, UITheme.PRIMARY_BG);
        btnLogin.setMaximumSize(new Dimension(280, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT); // Centers the login button
        btnLogin.addActionListener(e -> attemptLogin());

        JLabel lblRegister = new JLabel("Create a new Cashier account");
        lblRegister.setFont(UITheme.FONT_SMALL);
        lblRegister.setForeground(UITheme.TEXT_SECONDARY);
        lblRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { lblRegister.setForeground(UITheme.ACCENT); }
            @Override
            public void mouseExited(MouseEvent e) { lblRegister.setForeground(UITheme.TEXT_SECONDARY); }
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterFrame(LoginFrame.this).setVisible(true);
                setVisible(false);
            }
        });

        lblError = new JLabel(" ");
        lblError.setForeground(UITheme.DANGER);
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(lblTitle);
        mainPanel.add(lblSubtitle);
        mainPanel.add(Box.createVerticalStrut(40));
        
        mainPanel.add(createLabel("Username"));
        mainPanel.add(txtUsername);
        mainPanel.add(Box.createVerticalStrut(15));
        
        mainPanel.add(createLabel("Password"));
        mainPanel.add(txtPassword);
        mainPanel.add(Box.createVerticalStrut(10));
        
        mainPanel.add(lblError);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(btnLogin);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(lblRegister);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.PRIMARY_BG);
        wrapper.add(mainPanel);
        
        add(wrapper, BorderLayout.CENTER);
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        try {
            User user = authService.login(username, password);
            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            lblError.setText(ex.getMessage());
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        styleTextField(tf);
        return tf;
    }

    private void styleTextField(JTextField tf) {
        tf.setMaximumSize(new Dimension(280, 40));
        tf.setBackground(UITheme.PRIMARY_BG); 
        tf.setForeground(UITheme.TEXT_PRIMARY);
        tf.setCaretColor(UITheme.TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            UITheme.createRoundedBorder(UITheme.SECONDARY_BG, 5, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        tf.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}