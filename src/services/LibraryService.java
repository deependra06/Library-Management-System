package services;

import dao.BookDAO;
import models.Book;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LibraryService {
  private BookDAO bookDAO;

  public LibraryService() {
    this.bookDAO = new BookDAO();
  }

  public boolean addBook(String isbn, String title, String author, String genre) {
    if (bookDAO.findBookByIsbn(isbn) != null)
      return false;
    bookDAO.addBook(new Book(isbn, title, author, genre));
    return true;
  }

  public boolean removeBook(String isbn) {
    return bookDAO.removeBook(isbn);
  }

  public boolean issueBook(String isbn, String issuedTo, int days) {
    Book book = bookDAO.findBookByIsbn(isbn);
    if (book == null || book.isIssued())
      return false;

    book.setIssued(true);
    book.setIssuedTo(issuedTo);
    book.setIssueDate(new Date());
    book.setDueDate(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days)));

    bookDAO.updateBook(book);
    return true;
  }

  public boolean returnBook(String isbn) {
    Book book = bookDAO.findBookByIsbn(isbn);
    if (book == null || !book.isIssued())
      return false;

    book.setIssued(false);
    book.setIssuedTo(null);
    book.setIssueDate(null);
    book.setDueDate(null);

    bookDAO.updateBook(book);
    return true;
  }

  public boolean returnMyBook(String isbn, String username) {
    Book book = bookDAO.findBookByIsbn(isbn);
    if (book == null || !book.isIssued() || !book.getIssuedTo().equals(username))
      return false;

    return returnBook(isbn);
  }

  public long getTimeLeft(String isbn) {
    Book book = bookDAO.findBookByIsbn(isbn);
    if (book == null || !book.isIssued() || book.getDueDate() == null)
      return -1;
    return TimeUnit.MILLISECONDS.toDays(book.getDueDate().getTime() - System.currentTimeMillis());
  }

  // Delegated methods
  public List<Book> getAllBooks() {
    return bookDAO.getAllBooks();
  }

  public List<Book> getAvailableBooks() {
    return bookDAO.getAvailableBooks();
  }

  public List<Book> getIssuedBooks() {
    return bookDAO.getIssuedBooks();
  }

  public List<Book> searchBooks(String keyword) {
    return bookDAO.searchBooks(keyword);
  }

  public Book findBookByIsbn(String isbn) {
    return bookDAO.findBookByIsbn(isbn);
  }

  public int getTotalBooks() {
    return bookDAO.getTotalBooks();
  }

  public int getAvailableBooksCount() {
    return bookDAO.getAvailableBooksCount();
  }

  public int getIssuedBooksCount() {
    return bookDAO.getIssuedBooksCount();
  }
}