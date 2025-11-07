package ui;

import services.LibraryService;
import services.AuthService;
import models.Book;
import utils.Constants;
import utils.DateUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MyBooksPanel extends JPanel implements MainFrame.Refreshable {
  private LibraryService libraryService;
  private AuthService authService;
  private JTable booksTable;
  private DefaultTableModel tableModel;

  public MyBooksPanel(LibraryService libraryService, AuthService authService, MainFrame mainFrame) {
    this.libraryService = libraryService;
    this.authService = authService;
    initializeUI();
    loadMyBooks();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    setBackground(Constants.LIGHT_BG);

    // Header
    JLabel titleLabel = new JLabel("My Issued Books");
    titleLabel.setFont(Constants.HEADER_FONT);
    titleLabel.setForeground(Constants.PRIMARY_COLOR);
    add(titleLabel, BorderLayout.NORTH);

    // Books table
    String[] columns = { "ISBN", "Title", "Author", "Genre", "Issue Date", "Due Date", "Days Left", "Actions" };
    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 7; // Only actions column is editable
      }
    };

    booksTable = new JTable(tableModel);
    booksTable.setRowHeight(35);
    booksTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
    booksTable.getColumnModel().getColumn(7)
        .setCellEditor(new ButtonEditor(new JCheckBox(), authService, libraryService, this));

    JScrollPane scrollPane = new JScrollPane(booksTable);
    scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
    add(scrollPane, BorderLayout.CENTER);

    // Refresh button
    JButton refreshBtn = createStyledButton("🔄 Refresh", Constants.BTN_PRIMARY);
    refreshBtn.addActionListener(e -> refresh());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(Constants.LIGHT_BG);
    buttonPanel.add(refreshBtn);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void loadMyBooks() {
    tableModel.setRowCount(0);
    String currentUser = authService.getCurrentUser().getUsername();

    List<Book> myBooks = libraryService.getIssuedBooks().stream()
        .filter(book -> book.getIssuedTo().equals(currentUser))
        .collect(Collectors.toList());

    for (Book book : myBooks) {
      long daysLeft = book.getDaysUntilDue();
      String daysLeftStr = daysLeft + " days";
      if (daysLeft < 0) {
        daysLeftStr = "<html><b style='color: red;'>Overdue " + Math.abs(daysLeft) + " days</b></html>";
      } else if (daysLeft == 0) {
        daysLeftStr = "<html><b style='color: orange;'>Due today</b></html>";
      }

      tableModel.addRow(new Object[] {
          book.getIsbn(),
          book.getTitle(),
          book.getAuthor(),
          book.getGenre(),
          DateUtils.formatDate(book.getIssueDate()),
          DateUtils.formatDate(book.getDueDate()),
          daysLeftStr,
          "Return Book"
      });
    }

    if (myBooks.isEmpty()) {
      JOptionPane.showMessageDialog(this, "You don't have any issued books.", "My Books",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  private JButton createStyledButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setFont(Constants.NORMAL_FONT);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
  }

  @Override
  public void refresh() {
    loadMyBooks();
  }

  // Button renderer and editor for action column
  private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
    public ButtonRenderer() {
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      setText((value == null) ? "" : value.toString());
      setBackground(Constants.BTN_SUCCESS);
      setForeground(Color.WHITE);
      return this;
    }
  }

  private static class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String isbn;
    private AuthService authService;
    private LibraryService libraryService;
    private MyBooksPanel panel;

    public ButtonEditor(JCheckBox checkBox, AuthService authService, LibraryService libraryService,
        MyBooksPanel panel) {
      super(checkBox);
      this.authService = authService;
      this.libraryService = libraryService;
      this.panel = panel;
      button = new JButton();
      button.setOpaque(true);
      button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      isbn = (String) table.getValueAt(row, 0);
      button.setText("Returning...");
      button.setBackground(Constants.BTN_WARNING);
      return button;
    }

    public Object getCellEditorValue() {
      // Return book
      if (libraryService.returnMyBook(isbn, authService.getCurrentUser().getUsername())) {
        JOptionPane.showMessageDialog(button, "Book returned successfully!");
        panel.refresh();
      } else {
        JOptionPane.showMessageDialog(button, "Failed to return book!", "Error", JOptionPane.ERROR_MESSAGE);
      }
      return "Return Book";
    }
  }
}