package models;

import java.io.Serializable;

public class User implements Serializable {
  private String username;
  private String password;
  private String email;
  private UserRole role;

  public User(String username, String password, String email, UserRole role) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
  }

  // Getters and setters
  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public UserRole getRole() {
    return role;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean validatePassword(String password) {
    return this.password.equals(password);
  }

  @Override
  public String toString() {
    return username + " (" + role + ")";
  }
}