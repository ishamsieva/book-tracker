package org.iliza.booktracker.persist;

import org.iliza.booktracker.model.Book;
import org.iliza.booktracker.service.BookService;
import org.iliza.booktracker.service.BookServiceImpl;
import org.junit.Test;

/**
 * Created by ishamsieva on 27/12/2015.
 */
public class BookPersistTest {

    @Test
    public void testPersist() {
        BookService bp = new BookServiceImpl();
        Book book  = new Book();
        book.setName("Gradle In Action - 1");
        bp.persistBook(book);

        Book book2  = new Book();
        book2.setName("Gradle In Action - 2");
        bp.persistBook(book2);

        //String json = bp.retrieveBooks();

        //bp.purgeBooks();
    }

    @Test
    public void testUpdate() {
        BookService bp = new BookServiceImpl();

        bp.addDate("Gradle In Action - 1", "2015-12-29");
    }

    @Test
    public void testPersist3rd() {
        BookService bp = new BookServiceImpl();
        Book book  = new Book();
        book.setName("Gradle In Action - 3");
        bp.persistBook(book);
    }

    @Test
    public void testRetrieve() {
        BookService bp = new BookServiceImpl();
        String allBooks = bp.retrieveBooks();
    }

    @Test
    public void testStartDayOfTheBook() {
        BookPersist bp = new BookPersistImpl();
        String startReadingDate = bp.getStartDateOfBook("Gradle In Action - 2");
    }

    @Test
    public void testDeleteBook() {
        BookService bs = new BookServiceImpl();
        bs.deleteBook("new one");
    }

}
