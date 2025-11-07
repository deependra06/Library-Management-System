package services;

import models.Book;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BookIssueService {

  public static boolean canIssueBook(Book book) {
    return book != null && !book.isIssued();
  }

  public static Date calculateDueDate(int days) {
    return new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days));
  }

  public static boolean isBookOverdue(Book book) {
    return book.isIssued() && book.getDueDate() != null &&
        book.getDueDate().before(new Date());
  }
}