package ui;

import services.LibraryService;
import services.AuthService;
import services.BookRequestService;
import models.Book;
import models.BookRequest;
import utils.DateUtils;
import utils.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookRequestsPanel extends JPanel implements MainFrame.Refreshable {
  private LibraryService libraryService;
  private BookRequestService requestService;
  private JTable requestsTable;
  private DefaultTableModel tableModel;

  public BookRequestsPanel(LibraryService libraryService, AuthService authService,
      BookRequestService requestService, MainFrame mainFrame) {
    this.libraryService = libraryService;
    this.requestService = requestService;
    initializeUI();
    loadRequests();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    setBackground(Constants.LIGHT_BG);

    // Header
    JLabel titleLabel = new JLabel("📋 Book Requests");
    titleLabel.setFont(Constants.HEADER_FONT);
    titleLabel.setForeground(Constants.PRIMARY_COLOR);
    add(titleLabel, BorderLayout.NORTH);

    // Requests table
    String[] columns = { "Request ID", "ISBN", "Book Title", "Requested By", "Request Date", "Status", "Actions" };
    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 6; // Only actions column is editable
      }
    };

    requestsTable = new JTable(tableModel);
    requestsTable.setRowHeight(35);
    requestsTable.getColumnModel().getColumn(6).setCellRenderer(new RequestsButtonRenderer());
    requestsTable.getColumnModel().getColumn(6)
        .setCellEditor(new RequestsButtonEditor(new JCheckBox(), requestService, this));

    JScrollPane scrollPane = new JScrollPane(requestsTable);
    add(scrollPane, BorderLayout.CENTER);

    // Buttons panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(Constants.LIGHT_BG);

    JButton refreshBtn = createStyledButton("🔄 Refresh", Constants.BTN_PRIMARY);
    refreshBtn.addActionListener(e -> refresh());

    buttonPanel.add(refreshBtn);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void loadRequests() {
    tableModel.setRowCount(0);
    List<BookRequest> requests = requestService.getPendingRequests();

    for (BookRequest request : requests) {
      Book book = libraryService.findBookByIsbn(request.getIsbn());
      String bookTitle = book != null ? book.getTitle() : "Unknown Book";

      tableModel.addRow(new Object[] {
          request.getRequestId(),
          request.getIsbn(),
          bookTitle,
          request.getUsername(),
          DateUtils.formatDate(request.getRequestDate()),
          request.getStatus().toString(),
          "Approve/Reject"
      });
    }

    if (requests.isEmpty()) {
      // Add a message row
      tableModel.addRow(new Object[] { "", "No pending requests", "", "", "", "", "" });
    }
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

  @Override
  public void refresh() {
    loadRequests();
  }

  // Button renderer and editor for requests
  private static class RequestsButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
    public RequestsButtonRenderer() {
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      setText((value == null) ? "" : value.toString());
      setBackground(Constants.BTN_WARNING);
      setForeground(Color.WHITE);
      return this;
    }
  }

  private static class RequestsButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String requestId;
    private BookRequestService requestService;
    private BookRequestsPanel panel;

    public RequestsButtonEditor(JCheckBox checkBox, BookRequestService requestService, BookRequestsPanel panel) {
      super(checkBox);
      this.requestService = requestService;
      this.panel = panel;
      button = new JButton();
      button.setOpaque(true);
      button.addActionListener(e -> showActionDialog());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      requestId = (String) table.getValueAt(row, 0);
      button.setText("Process...");
      button.setBackground(Constants.BTN_WARNING);
      return button;
    }

    private void showActionDialog() {
      Object[] options = { "Approve", "Reject", "Cancel" };
      int choice = JOptionPane.showOptionDialog(button,
          "How would you like to process this request?",
          "Process Book Request",
          JOptionPane.YES_NO_CANCEL_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,
          options,
          options[2]);

      if (choice == 0) { // Approve
        String daysStr = JOptionPane.showInputDialog(button,
            "Enter number of days for issue:", "14");
        try {
          int days = daysStr != null ? Integer.parseInt(daysStr) : 14;
          if (requestService.approveRequest(requestId, days)) {
            JOptionPane.showMessageDialog(button, "Request approved successfully!");
            panel.refresh();
          } else {
            JOptionPane.showMessageDialog(button, "Failed to approve request!", "Error", JOptionPane.ERROR_MESSAGE);
          }
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(button, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
      } else if (choice == 1) { // Reject
        if (requestService.rejectRequest(requestId)) {
          JOptionPane.showMessageDialog(button, "Request rejected!");
          panel.refresh();
        }
      }
    }

    public Object getCellEditorValue() {
      return "Approve/Reject";
    }
  }
}