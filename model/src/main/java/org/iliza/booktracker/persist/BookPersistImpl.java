package org.iliza.booktracker.persist;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.iliza.booktracker.model.Book;
import org.iliza.booktracker.model.Day;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by ishamsieva on 27/12/2015.
 */
public class BookPersistImpl implements BookPersist {

    @Override
    public void deleteBook(String bookName) {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase md = mongoClient.getDatabase("test");

        MongoCollection<Document> collection = md.getCollection("books");

        collection.updateOne(new Document("user", "iliza"),
                new Document("$pull", new Document("books", new Document("name", bookName))));
    }

    @Override
    public String getStartDateOfBook(String bookName) {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase md = mongoClient.getDatabase("test");

        MongoCollection<Document> collection = md.getCollection("books");



        FindIterable<Document> iterable = collection.find(new Document("user", "iliza")
                .append("books.name", bookName))
                .projection(Projections.elemMatch("books"));

        List<Document> list = (List<Document>)iterable.iterator().next().get("books");

        return list.get(0).get("startReading").toString();

    }

    @Override
    public void addDate(String bookName, String date) {


        MongoClient mongoClient = new MongoClient();
        MongoDatabase md = mongoClient.getDatabase("test");

        MongoCollection<Document> collection = md.getCollection("books");

        collection.findOneAndUpdate(new Document("user", "iliza").append("books.name", bookName),
                new Document("$addToSet",
                        new Document("books.$.days", new Document("date", date).append("type", "READING"))));
    }

    @Override
    public void persistBook(Book book) {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase md = mongoClient.getDatabase("test");

        String today = LocalDate.now().toString();

        MongoCollection<Document> collection = md.getCollection("books");

        Document newBook = new Document("name", book.getName())
                .append("startReading", today)
                .append("days", asList(new Document("date", today)
                        .append("type", "START")));


        if (collection.find(new Document("user", "iliza")).iterator().hasNext()) {
            collection.findOneAndUpdate(new Document("user", "iliza"),
                    new Document("$addToSet", new Document("books", newBook)));
        } else {
            collection.insertOne(new Document("user", "iliza")
                    .append("startDate", today)
                    .append("books", asList(newBook)));
        }

    }

    @Override
    public FindIterable<Document> retrieveBooks() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase md = mongoClient.getDatabase("test");

        FindIterable<Document> iterable = md.getCollection("books").find();

        return iterable;

    }

    @Override
    public void purgeBooks() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase md = mongoClient.getDatabase("test");

        md.getCollection("books").deleteMany(new Document());
    }
}
