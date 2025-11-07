package services;

import models.BookRequest;
import models.RequestStatus;
import java.util.*;
import java.util.stream.Collectors;

public class BookRequestService {
    private List<BookRequest> requests;
    private LibraryService libraryService;

    public BookRequestService(LibraryService libraryService) {
        this.requests = new ArrayList<>();
        this.libraryService = libraryService;
    }

    public BookRequest requestBook(String isbn, String username) {
        // Check if book exists and is available
        if (libraryService.findBookByIsbn(isbn) == null) {
            return null;
        }
        
        // Check if user already has a pending request for this book
        boolean hasPendingRequest = requests.stream()
            .anyMatch(req -> req.getIsbn().equals(isbn) && 
                           req.getUsername().equals(username) && 
                           req.getStatus() == RequestStatus.PENDING);
        
        if (hasPendingRequest) {
            return null;
        }

        BookRequest request = new BookRequest(isbn, username);
        requests.add(request);
        return request;
    }

    public boolean approveRequest(String requestId, int days) {
        BookRequest request = findRequestById(requestId);
        if (request != null && request.getStatus() == RequestStatus.PENDING) {
            // Issue the book
            boolean issued = libraryService.issueBook(
                request.getIsbn(), request.getUsername(), days);
            
            if (issued) {
                request.setStatus(RequestStatus.APPROVED);
                return true;
            }
        }
        return false;
    }

    public boolean rejectRequest(String requestId) {
        BookRequest request = findRequestById(requestId);
        if (request != null && request.getStatus() == RequestStatus.PENDING) {
            request.setStatus(RequestStatus.REJECTED);
            return true;
        }
        return false;
    }

    public List<BookRequest> getPendingRequests() {
        return requests.stream()
            .filter(req -> req.getStatus() == RequestStatus.PENDING)
            .collect(Collectors.toList());
    }

    public List<BookRequest> getUserRequests(String username) {
        return requests.stream()
            .filter(req -> req.getUsername().equals(username))
            .collect(Collectors.toList());
    }

    private BookRequest findRequestById(String requestId) {
        return requests.stream()
            .filter(req -> req.getRequestId().equals(requestId))
            .findFirst()
            .orElse(null);
    }
}