package utils;

import java.awt.*;

public class Constants {
  public static final String APP_NAME = "Library Management System";
  public static final String DATA_FILE = "data/books.dat";
  public static final String USER_FILE = "data/users.dat";
  public static final String REQUESTS_FILE = "data/requests.dat";

  // UI Constants
  public static final Dimension WINDOW_SIZE = new Dimension(1200, 800);
  public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
  public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
  public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
  public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

  // Modern Vibrant Color Palette - Eye-catching and Professional
  public static final Color PRIMARY_COLOR = new Color(59, 130, 246);       // Bright Blue
  public static final Color SECONDARY_COLOR = new Color(139, 92, 246);     // Purple-500
  public static final Color SUCCESS_COLOR = new Color(16, 185, 129);       // Emerald-500
  public static final Color WARNING_COLOR = new Color(251, 146, 60);       // Orange-500
  public static final Color DANGER_COLOR = new Color(239, 68, 68);         // Red-500
  public static final Color INFO_COLOR = new Color(6, 182, 212);           // Cyan-500

  // Background Colors - Clean and Modern
  public static final Color LIGHT_BG = new Color(249, 250, 251);           // Gray-50
  public static final Color CARD_BG = new Color(255, 255, 255);            // Pure White
  public static final Color BORDER_COLOR = new Color(229, 231, 235);       // Gray-200
  
  // Text Colors - High Contrast
  public static final Color TEXT_PRIMARY = new Color(17, 24, 39);          // Gray-900
  public static final Color TEXT_SECONDARY = new Color(107, 114, 128);     // Gray-500

  // Button Colors - Vibrant Set
  public static final Color BTN_PRIMARY = new Color(59, 130, 246);         // Blue-500
  public static final Color BTN_SUCCESS = new Color(16, 185, 129);         // Emerald-500
  public static final Color BTN_WARNING = new Color(251, 146, 60);         // Orange-500
  public static final Color BTN_DANGER = new Color(239, 68, 68);           // Red-500
  public static final Color BTN_SECONDARY = new Color(100, 116, 139);      // Slate-500
  public static final Color BTN_DISABLED = new Color(209, 213, 219);       // Gray-300
  public static final Color BTN_TEXT_COLOR = Color.WHITE;

  // Button Hover States - Darker and More Saturated
  public static final Color BTN_HOVER_PRIMARY = new Color(37, 99, 235);    // Blue-600
  public static final Color BTN_HOVER_SUCCESS = new Color(5, 150, 105);    // Emerald-600
  public static final Color BTN_HOVER_WARNING = new Color(234, 88, 12);    // Orange-600
  public static final Color BTN_HOVER_DANGER = new Color(220, 38, 38);     // Red-600

  // Enhanced Table Colors - Rich Header with Perfect Contrast
  public static final Color TABLE_HEADER_BG = new Color(37, 99, 235);      // Bright Blue-600 (Same as primary)
  public static final Color TABLE_HEADER_BG_GRADIENT = new Color(29, 78, 216); // Blue-700 for gradient
  public static final Color TABLE_HEADER_FG = new Color(255, 255, 255);    // Pure White
  public static final Color TABLE_HEADER_BORDER = new Color(59, 130, 246); // Bright Blue-500
  
  public static final Color TABLE_SELECTION_BG = new Color(147, 197, 253); // Blue-300 (Lighter)
  public static final Color TABLE_SELECTION_FG = new Color(30, 58, 138);   // Blue-900
  public static final Color TABLE_ROW_EVEN = new Color(255, 255, 255);     // White
  public static final Color TABLE_ROW_ODD = new Color(249, 250, 251);      // Gray-50
  public static final Color TABLE_HOVER = new Color(243, 244, 246);        // Gray-100

  // Status Colors - High Visibility and Vibrant
  public static final Color STATUS_AVAILABLE = new Color(5, 150, 105);     // Emerald-600
  public static final Color STATUS_BORROWED = new Color(220, 38, 38);      // Red-600
  public static final Color STATUS_OVERDUE = new Color(153, 27, 27);       // Red-900
  public static final Color STATUS_PENDING = new Color(217, 119, 6);       // Amber-600
  
  // Accent Colors for Special Elements
  public static final Color ACCENT_GOLD = new Color(245, 158, 11);         // Amber-500
  public static final Color ACCENT_PURPLE = new Color(168, 85, 247);       // Purple-500
  public static final Color ACCENT_PINK = new Color(236, 72, 153);         // Pink-500
  public static final Color ACCENT_TEAL = new Color(20, 184, 166);         // Teal-500
  
  // Shadow and Depth
  public static final Color SHADOW_COLOR = new Color(0, 0, 0, 25);
  public static final Color SHADOW_LIGHT = new Color(0, 0, 0, 10);
}