package dao;

import models.Book;
import models.Library;
import java.util.List;

public class BookDAO {
    private Library library;
    private FileHandlerDAO fileHandler;
    
    public BookDAO() {
        this.fileHandler = new FileHandlerDAO();
        this.library = fileHandler.loadLibrary();
    }
    
    public void addBook(Book book) {
        library.addBook(book);
        fileHandler.saveLibrary(library);
    }
    
    public boolean removeBook(String isbn) {
        boolean removed = library.removeBook(isbn);
        if (removed) fileHandler.saveLibrary(library);
        return removed;
    }
    
    public void updateBook(Book book) {
        removeBook(book.getIsbn());
        addBook(book);
    }
    
    public List<Book> getAllBooks() { return library.getAllBooks(); }
    public List<Book> getAvailableBooks() { return library.getAvailableBooks(); }
    public List<Book> getIssuedBooks() { return library.getIssuedBooks(); }
    public List<Book> searchBooks(String keyword) { return library.searchBooks(keyword); }
    public Book findBookByIsbn(String isbn) { return library.findBookByIsbn(isbn); }
    
    public int getTotalBooks() { return library.getTotalBooks(); }
    public int getAvailableBooksCount() { return library.getAvailableBooksCount(); }
    public int getIssuedBooksCount() { return library.getIssuedBooksCount(); }
}