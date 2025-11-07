package ui.components;

import models.Book;
import utils.DateUtils;
import utils.Constants;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookTable extends JTable {
  private final DefaultTableModel model;

  public BookTable(List<Book> books) {
    String[] columns = { "ISBN", "Title", "Author", "Genre", "Status", "Issued To", "Due Date", "Days Left" };
    this.model = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }

      @Override
      public Class<?> getColumnClass(int column) {
        return String.class;
      }
    };
    setModel(model);

    // Enhanced styling with high contrast
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setAutoCreateRowSorter(true);
    setFillsViewportHeight(true);
    setRowHeight(35);
    setFont(Constants.NORMAL_FONT);
    setForeground(Constants.TEXT_PRIMARY);
    setBackground(Color.WHITE);
    
    // Header styling
    getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    getTableHeader().setBackground(Constants.TABLE_HEADER_BG);
    getTableHeader().setForeground(Constants.TABLE_HEADER_FG);
    getTableHeader().setPreferredSize(new Dimension(0, 35));

    // Custom cell renderer for status with high contrast
    setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Set default colors
        c.setForeground(Constants.TEXT_PRIMARY);
        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));

        if (column == 4) { // Status column
          if ("Available".equals(value)) {
            c.setForeground(Constants.STATUS_AVAILABLE);
            c.setFont(c.getFont().deriveFont(Font.BOLD));
          } else if ("Issued".equals(value)) {
            c.setForeground(Constants.STATUS_BORROWED);
            c.setFont(c.getFont().deriveFont(Font.BOLD));
          }
        } else if (column == 7) { // Days Left column
          if (value instanceof String) {
            String daysStr = (String) value;
            if (!"-".equals(daysStr)) {
              try {
                long days = Long.parseLong(daysStr);
                if (days < 0) {
                  c.setForeground(Constants.STATUS_OVERDUE);
                  c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (days < 3) {
                  c.setForeground(Constants.WARNING_COLOR);
                  c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
              } catch (NumberFormatException e) {
                // Ignore
              }
            }
          }
        }

        if (isSelected) {
          c.setBackground(Constants.TABLE_SELECTION_BG);
          c.setForeground(Constants.TABLE_SELECTION_FG);
        }

        return c;
      }
    });

    updateData(books);
  }

  public void updateData(List<Book> books) {
    model.setRowCount(0);
    for (Book book : books) {
      Object[] row = {
          book.getIsbn(),
          book.getTitle(),
          book.getAuthor(),
          book.getGenre(),
          book.isIssued() ? "Issued" : "Available",
          book.isIssued() ? book.getIssuedTo() : "-",
          book.isIssued() ? DateUtils.formatDueDate(book.getDueDate()) : "-",
          book.isIssued() ? String.valueOf(book.getDaysUntilDue()) : "-"
      };
      model.addRow(row);
    }
  }

  public Book getSelectedBook() {
    int row = getSelectedRow();
    if (row == -1)
      return null;

    int modelRow = convertRowIndexToModel(row);
    String isbn = (String) model.getValueAt(modelRow, 0);

    // Get the actual book from service to ensure we have all data
    String title = (String) model.getValueAt(modelRow, 1);
    String author = (String) model.getValueAt(modelRow, 2);
    String genre = (String) model.getValueAt(modelRow, 3);

    Book book = new Book(isbn, title, author, genre);

    String status = (String) model.getValueAt(modelRow, 4);
    if ("Issued".equals(status)) {
      book.setIssued(true);
      book.setIssuedTo((String) model.getValueAt(modelRow, 5));
    }

    return book;
  }
}