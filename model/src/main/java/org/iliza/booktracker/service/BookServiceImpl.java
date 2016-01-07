package org.iliza.booktracker.service;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.iliza.booktracker.model.Book;
import org.iliza.booktracker.model.Day;
import org.iliza.booktracker.model.MonthDayData;
import org.iliza.booktracker.persist.BookPersist;
import org.iliza.booktracker.persist.BookPersistImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ishamsieva on 27/12/2015.
 */
public class BookServiceImpl implements BookService {

    BookPersist bookPersist = new BookPersistImpl();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void addDate(String bookName, String date) {
        bookPersist.addDate(bookName, date);
    }

    @Override
    public void purgeBooks() {
        bookPersist.purgeBooks();
    }

    @Override
    public void deleteBook(String bookName) {
        bookPersist.deleteBook(bookName);
    }

    @Override
    public String persistBook(Book book) {
        bookPersist.persistBook(book);

        return retrieveBooks();
    }

    @Override
    public String addFinishDay(String bookName, String finishDay) {
        return retrieveBooks();
    }

    @Override
    public String addReadingDay(String bookName, String readingDay) {

        String startReadingDate = bookPersist.getStartDateOfBook(bookName);

        LocalDate startReadingDateLD = LocalDate.parse(startReadingDate, formatter);
        LocalDate readingDateLD = LocalDate.parse(readingDay, formatter);

        if (readingDateLD.isAfter(startReadingDateLD)) {
            bookPersist.addDate(bookName, readingDay);
        }

        return retrieveBooks();
    }

    @Override
    public String retrieveBooks() {

        FindIterable<Document> iterable = bookPersist.retrieveBooks();

        JSONArray values = new JSONArray();

        String userStartDate = null;

        if (iterable.iterator().hasNext()) {
            Document userDoc = iterable.iterator().next();
            userStartDate = userDoc.getString("startDate");
            List<Document> books = (List<Document>)userDoc.get("books");
            for (Document bookDoc : books) {
                JSONObject bookJson = new JSONObject();
                bookJson.put("name", bookDoc.getString("name"));
                bookJson.put("startReading", bookDoc.getString("startReading"));
                bookJson.put("days", getDays(bookDoc, userStartDate));

                values.put(bookJson);
            }
        }

        JSONObject booksGridJson = new JSONObject();
        booksGridJson.put("books", values);
        booksGridJson.put("user", "iliza");
        booksGridJson.put("monthDays", getMonthDayData(userStartDate));


        return booksGridJson.toString();
    }

    private List<MonthDayData> getMonthDayData(String userStartDay) {

        LocalDate userStartDate = LocalDate.parse(userStartDay, formatter);
        LocalDate firstDayofCurrentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayofCurrentMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        LocalDate runningDate = (userStartDate.isBefore(firstDayofCurrentMonth))?
                userStartDate : firstDayofCurrentMonth;

        List<MonthDayData> mddList = new LinkedList<>();
        MonthDayData mdd = new MonthDayData();

        while(!runningDate.isAfter(lastDayofCurrentMonth)) {
            String month = runningDate.getMonth().toString();
            if (!month.equals(mdd.getMonth())) {
                if (mdd.getMonth() != null) {
                    mddList.add(mdd);
                }
                mdd = new MonthDayData();
                mdd.setMonth(month);
            }
            mdd.addDay(runningDate.getDayOfMonth());
            runningDate = runningDate.plusDays(1);
        }
        mddList.add(mdd);

        return mddList;
    }

    private List<Day> getDays(Document doc, String userStartDay) {
        List<Document> docs = (List<Document>)doc.get("days");
        LinkedList<Day> list = new LinkedList<>();
        for(Document d : docs){
            String date = d.getString("date");
            String type = d.getString("type");
            Day day = new Day();
            day.setDate(date);
            day.setType(type);
            list.add(day);
        }

        list.sort((o1, o2) -> {
            LocalDate o1Date = LocalDate.parse(o1.getDate(), formatter);
            LocalDate o2Date = LocalDate.parse(o2.getDate(), formatter);

            if (o1Date.isBefore(o2Date)) {
                return -1;
            }else if (o1Date.isAfter(o2Date)) {
                return 1;
            }else {
                return 0;
            }
        });

        LocalDate userStartDate = LocalDate.parse(userStartDay, formatter);
        LocalDate firstDayofCurrentMonth = LocalDate.now().withDayOfMonth(1);

        LocalDate firstReadingDay = LocalDate.parse(list.getFirst().getDate(), formatter);

        LocalDate earliestStartDate = (userStartDate.isBefore(firstDayofCurrentMonth))?
                userStartDate : firstDayofCurrentMonth;


        while (!earliestStartDate.isEqual(firstReadingDay)) {
            firstReadingDay = firstReadingDay.minusDays(1);
            Day day = new Day();
            day.setDate(firstReadingDay.format(formatter));
            day.setType("NOT_READING");
            list.addFirst(day);
        }

        LocalDate lastDayofMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        LocalDate lastDayRead = LocalDate.parse(list.getLast().getDate(), formatter);

        while(!lastDayRead.isEqual(lastDayofMonth)) {
            lastDayRead = lastDayRead.plusDays(1);
            String dayLast = lastDayRead.format(formatter);
            Day day = new Day();
            day.setDate(dayLast);
            day.setType("NOT_READING");
            list.addLast(day);
        }

        return list;
    }
}
