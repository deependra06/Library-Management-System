package models;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Book implements Serializable {
  private String isbn;
  private String title;
  private String author;
  private String genre;
  private boolean isIssued;
  private Date issueDate;
  private Date dueDate;
  private String issuedTo;

  public Book(String isbn, String title, String author, String genre) {
    this.isbn = isbn;
    this.title = title;
    this.author = author;
    this.genre = genre;
    this.isIssued = false;
  }

  // Getters
  public String getIsbn() {
    return isbn;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public String getGenre() {
    return genre;
  }

  public boolean isIssued() {
    return isIssued;
  }

  public Date getIssueDate() {
    return issueDate;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public String getIssuedTo() {
    return issuedTo;
  }

  // Setters
  public void setIssued(boolean issued) {
    isIssued = issued;
  }

  public void setIssueDate(Date issueDate) {
    this.issueDate = issueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public void setIssuedTo(String issuedTo) {
    this.issuedTo = issuedTo;
  }

  public long getDaysUntilDue() {
    if (dueDate == null)
      return -1;
    long diff = dueDate.getTime() - System.currentTimeMillis();
    return TimeUnit.MILLISECONDS.toDays(diff);
  }

  @Override
  public String toString() {
    return String.format("%s by %s (ISBN: %s)", title, author, isbn);
  }
}