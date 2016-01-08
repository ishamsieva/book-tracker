package org.iliza.booktracker.service;

import org.iliza.booktracker.model.Book;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ishamsieva on 27/12/2015.
 */
public interface BookService {

    String persistBook(Book book);

    String retrieveBooks();

    JSONObject retrieveBooks2();

    void purgeBooks();

    void addDate(String bookName, String date);

    String addReadingDay(String bookName, String readingDay);

    String addFinishDay(String bookName, String finishDay);

    void deleteBook(String bookName);
}
