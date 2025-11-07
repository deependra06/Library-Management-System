package ui;

import services.LibraryService;
import services.AuthService;
import services.BookRequestService;
import utils.Constants;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
  private LibraryService libraryService;
  private AuthService authService;
  private BookRequestService requestService;
  private JTabbedPane tabbedPane;
  private LoginPanel loginPanel;

  public MainFrame() {
    libraryService = new LibraryService();
    authService = new AuthService();
    requestService = new BookRequestService(libraryService);
    initializeUI();
    showLogin();
  }

  private void initializeUI() {
    setTitle(Constants.APP_NAME);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(Constants.WINDOW_SIZE);
    setLocationRelativeTo(null);
    setIconImage(createAppIcon());
  }

  private Image createAppIcon() {
    int size = 32;
    java.awt.Image icon = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = (Graphics2D) icon.getGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setColor(Constants.PRIMARY_COLOR);
    g2d.fillRoundRect(5, 5, 22, 24, 5, 5);
    g2d.setColor(Color.WHITE);
    g2d.fillRoundRect(8, 8, 16, 18, 3, 3);
    g2d.setColor(Constants.PRIMARY_COLOR);
    g2d.fillRect(12, 10, 8, 14);
    g2d.dispose();
    return icon;
  }

  private void showLogin() {
    getContentPane().removeAll();
    loginPanel = new LoginPanel(authService, this);
    add(loginPanel);
    revalidate();
    repaint();
  }

  public void onLoginSuccess() {
    getContentPane().removeAll();

    tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

    // Common tabs for all users
    tabbedPane.addTab("📊 Dashboard", new DashboardPanel(libraryService, authService, this));

    // Different tabs based on role
    if (authService.isMember()) {
      // Member specific tabs
      tabbedPane.addTab("🔍 Browse Books", new SearchPanel(libraryService, authService, requestService));
      tabbedPane.addTab("📖 My Books", new MyBooksPanel(libraryService, authService, this));
    } else {
      // Admin/Librarian tabs
      tabbedPane.addTab("🔍 Search Books", new SearchPanel(libraryService, authService, requestService));
      tabbedPane.addTab("📚 Issue/Return", new IssueBookPanel(libraryService, authService, this));

      if (authService.canManageBooks()) {
        tabbedPane.addTab("➕ Add Book", new AddBookPanel(libraryService, this));
        tabbedPane.addTab("⚙️ Manage Books", new BookManagementPanel(libraryService, authService, this));
      }

      if (authService.canProcessRequests()) {
        tabbedPane.addTab("📋 Book Requests", new BookRequestsPanel(libraryService, authService, requestService, this));
      }
    }

    add(tabbedPane);
    setupMenuBar();
    revalidate();
    repaint();

    // Welcome message
    String welcomeMessage = String.format(
        "<html><div style='text-align: center;'>"
            + "<h2>Welcome back, %s!</h2>"
            + "<p>Role: <b style='color: %s;'>%s</b></p>"
            + "<p>You have access to %d books in the library.</p>"
            + "</div></html>",
        authService.getCurrentUser().getUsername(),
        getRoleColor(authService.getCurrentUser().getRole()),
        authService.getCurrentUser().getRole(),
        libraryService.getTotalBooks());

    JOptionPane.showMessageDialog(this, welcomeMessage, "Welcome", JOptionPane.INFORMATION_MESSAGE);
  }

  private String getRoleColor(models.UserRole role) {
    switch (role) {
      case ADMIN:
        return "#FFD700"; // Gold
      case LIBRARIAN:
        return "#32CD32"; // Lime Green
      case MEMBER:
        return "#87CEEB"; // Sky Blue
      default:
        return "#000000";
    }
  }

  private void setupMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    menuBar.setBackground(Constants.PRIMARY_COLOR);
    menuBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

    // File menu
    JMenu fileMenu = new JMenu("File");
    fileMenu.setForeground(Color.WHITE);
    fileMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));

    JMenuItem refreshItem = new JMenuItem("🔄 Refresh");
    JMenuItem logoutItem = new JMenuItem("🚪 Logout");
    JMenuItem exitItem = new JMenuItem("❌ Exit");

    refreshItem.addActionListener(e -> refreshAllTabs());
    logoutItem.addActionListener(e -> logout());
    exitItem.addActionListener(e -> System.exit(0));

    fileMenu.add(refreshItem);
    fileMenu.addSeparator();
    fileMenu.add(logoutItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);

    // Help menu
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setForeground(Color.WHITE);
    helpMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));

    JMenuItem aboutItem = new JMenuItem("ℹ️ About");
    aboutItem.addActionListener(e -> showAbout());
    helpMenu.add(aboutItem);

    menuBar.add(fileMenu);
    menuBar.add(helpMenu);

    // User info on the right
    if (authService.isLoggedIn()) {
      menuBar.add(Box.createHorizontalGlue());
      JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
      userPanel.setOpaque(false);

      String roleColor = getRoleColor(authService.getCurrentUser().getRole());
      JLabel userLabel = new JLabel(String.format(
          "<html><b>%s</b> <span style='color: %s; font-size: 10px;'>(%s)</span></html>",
          authService.getCurrentUser().getUsername(),
          roleColor,
          authService.getCurrentUser().getRole()));
      userLabel.setForeground(Color.WHITE);
      userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
      userLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

      userPanel.add(userLabel);
      menuBar.add(userPanel);
    }

    setJMenuBar(menuBar);
  }

  public void logout() {
    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to logout?",
        "Confirm Logout",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
      authService.logout();
      showLogin();
    }
  }

  private void showAbout() {
    String aboutText = String.format(
        "<html><div style='text-align: center;'>"
            + "<h2>%s</h2>"
            + "<p><b>Version:</b> 3.0.0</p>"
            + "<p><b>Developed by:</b> Library Team</p>"
            + "<hr>"
            + "<p>📚 <b>Role-Based Features:</b></p>"
            + "<ul style='text-align: left;'>"
            + "<li><b>👑 Admin:</b> Full system access</li>"
            + "<li><b>📚 Librarian:</b> Book management & requests</li>"
            + "<li><b>👤 Member:</b> Browse books & request issues</li>"
            + "</ul>"
            + "<p><b>Tech Stack:</b> Java Swing, File-based Data Storage</p>"
            + "<p><b>Total Books:</b> %d | <b>Available:</b> %d | <b>Issued:</b> %d</p>"
            + "</div></html>",
        Constants.APP_NAME,
        libraryService.getTotalBooks(),
        libraryService.getAvailableBooksCount(),
        libraryService.getIssuedBooksCount());

    JOptionPane.showMessageDialog(this, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
  }

  public void refreshDashboard() {
    tabbedPane.setComponentAt(0, new DashboardPanel(libraryService, authService, this));
  }

  public void refreshAllTabs() {
    int selectedIndex = tabbedPane.getSelectedIndex();
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      Component comp = tabbedPane.getComponentAt(i);
      if (comp instanceof Refreshable) {
        ((Refreshable) comp).refresh();
      }
    }
    tabbedPane.setSelectedIndex(selectedIndex);
    JOptionPane.showMessageDialog(this, "All data has been refreshed!", "Refresh Complete",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void showAddBookTab() {
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      if (tabbedPane.getTitleAt(i).contains("Add Book")) {
        tabbedPane.setSelectedIndex(i);
        return;
      }
    }
  }

  public interface Refreshable {
    void refresh();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // Enhanced UI settings
        UIManager.put("Button.background", Constants.BTN_PRIMARY);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("Label.font", Constants.NORMAL_FONT);
        UIManager.put("TextField.font", Constants.NORMAL_FONT);
        UIManager.put("ComboBox.font", Constants.NORMAL_FONT);
      } catch (Exception e) {
        e.printStackTrace();
      }
      new MainFrame().setVisible(true);
    });
  }
}