package models;

public enum UserRole {
  ADMIN, // Full access - can manage users, books, everything
  LIBRARIAN, // Can manage books, issue/return books
  MEMBER // Can view books, request issues, return their own books
}