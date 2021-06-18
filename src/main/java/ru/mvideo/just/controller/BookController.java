package ru.mvideo.just.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mvideo.just.dto.UpdateBookDto;
import ru.mvideo.just.dto.UpdateBookTitleDto;
import ru.mvideo.just.model.Book;
import ru.mvideo.just.service.BookService;

@RestController
@RequestMapping(value = "/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public Flux<Book> getAll() {
        return bookService.getAll();
    }

    @GetMapping("/author/{author}")
    public Flux<Book> getByAuthor(@PathVariable(name = "author") String author) {
        return bookService.getByAuthor(author);
    }

    @PostMapping
    public Mono<Book> createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }

    @PutMapping("/{id}")
    public Mono<Book> updateBook(@PathVariable(name = "id") long id, @RequestBody Mono<UpdateBookDto> updateBookDtoMono) {
        return bookService.updateBook(id, updateBookDtoMono);
    }

    @PutMapping("/{id}/title")
    public Mono<Integer> updateBookTitle(@PathVariable(name = "id") long id, @RequestBody UpdateBookTitleDto bookTitleDto) {
        return bookService.updateBookTitle(id, bookTitleDto.getTitle());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteBook(@PathVariable(name = "id") long id) {
        return bookService.deleteBook(id);
    }

    @PostMapping("/testUpdateInOneTransaction/{id}")
    public Mono<Integer> testOneTransaction(@PathVariable(name = "id") long id) {
        return bookService.testOneTransaction(id);
    }

    @PostMapping("/testManyTransaction/{id}")
    public Mono<Integer> testManyTransaction(@PathVariable(name = "id") long id) {
        return bookService.testManyTransaction(id);
    }

    @PostMapping("/testOneTransactionRollback/{id}")
    public Mono<Object> testOneTransactionRollback(@PathVariable(name = "id") long id) {
        return bookService.testOneTransactionRollback(id);
    }
}
