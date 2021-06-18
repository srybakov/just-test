package ru.mvideo.just.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mvideo.just.dto.UpdateBookDto;
import ru.mvideo.just.model.Book;
import ru.mvideo.just.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public Flux<Book> getAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Flux<Book> getByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Transactional
    public Mono<Book> createBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Mono<Book> updateBook(long id, Mono<UpdateBookDto> updateBookDtoMono) {
        return bookRepository.findById(id)
                .flatMap(bookToUpdate -> updateBookDtoMono.map(updateBookDto -> {
                    bookToUpdate.setTitle(updateBookDto.getTitle());
                    bookToUpdate.setAuthor(updateBookDto.getAuthor());
                    return bookToUpdate;
                }))
                .flatMap(bookRepository::save);
    }

    @Transactional
    public Mono<Integer> updateBookTitle(long id, String bookTitle) {
        return bookRepository.updateTitle(id, bookTitle);
    }

    @Transactional
    public Mono<Void> deleteBook(long id) {
        return bookRepository.deleteById(id);
    }

    /**
     * Наглядно показывает что транзакция @Transactional работает.
     *
     * В базе делаем запрос
     * select pga.pid, pga.query_start, pga.query
     * from pg_stat_activity pga
     * where pga.usesysid is not null and pga.application_name = 'r2dbc-postgresql'
     *
     * Проверяем что при 10 открытых коннекшенах, изменения делались в одной транзакции.
     * + в query последняя операция будет COMMIT
     *
     * https://drive.google.com/file/d/1PCZLW-V369oMIZ3HquQidzVG1hkGbNL2/view?usp=sharing
     */
    @Transactional
    public Mono<Integer> testOneTransaction(long id) {
        return updateBookTitle(id, "title 12")
                .then(updateBookTitle(id, "title 13"))
                .then(updateBookTitle(id, "title 14"))
                .then(updateBookTitle(id, "title 15"));
    }

    /**
     * Наглядно показывает что без @Transactional работает в отдельных транзакциях.
     *
     * В базе делаем запрос
     * select pga.pid, pga.query_start, pga.query
     * from pg_stat_activity pga
     * where pga.usesysid is not null and pga.application_name = 'r2dbc-postgresql'
     *
     * Проверяем что при 10 открытых коннекшенах, изменения делались в разных транзакциях (query_start изменится у 4х коннекшенов)
     *
     * https://drive.google.com/file/d/1xJyICnvG7RuYceapqdiVTIVyLaXD7pDB/view?usp=sharing
     */
    public Mono<Integer> testManyTransaction(long id) {
        return updateBookTitle(id, "title 12")
                .then(updateBookTitle(id, "title 13"))
                .then(updateBookTitle(id, "title 14"))
                .then(updateBookTitle(id, "title 15"));
    }

    /**
     * Показываем что  Rollback для транзакций работает
     */
    @Transactional
    public Mono<Void> testOneTransactionRollback(long id) {
        updateBook(id, Mono.just(UpdateBookDto.builder().author("updated author 1").title("updated title 1").build()));
        updateBook(id, Mono.just(UpdateBookDto.builder().author("updated author 2").title("updated title 2").build()));
        throw new IllegalStateException();
    }
}
