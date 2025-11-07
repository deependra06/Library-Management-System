package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library implements Serializable {
  private List<Book> books;

  public Library() {
    this.books = new ArrayList<>();
  }

  public void addBook(Book book) {
    books.add(book);
  }

  public boolean removeBook(String isbn) {
    return books.removeIf(book -> book.getIsbn().equals(isbn));
  }

  public Book findBookByIsbn(String isbn) {
    return books.stream()
        .filter(book -> book.getIsbn().equals(isbn))
        .findFirst()
        .orElse(null);
  }

  public List<Book> searchBooks(String keyword) {
    String lowerKeyword = keyword.toLowerCase();
    return books.stream()
        .filter(book -> book.getTitle().toLowerCase().contains(lowerKeyword) ||
            book.getAuthor().toLowerCase().contains(lowerKeyword) ||
            book.getIsbn().contains(keyword))
        .collect(Collectors.toList());
  }

  public List<Book> getAvailableBooks() {
    return books.stream()
        .filter(book -> !book.isIssued())
        .collect(Collectors.toList());
  }

  public List<Book> getIssuedBooks() {
    return books.stream()
        .filter(Book::isIssued)
        .collect(Collectors.toList());
  }

  public List<Book> getAllBooks() {
    return new ArrayList<>(books);
  }

  public int getTotalBooks() {
    return books.size();
  }

  public int getAvailableBooksCount() {
    return getAvailableBooks().size();
  }

  public int getIssuedBooksCount() {
    return getIssuedBooks().size();
  }
}