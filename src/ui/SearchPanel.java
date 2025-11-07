package ui;

import services.LibraryService;
import services.AuthService;
import services.BookRequestService;
import models.Book;
import models.BookRequest;
import utils.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchPanel extends JPanel implements MainFrame.Refreshable {
  private LibraryService libraryService;
  private AuthService authService;
  private BookRequestService requestService;
  private JTextField searchField;
  private JTable resultsTable;
  private DefaultTableModel tableModel;

  public SearchPanel(LibraryService libraryService, AuthService authService, BookRequestService requestService) {
    this.libraryService = libraryService;
    this.authService = authService;
    this.requestService = requestService;
    initializeUI();
    showAllBooks();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    setBackground(Constants.LIGHT_BG);

    add(createSearchPanel(), BorderLayout.NORTH);
    add(createResultsTable(), BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel createSearchPanel() {
    JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
    searchPanel.setBackground(Constants.LIGHT_BG);

    JLabel titleLabel = new JLabel("🔍 Search Books");
    titleLabel.setFont(Constants.HEADER_FONT);
    titleLabel.setForeground(Constants.PRIMARY_COLOR);

    JPanel searchControls = new JPanel(new BorderLayout(5, 5));
    searchControls.setBackground(Constants.LIGHT_BG);

    searchField = new JTextField();
    JButton searchButton = createStyledButton("Search", Constants.BTN_PRIMARY);

    searchControls.add(new JLabel("Search by title, author, or ISBN:"), BorderLayout.WEST);
    searchControls.add(searchField, BorderLayout.CENTER);
    searchControls.add(searchButton, BorderLayout.EAST);

    searchButton.addActionListener(e -> performSearch());
    searchField.addActionListener(e -> performSearch());

    searchPanel.add(titleLabel, BorderLayout.NORTH);
    searchPanel.add(searchControls, BorderLayout.SOUTH);

    return searchPanel;
  }

  private JScrollPane createResultsTable() {
    String[] columns = authService.isMember() ? new String[] { "ISBN", "Title", "Author", "Genre", "Status", "Action" }
        : new String[] { "ISBN", "Title", "Author", "Genre", "Status", "Issued To" };

    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return authService.isMember() && column == 5;
      }
    };

    resultsTable = new JTable(tableModel);
    resultsTable.setRowHeight(35);

    if (authService.isMember()) {
      resultsTable.getColumnModel().getColumn(5).setCellRenderer(new RequestButtonRenderer());
      resultsTable.getColumnModel().getColumn(5)
          .setCellEditor(new RequestButtonEditor(new JCheckBox(), requestService, authService, this));
    }

    return new JScrollPane(resultsTable);
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setBackground(Constants.LIGHT_BG);

    JButton showAllButton = createStyledButton("Show All Books", Constants.BTN_PRIMARY);
    JButton showAvailableButton = createStyledButton("Show Available", Constants.BTN_SUCCESS);
    JButton showIssuedButton = createStyledButton("Show Issued", Constants.BTN_WARNING);
    JButton refreshButton = createStyledButton("🔄 Refresh", Constants.BTN_SECONDARY);

    showAllButton.addActionListener(e -> showAllBooks());
    showAvailableButton.addActionListener(e -> showAvailableBooks());
    showIssuedButton.addActionListener(e -> showIssuedBooks());
    refreshButton.addActionListener(e -> refresh());

    buttonPanel.add(showAllButton);
    buttonPanel.add(showAvailableButton);
    buttonPanel.add(showIssuedButton);
    buttonPanel.add(refreshButton);

    return buttonPanel;
  }

  private JButton createStyledButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setFont(Constants.NORMAL_FONT);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    return button;
  }

  private void performSearch() {
    String keyword = searchField.getText().trim();
    displayBooks(keyword.isEmpty() ? libraryService.getAllBooks() : libraryService.searchBooks(keyword));
  }

  private void showAllBooks() {
    displayBooks(libraryService.getAllBooks());
  }

  private void showAvailableBooks() {
    displayBooks(libraryService.getAvailableBooks());
  }

  private void showIssuedBooks() {
    displayBooks(libraryService.getIssuedBooks());
  }

  private void displayBooks(List<Book> books) {
    tableModel.setRowCount(0);
    for (Book book : books) {
      if (authService.isMember()) {
        tableModel.addRow(new Object[] {
            book.getIsbn(),
            book.getTitle(),
            book.getAuthor(),
            book.getGenre(),
            book.isIssued() ? "🔴 Issued" : "🟢 Available",
            book.isIssued() ? "Not Available" : "Request Book"
        });
      } else {
        tableModel.addRow(new Object[] {
            book.getIsbn(),
            book.getTitle(),
            book.getAuthor(),
            book.getGenre(),
            book.isIssued() ? "Issued" : "Available",
            book.isIssued() ? book.getIssuedTo() : "-"
        });
      }
    }
  }

  @Override
  public void refresh() {
    showAllBooks();
  }

  // Button renderer and editor for member book requests
  private static class RequestButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
    public RequestButtonRenderer() {
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      setText((value == null) ? "" : value.toString());
      if ("Request Book".equals(value)) {
        setBackground(Constants.BTN_SUCCESS);
        setForeground(Color.WHITE);
      } else {
        setBackground(Constants.BTN_SECONDARY);
        setForeground(Color.WHITE);
        setEnabled(false);
      }
      return this;
    }
  }

  private static class RequestButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String isbn;
    private BookRequestService requestService;
    private AuthService authService;
    private SearchPanel panel;

    public RequestButtonEditor(JCheckBox checkBox, BookRequestService requestService,
        AuthService authService, SearchPanel panel) {
      super(checkBox);
      this.requestService = requestService;
      this.authService = authService;
      this.panel = panel;
      button = new JButton();
      button.setOpaque(true);
      button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      isbn = (String) table.getValueAt(row, 0);
      String status = (String) table.getValueAt(row, 4);

      if ("🟢 Available".equals(status)) {
        button.setText("Requesting...");
        button.setBackground(Constants.BTN_WARNING);
      } else {
        button.setText("Not Available");
        button.setBackground(Constants.BTN_SECONDARY);
        button.setEnabled(false);
      }
      return button;
    }

    public Object getCellEditorValue() {
      String status = "Request Book"; // Reset to original text

      // Request the book
      BookRequest request = requestService.requestBook(isbn, authService.getCurrentUser().getUsername());
      if (request != null) {
        JOptionPane.showMessageDialog(button,
            "Book request submitted successfully!\nYour request ID: " + request.getRequestId(),
            "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
        panel.refresh();
      } else {
        JOptionPane.showMessageDialog(button,
            "Unable to request book. It might be already requested or unavailable.",
            "Request Failed", JOptionPane.ERROR_MESSAGE);
      }

      return status;
    }
  }
}