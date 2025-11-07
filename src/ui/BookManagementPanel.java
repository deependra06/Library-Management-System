package ui;

import ui.components.BookTable;
import utils.Constants;
import models.Book;
import services.LibraryService;
import services.AuthService;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookManagementPanel extends JPanel implements MainFrame.Refreshable {
  private final LibraryService libraryService;
  private final MainFrame mainFrame;
  private BookTable bookTable;
  private JTextField searchField;

  public BookManagementPanel(LibraryService libraryService, AuthService authService, MainFrame mainFrame) {
    this.libraryService = libraryService;
    this.mainFrame = mainFrame;
    initializeUI();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    setBackground(Constants.LIGHT_BG);

    // Header with search
    add(createHeaderPanel(), BorderLayout.NORTH);

    // Book table
    bookTable = new BookTable(libraryService.getAllBooks());
    JScrollPane scrollPane = new JScrollPane(bookTable);
    scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
    add(scrollPane, BorderLayout.CENTER);

    // Action buttons
    add(createActionPanel(), BorderLayout.SOUTH);
  }

  private JPanel createHeaderPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(Constants.LIGHT_BG);

    JLabel titleLabel = new JLabel("Book Management");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(Constants.PRIMARY_COLOR);

    // Search panel
    JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
    searchPanel.setBackground(Constants.LIGHT_BG);

    searchField = new JTextField(20);
    searchField.setFont(Constants.NORMAL_FONT);
    JButton searchBtn = createStyledButton("🔍 Search", Constants.BTN_PRIMARY);

    JLabel searchLabel = new JLabel("Search:");
    searchLabel.setFont(Constants.NORMAL_FONT);
    searchLabel.setForeground(Constants.TEXT_PRIMARY);

    searchPanel.add(searchLabel, BorderLayout.WEST);
    searchPanel.add(searchField, BorderLayout.CENTER);
    searchPanel.add(searchBtn, BorderLayout.EAST);

    searchBtn.addActionListener(e -> performSearch());
    searchField.addActionListener(e -> performSearch());

    panel.add(titleLabel, BorderLayout.WEST);
    panel.add(searchPanel, BorderLayout.EAST);

    return panel;
  }

  private JPanel createActionPanel() {
    JPanel panel = new JPanel(new FlowLayout());
    panel.setBackground(Constants.LIGHT_BG);

    JButton addBtn = createStyledButton("➕ Add Book", Constants.BTN_PRIMARY);
    JButton issueBtn = createStyledButton("📅 Issue Book", Constants.BTN_SUCCESS);
    JButton returnBtn = createStyledButton("↩️ Return Book", Constants.BTN_WARNING);
    JButton deleteBtn = createStyledButton("🗑️ Delete Book", Constants.BTN_DANGER);
    JButton refreshBtn = createStyledButton("🔄 Refresh", Constants.BTN_SECONDARY);

    addBtn.addActionListener(e -> navigateToAddBook());
    issueBtn.addActionListener(e -> showIssueBookDialog());
    returnBtn.addActionListener(e -> returnSelectedBook());
    deleteBtn.addActionListener(e -> deleteSelectedBook());
    refreshBtn.addActionListener(e -> refresh());

    panel.add(addBtn);
    panel.add(issueBtn);
    panel.add(returnBtn);
    panel.add(deleteBtn);
    panel.add(refreshBtn);

    return panel;
  }

  private JButton createStyledButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setFont(Constants.NORMAL_FONT);
    button.setBackground(color);
    button.setForeground(Constants.BTN_TEXT_COLOR);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color.darker(), 1),
        BorderFactory.createEmptyBorder(10, 15, 10, 15)
    ));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Add hover effect
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          if (color == Constants.BTN_PRIMARY) button.setBackground(Constants.BTN_HOVER_PRIMARY);
          else if (color == Constants.BTN_SUCCESS) button.setBackground(Constants.BTN_HOVER_SUCCESS);
          else if (color == Constants.BTN_WARNING) button.setBackground(Constants.BTN_HOVER_WARNING);
          else if (color == Constants.BTN_DANGER) button.setBackground(Constants.BTN_HOVER_DANGER);
          else button.setBackground(Constants.BTN_SECONDARY.darker());
        }
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(color);
        }
      }
    });
    
    return button;
  }

  private void navigateToAddBook() {
    mainFrame.showAddBookTab();
  }

  private void performSearch() {
    String query = searchField.getText().trim();
    List<Book> results = query.isEmpty() ? libraryService.getAllBooks() : libraryService.searchBooks(query);
    bookTable.updateData(results);
  }

  private void showIssueBookDialog() {
    Book selected = bookTable.getSelectedBook();
    if (selected == null) {
      showMessage("Please select a book first!");
      return;
    }
    if (selected.isIssued()) {
      showMessage("This book is already issued!");
      return;
    }

    JTextField userField = new JTextField();
    JTextField daysField = new JTextField("14");

    Object[] fields = {
        "Issued to:", userField,
        "Days:", daysField
    };

    int result = JOptionPane.showConfirmDialog(this, fields,
        "Issue Book: " + selected.getTitle(), JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
      try {
        boolean issued = libraryService.issueBook(
            selected.getIsbn(), userField.getText(),
            Integer.parseInt(daysField.getText()));
        showMessage(issued ? "Book issued successfully!" : "Failed to issue book.");
        refresh();
      } catch (NumberFormatException e) {
        showMessage("Please enter valid number of days!");
      }
    }
  }

  private void returnSelectedBook() {
    Book selected = bookTable.getSelectedBook();
    if (selected == null) {
      showMessage("Please select a book first!");
      return;
    }

    boolean returned = libraryService.returnBook(selected.getIsbn());
    showMessage(returned ? "Book returned successfully!" : "Failed to return book.");
    refresh();
  }

  private void deleteSelectedBook() {
    Book selected = bookTable.getSelectedBook();
    if (selected == null) {
      showMessage("Please select a book first!");
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to delete: " + selected.getTitle() + "?",
        "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      boolean deleted = libraryService.removeBook(selected.getIsbn());
      showMessage(deleted ? "Book deleted successfully!" : "Failed to delete book.");
      refresh();
    }
  }

  private void showMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

  @Override
  public void refresh() {
    bookTable.updateData(libraryService.getAllBooks());
    mainFrame.refreshAllTabs();
  }
}