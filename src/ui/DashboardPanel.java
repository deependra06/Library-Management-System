package ui;

import models.Book;
import services.LibraryService;
import ui.components.StatCard;
import services.AuthService;
import utils.Constants;
import utils.DateUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class DashboardPanel extends JPanel {
  private LibraryService libraryService;
  private AuthService authService;
  private MainFrame mainFrame;
  private JTable booksTable;
  private DefaultTableModel tableModel;

  public DashboardPanel(LibraryService libraryService, AuthService authService, MainFrame mainFrame) {
    this.libraryService = libraryService;
    this.authService = authService;
    this.mainFrame = mainFrame;
    initializeUI();
    loadBooks();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(15, 15));
    setBackground(Constants.LIGHT_BG);
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    add(createHeader(), BorderLayout.NORTH);
    add(createStatsPanel(), BorderLayout.CENTER);
    add(createBooksTable(), BorderLayout.SOUTH);
  }

  private JPanel createHeader() {
    JPanel header = new JPanel(new BorderLayout(10, 10));
    header.setBackground(Constants.LIGHT_BG);

    // Title with modern styling
    JLabel title = new JLabel("📊 Dashboard Overview", JLabel.LEFT);
    title.setFont(Constants.HEADER_FONT);
    title.setForeground(Constants.PRIMARY_COLOR);

    // User info card - enhanced design
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
    rightPanel.setBackground(Constants.LIGHT_BG);

    JPanel userInfoCard = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
    userInfoCard.setBackground(Constants.CARD_BG);
    userInfoCard.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(getRoleColor(), 3, true),
        BorderFactory.createEmptyBorder(8, 18, 8, 18)
    ));

    JLabel userIcon = new JLabel(getUserIcon());
    userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

    JLabel userInfoLabel = new JLabel(String.format(
        "<html><b style='font-size: 13px;'>%s</b><br><span style='color: %s; font-size: 11px;'>● %s</span></html>",
        authService.getCurrentUser().getUsername(),
        toHex(getRoleColor()),
        authService.getCurrentUser().getRole()
    ));
    userInfoLabel.setFont(Constants.SMALL_FONT);
    userInfoLabel.setForeground(Constants.TEXT_PRIMARY);

    userInfoCard.add(userIcon);
    userInfoCard.add(userInfoLabel);

    // Enhanced logout button
    JButton logoutButton = createModernButton("🚪 Logout", Constants.BTN_DANGER);
    logoutButton.addActionListener(e -> mainFrame.logout());

    rightPanel.add(userInfoCard);
    rightPanel.add(logoutButton);

    header.add(title, BorderLayout.WEST);
    header.add(rightPanel, BorderLayout.EAST);

    return header;
  }

  private String getUserIcon() {
    switch (authService.getCurrentUser().getRole()) {
      case ADMIN: return "👑";
      case LIBRARIAN: return "📚";
      case MEMBER: return "👤";
      default: return "👤";
    }
  }

  private Color getRoleColor() {
    switch (authService.getCurrentUser().getRole()) {
      case ADMIN: return Constants.ACCENT_GOLD;
      case LIBRARIAN: return Constants.SUCCESS_COLOR;
      case MEMBER: return Constants.INFO_COLOR;
      default: return Constants.TEXT_SECONDARY;
    }
  }

  private String toHex(Color color) {
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
  }

  private JPanel createStatsPanel() {
    JPanel statsPanel = new JPanel(new GridLayout(1, 3, 25, 25));
    statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    statsPanel.setBackground(Constants.LIGHT_BG);

    statsPanel.add(new StatCard("Total Books",
        String.valueOf(libraryService.getTotalBooks()), Constants.PRIMARY_COLOR));
    statsPanel.add(new StatCard("Available",
        String.valueOf(libraryService.getAvailableBooksCount()), Constants.SUCCESS_COLOR));
    statsPanel.add(new StatCard("Issued",
        String.valueOf(libraryService.getIssuedBooksCount()), Constants.WARNING_COLOR));

    return statsPanel;
  }

  private JPanel createBooksTable() {
    JPanel tablePanel = new JPanel(new BorderLayout(0, 12));
    tablePanel.setBackground(Constants.LIGHT_BG);

    // Enhanced table title with badge
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    titlePanel.setBackground(Constants.LIGHT_BG);
    
    JLabel tableTitle = new JLabel("📖 Recent Activity");
    tableTitle.setFont(Constants.TITLE_FONT);
    tableTitle.setForeground(Constants.TEXT_PRIMARY);
    
    JLabel badge = new JLabel(libraryService.getAllBooks().size() + " books");
    badge.setFont(Constants.SMALL_FONT);
    badge.setForeground(Constants.BTN_TEXT_COLOR);
    badge.setBackground(Constants.PRIMARY_COLOR);
    badge.setOpaque(true);
    badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
    
    titlePanel.add(tableTitle);
    titlePanel.add(badge);

    // Different columns based on user role
    String[] columns = authService.canManageBooks()
        ? new String[] { "ISBN", "Title", "Author", "Genre", "Status", "Issued To", "Due Date", "Actions" }
        : new String[] { "ISBN", "Title", "Author", "Genre", "Status", "Issued To", "Due Date" };

    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return authService.canManageBooks() && column == 7;
      }
    };

    booksTable = new JTable(tableModel);
    booksTable.setRowHeight(45);
    booksTable.setFont(Constants.NORMAL_FONT);
    booksTable.setShowGrid(true);
    booksTable.setGridColor(Constants.BORDER_COLOR);
    booksTable.setIntercellSpacing(new Dimension(1, 1));
    
    // Enhanced header styling with better visibility
    JTableHeader header = booksTable.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 15));
    header.setBackground(Constants.TABLE_HEADER_BG);
    header.setForeground(Constants.TABLE_HEADER_FG);
    header.setPreferredSize(new Dimension(0, 50));
    header.setReorderingAllowed(false);
    header.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 4, 0, Constants.TABLE_HEADER_BORDER),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
    ));
    
    // Custom header renderer for perfect text visibility
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel(value.toString(), JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(Color.WHITE);
        label.setBackground(Constants.TABLE_HEADER_BG);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return label;
      }
    });

    // Enhanced cell renderer
    booksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (c instanceof JLabel) {
          JLabel label = (JLabel) c;
          label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
          
          if (column == 4) {
            label.setHorizontalAlignment(JLabel.CENTER);
          } else {
            label.setHorizontalAlignment(JLabel.LEFT);
          }
        }
        
        c.setFont(Constants.NORMAL_FONT);
        c.setForeground(Constants.TEXT_PRIMARY);
        
        if (!isSelected) {
          c.setBackground(row % 2 == 0 ? Constants.TABLE_ROW_EVEN : Constants.TABLE_ROW_ODD);
        } else {
          c.setBackground(Constants.TABLE_SELECTION_BG);
          c.setForeground(Constants.TABLE_SELECTION_FG);
          c.setFont(c.getFont().deriveFont(Font.BOLD));
        }

        // Status column coloring
        if (column == 4) {
          String status = String.valueOf(value);
          c.setFont(new Font("Segoe UI", Font.BOLD, 14));
          if (status.contains("Available") || status.contains("🟢")) {
            c.setForeground(Constants.STATUS_AVAILABLE);
          } else if (status.contains("Issued") || status.contains("🔴")) {
            c.setForeground(Constants.STATUS_BORROWED);
          }
        }
        
        // Title column - bold
        if (column == 1) {
          c.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        return c;
      }
    });

    // Add action buttons for users who can manage books
    if (authService.canManageBooks()) {
      booksTable.getColumnModel().getColumn(7).setCellRenderer(new ActionButtonRenderer());
      booksTable.getColumnModel().getColumn(7)
          .setCellEditor(new ActionButtonEditor(new JCheckBox(), libraryService, this));
    }

    JScrollPane scrollPane = new JScrollPane(booksTable);
    scrollPane.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Constants.BORDER_COLOR, 2),
        BorderFactory.createEmptyBorder(0, 0, 0, 0)
    ));
    scrollPane.getViewport().setBackground(Color.WHITE);

    tablePanel.add(titlePanel, BorderLayout.NORTH);
    tablePanel.add(scrollPane, BorderLayout.CENTER);
    tablePanel.add(createButtonPanel(), BorderLayout.SOUTH);

    return tablePanel;
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
    buttonPanel.setBackground(Constants.LIGHT_BG);

    JButton refreshBtn = createModernButton("🔄 Refresh", Constants.BTN_PRIMARY);
    refreshBtn.addActionListener(e -> loadBooks());

    if (authService.canManageBooks()) {
      JButton returnBookBtn = createModernButton("↩️ Return Selected", Constants.BTN_SUCCESS);
      returnBookBtn.addActionListener(e -> returnSelectedBook());
      buttonPanel.add(returnBookBtn);
    }

    buttonPanel.add(refreshBtn);
    return buttonPanel;
  }

  private JButton createModernButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setFont(new Font("Segoe UI", Font.BOLD, 13));
    button.setBackground(color);
    button.setForeground(Constants.BTN_TEXT_COLOR);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color.darker(), 2, true),
        BorderFactory.createEmptyBorder(10, 20, 10, 20)
    ));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      private Color originalColor = color;
      
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          Color hoverColor = getHoverColor(originalColor);
          button.setBackground(hoverColor);
        }
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(originalColor);
        }
      }
    });
    
    return button;
  }

  private Color getHoverColor(Color baseColor) {
    if (baseColor.equals(Constants.BTN_PRIMARY)) return Constants.BTN_HOVER_PRIMARY;
    if (baseColor.equals(Constants.BTN_SUCCESS)) return Constants.BTN_HOVER_SUCCESS;
    if (baseColor.equals(Constants.BTN_WARNING)) return Constants.BTN_HOVER_WARNING;
    if (baseColor.equals(Constants.BTN_DANGER)) return Constants.BTN_HOVER_DANGER;
    return baseColor.darker();
  }

  private void loadBooks() {
    tableModel.setRowCount(0);
    for (Book book : libraryService.getAllBooks()) {
      if (authService.canManageBooks()) {
        tableModel.addRow(new Object[] {
            book.getIsbn(), 
            book.getTitle(), 
            book.getAuthor(), 
            book.getGenre(),
            book.isIssued() ? "🔴 Issued" : "🟢 Available",
            book.isIssued() ? book.getIssuedTo() : "-",
            book.isIssued() ? DateUtils.formatDate(book.getDueDate()) : "-",
            book.isIssued() ? "Return" : "-"
        });
      } else {
        tableModel.addRow(new Object[] {
            book.getIsbn(), 
            book.getTitle(), 
            book.getAuthor(), 
            book.getGenre(),
            book.isIssued() ? "Issued" : "Available",
            book.isIssued() ? book.getIssuedTo() : "-",
            book.isIssued() ? DateUtils.formatDate(book.getDueDate()) : "-"
        });
      }
    }
  }

  private void returnSelectedBook() {
    int selectedRow = booksTable.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this, "⚠️ Please select a book to return!", "No Selection", 
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    String isbn = (String) tableModel.getValueAt(selectedRow, 0);
    if (libraryService.returnBook(isbn)) {
      JOptionPane.showMessageDialog(this, "✅ Book returned successfully!", "Success", 
          JOptionPane.INFORMATION_MESSAGE);
      loadBooks();
      mainFrame.refreshDashboard();
    } else {
      JOptionPane.showMessageDialog(this, "❌ Failed to return book!", "Error", 
          JOptionPane.ERROR_MESSAGE);
    }
  }

  // Action button renderer
  private static class ActionButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
    public ActionButtonRenderer() {
      setOpaque(true);
      setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      setText((value == null) ? "" : value.toString());
      if ("Return".equals(value)) {
        setBackground(Constants.BTN_SUCCESS);
        setForeground(Constants.BTN_TEXT_COLOR);
        setEnabled(true);
      } else {
        setBackground(Constants.BTN_DISABLED);
        setForeground(Constants.TEXT_SECONDARY);
        setEnabled(false);
      }
      return this;
    }
  }

  // Action button editor
  private static class ActionButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String isbn;
    private LibraryService libraryService;
    private DashboardPanel panel;

    public ActionButtonEditor(JCheckBox checkBox, LibraryService libraryService, DashboardPanel panel) {
      super(checkBox);
      this.libraryService = libraryService;
      this.panel = panel;
      button = new JButton();
      button.setOpaque(true);
      button.setFont(new Font("Segoe UI", Font.BOLD, 12));
      button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      isbn = (String) table.getValueAt(row, 0);
      button.setText("Returning...");
      button.setBackground(Constants.BTN_WARNING);
      button.setForeground(Constants.BTN_TEXT_COLOR);
      return button;
    }

    public Object getCellEditorValue() {
      if (libraryService.returnBook(isbn)) {
        JOptionPane.showMessageDialog(button, "✅ Book returned successfully!", "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        panel.loadBooks();
      } else {
        JOptionPane.showMessageDialog(button, "❌ Failed to return book!", "Error", 
            JOptionPane.ERROR_MESSAGE);
      }
      return "Return";
    }
  }
}