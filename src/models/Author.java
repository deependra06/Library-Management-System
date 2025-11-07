package models;

import java.io.Serializable;

public class Author implements Serializable {
  private String name;
  private String email;

  public Author(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return name;
  }
}