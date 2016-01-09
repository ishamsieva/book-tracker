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
import java.time.Period;
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
    public JSONObject retrieveBooks2() {
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
        booksGridJson.put("allDays", getDayData(userStartDate));

        return booksGridJson;
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
        booksGridJson.put("allDays", getDayData(userStartDate));


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

    private List<String> getDayData(String userStartDay) {

        LocalDate userStartDate = LocalDate.parse(userStartDay, formatter);
        LocalDate firstDayofCurrentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayofCurrentMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        LocalDate runningDate = (userStartDate.isBefore(firstDayofCurrentMonth))?
                userStartDate : firstDayofCurrentMonth;

        List<String> mddList = new LinkedList<>();

        while(!runningDate.isAfter(lastDayofCurrentMonth)) {

            mddList.add(String.valueOf(runningDate.getDayOfMonth()));
            runningDate = runningDate.plusDays(1);
        }

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


        LocalDate userStartDate = LocalDate.parse(userStartDay, formatter);
        LocalDate firstDayofCurrentMonth = LocalDate.now().withDayOfMonth(1);

        LocalDate earliestStartDate = (userStartDate.isBefore(firstDayofCurrentMonth))?
                userStartDate : firstDayofCurrentMonth;

        return getFillInDaysList(list, earliestStartDate);

    }

    private List<Day> getFillInDaysList(LinkedList<Day> days, LocalDate from) {

        days.sort((o1, o2) -> {
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

        LocalDate firstReadingDay = LocalDate.parse(days.getFirst().getDate(), formatter);
        LinkedList<Day> fillInDays = new LinkedList<>();

        LocalDate runningDayRead = from;

        while (!runningDayRead.isEqual(firstReadingDay)) {

            Day day = new Day();
            day.setDate(runningDayRead.format(formatter));
            day.setType("NOT_READING");
            fillInDays.addFirst(day);
            runningDayRead = runningDayRead.plusDays(1);
        }

        LocalDate lastDayofMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        fillInDays.add(days.getFirst());

        runningDayRead = firstReadingDay;

        for (int i = 1; i < days.size(); i++) {
            LocalDate day = LocalDate.parse(days.get(i).getDate(), formatter);
            int daysMissed = Period.between(runningDayRead, day).getDays();
            for (int y = 1; y < daysMissed; y++) {
                Day dayMissed = new Day();
                dayMissed.setDate(runningDayRead.plusDays(y).format(formatter));
                dayMissed.setType("NOT_READING");
                fillInDays.addLast(dayMissed);
            }
            fillInDays.add(days.get(i));
            runningDayRead = day;
        }

        while(!runningDayRead.isEqual(lastDayofMonth)) {
            runningDayRead = runningDayRead.plusDays(1);
            String dayLast = runningDayRead.format(formatter);
            Day day = new Day();
            day.setDate(dayLast);
            day.setType("NOT_READING");
            fillInDays.addLast(day);
        }

        for (Day d : fillInDays) {
            LocalDate ld = LocalDate.parse(d.getDate(), formatter);
            LocalDate now = LocalDate.now();
            if (ld.isAfter(now)) {
                d.setType("NOT_READING_FUT");
            }
            if (ld.isEqual(now)) {
                d.setType("NOW_" + d.getType());
            }
        }

        return fillInDays;
    }


}
