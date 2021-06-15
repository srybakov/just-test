package ru.mvideo.just.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mvideo.just.model.Book;

public interface BookRepository extends ReactiveCrudRepository<Book, Long> {

    Flux<Book> findByAuthor(String author);

    @Modifying
    @Query("UPDATE book SET title = :title where id = :id")
    Mono<Integer> updateTitle(@Param("id") long id, @Param("title") String title);

}
