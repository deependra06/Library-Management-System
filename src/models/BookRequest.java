package models;

import java.io.Serializable;
import java.util.Date;

public class BookRequest implements Serializable {
  private String requestId;
  private String isbn;
  private String username;
  private Date requestDate;
  private RequestStatus status;

  public BookRequest(String isbn, String username) {
    this.requestId = "REQ_" + System.currentTimeMillis();
    this.isbn = isbn;
    this.username = username;
    this.requestDate = new Date();
    this.status = RequestStatus.PENDING;
  }

  // Getters and Setters
  public String getRequestId() {
    return requestId;
  }

  public String getIsbn() {
    return isbn;
  }

  public String getUsername() {
    return username;
  }

  public Date getRequestDate() {
    return requestDate;
  }

  public RequestStatus getStatus() {
    return status;
  }

  public void setStatus(RequestStatus status) {
    this.status = status;
  }
}