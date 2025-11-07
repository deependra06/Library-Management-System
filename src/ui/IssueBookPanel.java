package ui;

import services.LibraryService;
import services.AuthService;
import utils.Constants;
import javax.swing.*;
import java.awt.*;

public class IssueBookPanel extends JPanel {
  private LibraryService libraryService;
  private AuthService authService;
  private MainFrame mainFrame;
  private JTextField isbnField, userField, daysField;

  public IssueBookPanel(LibraryService libraryService, AuthService authService, MainFrame mainFrame) {
    this.libraryService = libraryService;
    this.authService = authService;
    this.mainFrame = mainFrame;
    initializeUI();
  }

  private void initializeUI() {
    setLayout(new BorderLayout());
    setBackground(Constants.LIGHT_BG);

    add(createHeader(), BorderLayout.NORTH);
    add(createFormPanel(), BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel createHeader() {
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(Constants.LIGHT_BG);

    JLabel title = new JLabel("Issue Book", JLabel.CENTER);
    title.setFont(Constants.HEADER_FONT);
    title.setForeground(Constants.PRIMARY_COLOR);
    title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

    // Role information
    JLabel roleLabel = new JLabel("Role: " + authService.getCurrentUser().getRole());
    roleLabel.setFont(Constants.SMALL_FONT);
    roleLabel.setForeground(Constants.TEXT_SECONDARY);
    roleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

    header.add(title, BorderLayout.CENTER);
    header.add(roleLabel, BorderLayout.EAST);

    return header;
  }

  private JPanel createFormPanel() {
    JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
    formPanel.setBackground(Constants.LIGHT_BG);

    isbnField = new JTextField();
    userField = new JTextField();
    daysField = new JTextField("14"); // Default 14 days

    // Add labels and fields
    formPanel.add(createFormLabel("Book ISBN:"));
    formPanel.add(isbnField);
    formPanel.add(createFormLabel("Issued To:"));
    formPanel.add(userField);
    formPanel.add(createFormLabel("Days:"));
    formPanel.add(daysField);

    // Add quick action buttons for common durations
    JPanel quickDaysPanel = new JPanel(new FlowLayout());
    quickDaysPanel.setBackground(Constants.LIGHT_BG);
    JButton weekBtn = createSmallButton("7 days", Constants.BTN_SECONDARY);
    JButton twoWeeksBtn = createSmallButton("14 days", Constants.BTN_PRIMARY);
    JButton monthBtn = createSmallButton("30 days", Constants.BTN_SUCCESS);

    weekBtn.addActionListener(e -> daysField.setText("7"));
    twoWeeksBtn.addActionListener(e -> daysField.setText("14"));
    monthBtn.addActionListener(e -> daysField.setText("30"));

    quickDaysPanel.add(weekBtn);
    quickDaysPanel.add(twoWeeksBtn);
    quickDaysPanel.add(monthBtn);

    formPanel.add(createFormLabel("Quick Settings:"));
    formPanel.add(quickDaysPanel);

    return formPanel;
  }

  private JLabel createFormLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(Constants.NORMAL_FONT);
    label.setForeground(Constants.TEXT_PRIMARY);
    return label;
  }

  private JButton createSmallButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setFont(Constants.SMALL_FONT);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    return button;
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setBackground(Constants.LIGHT_BG);

    JButton issueButton = createStyledButton("📚 Issue Book", Constants.BTN_SUCCESS);
    JButton checkTimeButton = createStyledButton("⏰ Check Time Left", Constants.BTN_PRIMARY);
    JButton clearButton = createStyledButton("🗑️ Clear Form", Constants.BTN_SECONDARY);

    issueButton.addActionListener(e -> issueBook());
    checkTimeButton.addActionListener(e -> checkTimeLeft());
    clearButton.addActionListener(e -> clearForm());

    buttonPanel.add(issueButton);
    buttonPanel.add(checkTimeButton);
    buttonPanel.add(clearButton);

    return buttonPanel;
  }

  private JButton createStyledButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setFont(Constants.NORMAL_FONT);
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
  }

  private void issueBook() {
    String isbn = isbnField.getText().trim();
    String user = userField.getText().trim();
    String daysText = daysField.getText().trim();

    if (isbn.isEmpty() || user.isEmpty() || daysText.isEmpty()) {
      showError("Please fill all fields!");
      return;
    }

    try {
      int days = Integer.parseInt(daysText);
      if (days <= 0) {
        showError("Please enter a positive number of days!");
        return;
      }

      if (libraryService.issueBook(isbn, user, days)) {
        JOptionPane.showMessageDialog(this,
            "<html><b>Book issued successfully!</b><br>" +
                "Book ISBN: " + isbn + "<br>" +
                "Issued to: " + user + "<br>" +
                "Duration: " + days + " days</html>",
            "Success", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
        mainFrame.refreshDashboard();
      } else {
        showError("Failed to issue book. Book might not be available or already issued!");
      }
    } catch (NumberFormatException e) {
      showError("Please enter valid number of days!");
    }
  }

  private void checkTimeLeft() {
    String isbn = isbnField.getText().trim();
    if (isbn.isEmpty()) {
      showError("Please enter ISBN!");
      return;
    }

    long daysLeft = libraryService.getTimeLeft(isbn);
    if (daysLeft >= 0) {
      String status = daysLeft == 0 ? "Due today!" : daysLeft == 1 ? "Due tomorrow!" : daysLeft + " days left";
      JOptionPane.showMessageDialog(this,
          "<html><b>Time Left:</b> " + status + "</html>",
          "Book Status", JOptionPane.INFORMATION_MESSAGE);
    } else if (daysLeft == -1) {
      showError("Book is not issued or not found!");
    } else {
      // Book is overdue
      long overdueDays = Math.abs(daysLeft);
      JOptionPane.showMessageDialog(this,
          "<html><b style='color:red'>OVERDUE!</b><br>" +
              "Book is overdue by " + overdueDays + " day" + (overdueDays > 1 ? "s" : "") + "</html>",
          "Overdue Book", JOptionPane.WARNING_MESSAGE);
    }
  }

  private void clearForm() {
    isbnField.setText("");
    userField.setText("");
    daysField.setText("14");
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }
}