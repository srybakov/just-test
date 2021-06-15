package ru.mvideo.just.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mvideo.just.model.Book;

import java.util.function.BiFunction;
import java.util.function.Function;

@Repository
public class BookRepository {

    @Autowired
    private DatabaseClient databaseClient;

    public Flux<Book> findAll() {
        return databaseClient.sql("select * from book")
                .map(bookMapper())
                .all();
    }

    public Flux<Book> findByAuthor(String author) {
        return databaseClient.sql("select * from book where author = :author")
                .bind("author", author)
                .map(bookMapper())
                .all();
    }

    public Mono<Book> findById(long id) {
        return databaseClient.sql("select * from book where id = :id")
                .bind("id", id)
                .map(bookMapper())
                .one();
    }

    public Mono<Integer> updateTitle(long id, String title) {
        return databaseClient.sql("UPDATE book SET title = :title where id = :id")
                .bind("id", id)
                .bind("title", title)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Integer> update(long id, Book book) {
        return databaseClient.sql("UPDATE book SET title = :title, author = :author where id = :id")
                .bind("id", id)
                .bind("title", book.getTitle())
                .bind("author", book.getAuthor())
                .fetch()
                .rowsUpdated();
    }

    public Mono<Void> deleteById(long id) {
        return databaseClient.sql("DELETE FROM book WHERE id = :id")
                .bind("id", id)
                .then();
    }

    public Mono<Integer> save(Book book) {
        return this.databaseClient.sql("INSERT INTO  book (title, author) VALUES (:title, :author)")
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                .bind("title", book.getTitle())
                .bind("author", book.getAuthor())
                .fetch()
                .first()
                .map(objectMap -> (Integer) objectMap.get("id"));
    }

    private static BiFunction<Row, RowMetadata, Book> bookMapper() {
        return  (row, rowMetadata) -> bookMapperFunction().apply(row);
    }
    private static Function<Row, Book> bookMapperFunction() {
        return  (row) -> {
            Book book = new Book();
            book.setId(row.get("id", Long.class));
            book.setTitle(row.get("title", String.class));
            book.setAuthor(row.get("author", String.class));
            return book;
        };
    }
}
