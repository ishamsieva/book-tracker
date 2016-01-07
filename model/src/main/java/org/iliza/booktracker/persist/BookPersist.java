package org.iliza.booktracker.persist;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.iliza.booktracker.model.Book;

import java.util.List;

/**
 * Created by ishamsieva on 27/12/2015.
 */
public interface BookPersist {

    void persistBook(Book book);

    FindIterable<Document> retrieveBooks();

    void purgeBooks();

    void addDate(String bookName, String date);

    String getStartDateOfBook(String bookName);

    void deleteBook(String bookName);
}
