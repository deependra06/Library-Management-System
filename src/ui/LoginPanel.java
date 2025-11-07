package ui;

import services.AuthService;
import models.UserRole;
import utils.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {
  private AuthService authService;
  private MainFrame mainFrame;
  private JTextField usernameField;
  private JPasswordField passwordField;
  private JButton loginButton, registerButton;

  public LoginPanel(AuthService authService, MainFrame mainFrame) {
    this.authService = authService;
    this.mainFrame = mainFrame;
    initializeUI();
  }

  private void initializeUI() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
    setBackground(Constants.LIGHT_BG);

    // Main container with shadow effect
    JPanel mainContainer = new JPanel(new BorderLayout());
    mainContainer.setBackground(Constants.LIGHT_BG);
    
    JPanel centerPanel = new JPanel(new GridBagLayout());
    centerPanel.setBackground(Color.WHITE);
    centerPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Constants.BORDER_COLOR, 2),
        BorderFactory.createEmptyBorder(40, 40, 40, 40)));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(15, 15, 15, 15);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Title with icon
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    JLabel titleLabel = new JLabel("📚 Library Management System", JLabel.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    titleLabel.setForeground(Constants.PRIMARY_COLOR);
    centerPanel.add(titleLabel, gbc);

    // Subtitle
    gbc.gridy = 1;
    JLabel subtitleLabel = new JLabel("Sign in to your account", JLabel.CENTER);
    subtitleLabel.setFont(Constants.NORMAL_FONT);
    subtitleLabel.setForeground(Constants.TEXT_SECONDARY);
    centerPanel.add(subtitleLabel, gbc);

    // Username
    gbc.gridwidth = 1;
    gbc.gridy = 2;
    gbc.gridx = 0;
    JLabel userLabel = new JLabel("Username:");
    userLabel.setFont(Constants.NORMAL_FONT);
    userLabel.setForeground(Constants.TEXT_PRIMARY);
    centerPanel.add(userLabel, gbc);

    gbc.gridx = 1;
    usernameField = new JTextField(25);
    usernameField.setFont(Constants.NORMAL_FONT);
    usernameField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
    ));
    centerPanel.add(usernameField, gbc);

    // Password
    gbc.gridx = 0;
    gbc.gridy = 3;
    JLabel passLabel = new JLabel("Password:");
    passLabel.setFont(Constants.NORMAL_FONT);
    passLabel.setForeground(Constants.TEXT_PRIMARY);
    centerPanel.add(passLabel, gbc);

    gbc.gridx = 1;
    passwordField = new JPasswordField(25);
    passwordField.setFont(Constants.NORMAL_FONT);
    passwordField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
    ));
    centerPanel.add(passwordField, gbc);

    // Buttons
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
    buttonPanel.setBackground(Color.WHITE);

    loginButton = createEnhancedButton("🔐 Login", Constants.BTN_PRIMARY);
    registerButton = createEnhancedButton("📝 Register", Constants.BTN_SECONDARY);

    loginButton.addActionListener(this::performLogin);
    registerButton.addActionListener(this::showRegistrationDialog);

    // Make login button larger
    loginButton.setPreferredSize(new Dimension(140, 45));
    registerButton.setPreferredSize(new Dimension(140, 45));

    buttonPanel.add(loginButton);
    buttonPanel.add(registerButton);
    centerPanel.add(buttonPanel, gbc);

    // Default credentials hint
    gbc.gridy = 5;
    JPanel hintPanel = new JPanel(new BorderLayout());
    hintPanel.setBackground(Color.WHITE);
    hintPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
    
    JLabel hintLabel = new JLabel("<html><div style='text-align: center; color: #555;'>"
        + "<b>Demo Credentials:</b><br>"
        + "<table align='center' cellpadding='5'>"
        + "<tr><td align='left'>👑 Admin:</td><td align='left'><b>admin</b> / <b>admin123</b></td></tr>"
        + "<tr><td align='left'>📚 Librarian:</td><td align='left'><b>librarian</b> / <b>lib123</b></td></tr>"
        + "<tr><td align='left'>👤 Member:</td><td align='left'><b>member</b> / <b>member123</b></td></tr>"
        + "</table></div></html>", JLabel.CENTER);
    hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    hintLabel.setForeground(new Color(85, 85, 85));
    
    hintPanel.add(hintLabel, BorderLayout.CENTER);
    centerPanel.add(hintPanel, gbc);

    mainContainer.add(centerPanel, BorderLayout.CENTER);
    add(mainContainer, BorderLayout.CENTER);

    // Set focus to username field
    SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
  }

  private JButton createEnhancedButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color.darker(), 2),
        BorderFactory.createEmptyBorder(12, 20, 12, 20)
    ));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Enhanced hover effect with smooth transition
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      private Color originalColor = color;
      
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          // Darken the color for hover
          button.setBackground(new Color(
              Math.max(0, color.getRed() - 25),
              Math.max(0, color.getGreen() - 25),
              Math.max(0, color.getBlue() - 25)
          ));
          button.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(button.getBackground().darker(), 2),
              BorderFactory.createEmptyBorder(12, 20, 12, 20)
          ));
        }
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(originalColor);
          button.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(originalColor.darker(), 2),
              BorderFactory.createEmptyBorder(12, 20, 12, 20)
          ));
        }
      }
      
      public void mousePressed(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(originalColor.darker().darker());
        }
      }
    });
    
    return button;
  }

  private void performLogin(ActionEvent e) {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
      showStyledMessage("Please enter both username and password!", 
          "Input Required", JOptionPane.WARNING_MESSAGE);
      return;
    }

    // Show loading state
    loginButton.setText("⏳ Logging in...");
    loginButton.setEnabled(false);
    registerButton.setEnabled(false);

    // Simulate network delay and process login
    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        Thread.sleep(500); // Simulate network delay
        return authService.login(username, password);
      }

      @Override
      protected void done() {
        try {
          boolean success = get();
          if (success) {
            showStyledMessage("Login successful! Welcome back, " + username + "!", 
                "Welcome", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.onLoginSuccess();
          } else {
            showStyledMessage("Invalid username or password. Please try again.", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
          }
        } catch (Exception ex) {
          showStyledMessage("An error occurred during login. Please try again.", 
              "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
          loginButton.setText("🔐 Login");
          loginButton.setEnabled(true);
          registerButton.setEnabled(true);
        }
      }
    }.execute();
  }

  private void showRegistrationDialog(ActionEvent e) {
    // Create a custom dialog for registration
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Account", true);
    dialog.setLayout(new BorderLayout());
    dialog.setSize(400, 450);
    dialog.setLocationRelativeTo(this);
    dialog.getContentPane().setBackground(Color.WHITE);

    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(Color.WHITE);
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;

    // Title
    JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(Constants.PRIMARY_COLOR);
    formPanel.add(titleLabel, gbc);

    gbc.gridwidth = 1;
    gbc.gridy++;

    // Username
    gbc.gridx = 0;
    formPanel.add(new JLabel("Username:"), gbc);
    gbc.gridx = 1;
    JTextField regUsernameField = new JTextField(20);
    formPanel.add(regUsernameField, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    formPanel.add(new JLabel("Password:"), gbc);
    gbc.gridx = 1;
    JPasswordField regPasswordField = new JPasswordField(20);
    formPanel.add(regPasswordField, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    formPanel.add(new JLabel("Confirm Password:"), gbc);
    gbc.gridx = 1;
    JPasswordField regConfirmField = new JPasswordField(20);
    formPanel.add(regConfirmField, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    formPanel.add(new JLabel("Email:"), gbc);
    gbc.gridx = 1;
    JTextField regEmailField = new JTextField(20);
    formPanel.add(regEmailField, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    formPanel.add(new JLabel("Role:"), gbc);
    gbc.gridx = 1;
    JComboBox<UserRole> roleCombo = new JComboBox<>(new UserRole[] { UserRole.MEMBER });
    roleCombo.setSelectedItem(UserRole.MEMBER);
    formPanel.add(roleCombo, gbc);

    // Buttons
    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton registerBtn = createEnhancedButton("Create Account", Constants.BTN_SUCCESS);
    JButton cancelBtn = createEnhancedButton("Cancel", Constants.BTN_DANGER);

    registerBtn.addActionListener(ev -> {
      String username = regUsernameField.getText().trim();
      String password = new String(regPasswordField.getPassword());
      String confirmPassword = new String(regConfirmField.getPassword());
      String email = regEmailField.getText().trim();
      UserRole role = (UserRole) roleCombo.getSelectedItem();

      if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
        showStyledMessage("Please fill all fields!", "Input Required", JOptionPane.WARNING_MESSAGE);
        return;
      }

      if (!password.equals(confirmPassword)) {
        showStyledMessage("Passwords do not match!", "Input Error", JOptionPane.WARNING_MESSAGE);
        return;
      }

      if (authService.register(username, password, email, role)) {
        showStyledMessage("Registration successful! You can now login with your credentials.",
            "Success", JOptionPane.INFORMATION_MESSAGE);
        dialog.dispose();
      } else {
        showStyledMessage("Username already exists! Please choose a different username.",
            "Registration Failed", JOptionPane.ERROR_MESSAGE);
      }
    });

    cancelBtn.addActionListener(ev -> dialog.dispose());

    buttonPanel.add(registerBtn);
    buttonPanel.add(cancelBtn);
    formPanel.add(buttonPanel, gbc);

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.setVisible(true);
  }

  private void showStyledMessage(String message, String title, int messageType) {
    JOptionPane.showMessageDialog(this, 
        "<html><div style='text-align: center; padding: 10px;'>" + message + "</div></html>", 
        title, messageType);
  }

  public void clearFields() {
    usernameField.setText("");
    passwordField.setText("");
    usernameField.requestFocus();
  }
}