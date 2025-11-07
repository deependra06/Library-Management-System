package dao;

import models.User;
import models.UserRole;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
  private static final String USER_FILE = "data/users.dat";
  private List<User> users;

  public UserDAO() {
    this.users = loadUsers();
    initializeDefaultUsers();
  }

  @SuppressWarnings("unchecked")
  private List<User> loadUsers() {
    File file = new File(USER_FILE);
    if (!file.exists())
      return new ArrayList<>();

    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
      return (List<User>) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error loading users: " + e.getMessage());
      return new ArrayList<>();
    }
  }

  private void saveUsers() {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
      new File(USER_FILE).getParentFile().mkdirs();
      out.writeObject(users);
    } catch (IOException e) {
      System.err.println("Error saving users: " + e.getMessage());
    }
  }

  private void initializeDefaultUsers() {
    if (users.isEmpty()) {
      users.add(new User("admin", "admin123", "admin@library.com", UserRole.ADMIN));
      users.add(new User("librarian", "lib123", "librarian@library.com", UserRole.LIBRARIAN));
      users.add(new User("member", "member123", "member@library.com", UserRole.MEMBER));
      saveUsers();
    }
  }

  public boolean registerUser(String username, String password, String email, UserRole role) {
    if (findUserByUsername(username) != null)
      return false;

    users.add(new User(username, password, email, role));
    saveUsers();
    return true;
  }

  public User findUserByUsername(String username) {
    return users.stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst()
        .orElse(null);
  }

  public User authenticate(String username, String password) {
    User user = findUserByUsername(username);
    return (user != null && user.validatePassword(password)) ? user : null;
  }
}