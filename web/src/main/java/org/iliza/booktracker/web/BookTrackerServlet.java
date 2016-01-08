package org.iliza.booktracker.web;

import org.iliza.booktracker.model.Book;
import org.iliza.booktracker.service.BookService;
import org.iliza.booktracker.service.BookServiceImpl;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ishamsieva on 27/12/2015.
 */

/**
 * Created by ishamsieva on 21/12/2015.
 */
public class BookTrackerServlet extends HttpServlet {

    private BookService bookService = new BookServiceImpl();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String query = request.getServletPath();

        if ("/all".equals(query)) {
            //String result = bookService.retrieveBooks();

            //response.getWriter().write(result);

            JSONObject res = bookService.retrieveBooks2();
            response.getWriter().print(res);
            response.getWriter().flush();
        } else if ("/addBook".equals(query)){
            String bookName = request.getParameter("book");
            Book book = new Book();
            book.setName(bookName);
            String result = bookService.persistBook(book);
            response.getWriter().write(result);
        } else if ("/addReading".equals(query)) {
            String bookName = request.getParameter("book");
            String date = request.getParameter("date");
            String result = bookService.addReadingDay(bookName, date);
            response.getWriter().write(result);
        } else if ("/finishedReading".equals(query)) {
            String bookName = request.getParameter("book");
            String date = request.getParameter("date");
            String result = bookService.addFinishDay(bookName, date);
            response.getWriter().write(result);
        } else if ("/deleteBook".equals(query)) {
            String bookName = request.getParameter("book");
            bookService.deleteBook(bookName);

        }

    }

}

