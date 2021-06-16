package ru.mvideo.just.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.mvideo.just.dto.UpdateBookDto;
import ru.mvideo.just.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public Mono<Integer> updateBook(long id, Mono<UpdateBookDto> updateBookDtoMono) {
        return bookRepository.findById(id)
                .flatMap(bookToUpdate -> updateBookDtoMono.map(updateBookDto -> {
                    bookToUpdate.setTitle(updateBookDto.getTitle());
                    bookToUpdate.setAuthor(updateBookDto.getAuthor());
                    return bookToUpdate;
                }))
                .flatMap(book -> bookRepository.update(id, book));
    }
}
