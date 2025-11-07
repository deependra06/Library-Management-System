package dao;

import models.Library;
import java.io.*;

public class FileHandlerDAO {
  private static final String FILE_PATH = "data/books.dat";

  public void saveLibrary(Library library) {
    try (ObjectOutputStream out = new ObjectOutputStream(
        new FileOutputStream(FILE_PATH))) {
      new File(FILE_PATH).getParentFile().mkdirs();
      out.writeObject(library);
    } catch (IOException e) {
      System.err.println("Error saving library: " + e.getMessage());
    }
  }

  public Library loadLibrary() {
    File file = new File(FILE_PATH);
    if (!file.exists())
      return new Library();

    try (ObjectInputStream in = new ObjectInputStream(
        new FileInputStream(file))) {
      return (Library) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error loading library: " + e.getMessage());
      return new Library();
    }
  }
}