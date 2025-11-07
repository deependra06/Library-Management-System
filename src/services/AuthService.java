package services;

import dao.UserDAO;
import models.User;
import models.UserRole;

public class AuthService {
  private UserDAO userDAO;
  private User currentUser;

  public AuthService() {
    this.userDAO = new UserDAO();
  }

  public boolean login(String username, String password) {
    User user = userDAO.authenticate(username, password);
    if (user != null) {
      this.currentUser = user;
      return true;
    }
    return false;
  }

  public boolean register(String username, String password, String email, UserRole role) {
    return userDAO.registerUser(username, password, email, role);
  }

  public void logout() {
    this.currentUser = null;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public boolean isLoggedIn() {
    return currentUser != null;
  }

  // Admin has full access
  public boolean isAdmin() {
    return isLoggedIn() && currentUser.getRole() == UserRole.ADMIN;
  }

  // Librarian can manage books and requests
  public boolean isLibrarian() {
    return isLoggedIn() && currentUser.getRole() == UserRole.LIBRARIAN;
  }

  // Member has limited access
  public boolean isMember() {
    return isLoggedIn() && currentUser.getRole() == UserRole.MEMBER;
  }

  // Admin and Librarian can manage books
  public boolean canManageBooks() {
    return isAdmin() || isLibrarian();
  }

  // Only Admin can manage users
  public boolean canManageUsers() {
    return isAdmin();
  }

  // Librarian and Admin can process book requests
  public boolean canProcessRequests() {
    return isAdmin() || isLibrarian();
  }
}