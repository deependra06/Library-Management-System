package ui;

import services.LibraryService;
import utils.Constants;
import javax.swing.*;
import java.awt.*;

public class AddBookPanel extends JPanel {
  private LibraryService libraryService;
  private MainFrame mainFrame;
  private JTextField isbnField, titleField, authorField, genreField;

  public AddBookPanel(LibraryService libraryService, MainFrame mainFrame) {
    this.libraryService = libraryService;
    this.mainFrame = mainFrame;
    initializeUI();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(15, 15));
    setBackground(Constants.LIGHT_BG);
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    add(createHeader(), BorderLayout.NORTH);
    add(createFormPanel(), BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel createHeader() {
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(Constants.LIGHT_BG);

    JLabel title = new JLabel("➕ Add New Book", JLabel.LEFT);
    title.setFont(Constants.HEADER_FONT);
    title.setForeground(Constants.PRIMARY_COLOR);

    JLabel subtitle = new JLabel("Fill in the details to add a new book to the library");
    subtitle.setFont(Constants.NORMAL_FONT);
    subtitle.setForeground(Constants.TEXT_SECONDARY);

    JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
    titlePanel.setBackground(Constants.LIGHT_BG);
    titlePanel.add(title, BorderLayout.NORTH);
    titlePanel.add(subtitle, BorderLayout.SOUTH);

    header.add(titlePanel, BorderLayout.CENTER);
    return header;
  }

  private JPanel createFormPanel() {
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(Constants.CARD_BG);
    formPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
        BorderFactory.createEmptyBorder(30, 40, 30, 40)
    ));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(12, 10, 12, 10);

    // ISBN
    gbc.gridx = 0; gbc.gridy = 0;
    gbc.weightx = 0.3;
    formPanel.add(createLabel("ISBN:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.7;
    isbnField = createStyledTextField();
    formPanel.add(isbnField, gbc);

    // Title
    gbc.gridx = 0; gbc.gridy = 1;
    gbc.weightx = 0.3;
    formPanel.add(createLabel("Title:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.7;
    titleField = createStyledTextField();
    formPanel.add(titleField, gbc);

    // Author
    gbc.gridx = 0; gbc.gridy = 2;
    gbc.weightx = 0.3;
    formPanel.add(createLabel("Author:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.7;
    authorField = createStyledTextField();
    formPanel.add(authorField, gbc);

    // Genre
    gbc.gridx = 0; gbc.gridy = 3;
    gbc.weightx = 0.3;
    formPanel.add(createLabel("Genre:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.7;
    genreField = createStyledTextField();
    formPanel.add(genreField, gbc);

    return formPanel;
  }

  private JLabel createLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Segoe UI", Font.BOLD, 14));
    label.setForeground(Constants.TEXT_PRIMARY);
    return label;
  }

  private JTextField createStyledTextField() {
    JTextField field = new JTextField(25);
    field.setFont(Constants.NORMAL_FONT);
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
        BorderFactory.createEmptyBorder(10, 12, 10, 12)
    ));
    
    // Add focus effect
    field.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(9, 11, 9, 11)
        ));
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
      }
    });
    
    return field;
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    buttonPanel.setBackground(Constants.LIGHT_BG);

    JButton addButton = createModernButton("✅ Add Book", Constants.BTN_SUCCESS);
    JButton clearButton = createModernButton("🗑️ Clear Form", Constants.BTN_SECONDARY);

    addButton.addActionListener(e -> addBook());
    clearButton.addActionListener(e -> clearForm());

    buttonPanel.add(addButton);
    buttonPanel.add(clearButton);
    return buttonPanel;
  }

  private JButton createModernButton(String text, Color color) {
    JButton button = new JButton(text);
    button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    button.setBackground(color);
    button.setForeground(Constants.BTN_TEXT_COLOR);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color.darker(), 1, true),
        BorderFactory.createEmptyBorder(12, 30, 12, 30)
    ));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    button.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        if (button.isEnabled()) {
          button.setBackground(color.darker());
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

  private void addBook() {
    String isbn = isbnField.getText().trim();
    String title = titleField.getText().trim();
    String author = authorField.getText().trim();
    String genre = genreField.getText().trim();

    if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
      showMessage("Please fill all fields!", "Input Required", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (libraryService.addBook(isbn, title, author, genre)) {
      showMessage("Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
      clearForm();
      mainFrame.refreshDashboard();
    } else {
      showMessage("Book with this ISBN already exists!", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void clearForm() {
    isbnField.setText("");
    titleField.setText("");
    authorField.setText("");
    genreField.setText("");
    isbnField.requestFocus();
  }

  private void showMessage(String message, String title, int type) {
    JOptionPane.showMessageDialog(this, message, title, type);
  }
}