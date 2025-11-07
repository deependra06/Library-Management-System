package ui.components;

import utils.Constants;
import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {
  public StatCard(String title, String value, Color color) {
    setLayout(new BorderLayout());
    setBackground(Constants.CARD_BG);
    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(color, 3),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)));

    JLabel titleLabel = new JLabel(title, JLabel.CENTER);
    titleLabel.setFont(Constants.NORMAL_FONT);
    titleLabel.setForeground(Constants.TEXT_PRIMARY);

    JLabel valueLabel = new JLabel(value, JLabel.CENTER);
    valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
    valueLabel.setForeground(color);

    add(titleLabel, BorderLayout.NORTH);
    add(valueLabel, BorderLayout.CENTER);
  }
}