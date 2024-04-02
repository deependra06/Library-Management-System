import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Book {
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}

class Library {
    private List<Book> books;
    private List<Book> issuedBooks;

    public Library() {
        this.books = new ArrayList<>();
        this.issuedBooks = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
        issuedBooks.remove(book);
    }

    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    public List<Book> getIssuedBooks() {
        return issuedBooks;
    }

    public Book findBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    public void issueBook(Book book) {
        if (book.isAvailable()) {
            book.setAvailable(false);
            issuedBooks.add(book);
        }
    }

    public void returnBook(Book book) {
        if (!book.isAvailable()) {
            book.setAvailable(true);
            issuedBooks.remove(book);
        }
    }
}

public class LibraryManagementSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static Library library = new Library();

    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            System.out.println("Library Management System");
            System.out.println("1. Add Book"); // Option to add a book
            System.out.println("2. Remove Book"); // Option to remove a book
            System.out.println("3. Issue Book"); // Option to issue a book
            System.out.println("4. Return Book"); // Option to return a book
            System.out.println("5. Show Available Books"); // Option to show available books
            System.out.println("6. Show Issued Books"); // Option to show issued books
            System.out.println("7. Exit"); 
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    addBook(); 
                    break;
                case 2:
                    removeBook(); 
                    break;
                case 3:
                    issueBook(); 
                    break;
                case 4:
                    returnBook(); 
                    break;
                case 5:
                    showAvailableBooks(); 
                    break;
                case 6:
                    showIssuedBooks(); 
                    break;
                case 7:
                    exit = true; 
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }
    }

    private static void addBook() {
        System.out.print("Enter the title of the book: ");
        String title = scanner.nextLine();
        System.out.print("Enter the author of the book: ");
        String author = scanner.nextLine();

        Book book = new Book(title, author);
        library.addBook(book); // Add the book to the library

        System.out.println("Book added successfully!");
    }

    private static void removeBook() {
        System.out.print("Enter the title of the book to be removed: ");
        String title = scanner.nextLine();

        Book book = library.findBookByTitle(title);

        if (book != null) {
            if (book.isAvailable()) {
                library.removeBook(book); // Remove the book from the library
                System.out.println("Book removed successfully!");
            } else {
                System.out.println("Book is currently borrowed and cannot be removed.");
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    private static void issueBook() {
        System.out.print("Enter the title of the book to be issued: ");
        String title = scanner.nextLine();

        Book book = library.findBookByTitle(title);

        if (book != null) {
            if (book.isAvailable()) {
                library.issueBook(book); // Issue the book from the library
                System.out.println("Book issued successfully!");
            } else {
                System.out.println("Book is currently not available.");
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    private static void returnBook() {
        System.out.print("Enter the title of the book to be returned: ");
        String title = scanner.nextLine();

        Book book = library.findBookByTitle(title);

        if (book != null) {
            if (!book.isAvailable()) {
                library.returnBook(book); // Return the book to the library
                System.out.println("Book returned successfully!");
            } else {
                System.out.println("Book is not currently issued and cannot be returned.");
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    private static void showAvailableBooks() {
        List<Book> availableBooks = library.getAvailableBooks();

        if (availableBooks.isEmpty()) {
            System.out.println("No books available in the library.");
        } else {
            System.out.println("Available Books:");
            for (Book book : availableBooks) {
                System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor());
            }
        }
    }

    private static void showIssuedBooks() {
        List<Book> issuedBooks = library.getIssuedBooks();

        if (issuedBooks.isEmpty()) {
            System.out.println("No books issued in the library.");
        } else {
            System.out.println("Issued Books:");
            for (Book book : issuedBooks) {
                System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor());
            }
        }
    }
}
