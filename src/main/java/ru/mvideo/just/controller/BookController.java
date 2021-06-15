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
import ru.mvideo.just.repository.BookRepository;

@RestController
@RequestMapping(value = "/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;

    @GetMapping
    public Flux<Book> getAll() {
        return bookRepository.findAll();
    }

    @GetMapping("/author/{author}")
    public Flux<Book> getByAuthor(@PathVariable(name = "author") String author) {
        return bookRepository.findByAuthor(author);
    }

    @PostMapping
    public Mono<Integer> createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @PutMapping("/{id}")
    public Mono<Integer> updateBook(@PathVariable(name = "id") long id, @RequestBody Mono<UpdateBookDto> updateBookDtoMono) {
        return bookRepository.findById(id)
                .flatMap(bookToUpdate -> updateBookDtoMono.map(updateBookDto -> {
                    bookToUpdate.setTitle(updateBookDto.getTitle());
                    bookToUpdate.setAuthor(updateBookDto.getAuthor());
                    return bookToUpdate;
                }))
                .flatMap(book -> bookRepository.update(id, book));
    }

    @PutMapping("/{id}/title")
    public Mono<Integer> updateBookTitle(@PathVariable(name = "id") long id, @RequestBody UpdateBookTitleDto bookTitleDto) {
        return bookRepository.updateTitle(id, bookTitleDto.getTitle());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteBook(@PathVariable(name = "id") long id) {
        return bookRepository.deleteById(id);
    }
}
