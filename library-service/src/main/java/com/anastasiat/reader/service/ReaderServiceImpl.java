package com.anastasiat.reader.service;

import com.anastasiat.book.entity.Book;
import com.anastasiat.book.service.BookService;
import com.anastasiat.exception.AlreadyExistsException;
import com.anastasiat.exception.NotExistsException;
import com.anastasiat.exception.UnavailableOperationException;
import com.anastasiat.reader.entity.Reader;
import com.anastasiat.reader.entity.ReaderPage;
import com.anastasiat.reader.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;
    private final BookService bookService;

    @Override
    public Optional<Reader> findReaderById(Integer readerId) {
        return readerRepository.findById(readerId);
    }

    @Override
    public Iterable<Reader> findAllReaders() {
        return readerRepository.findAll();
    }

    @Override
    public ReaderPage findAllReaders(int page, int size) {
        Page<Reader> readerPage = readerRepository.findAll(PageRequest.of(page, size));
        return new ReaderPage(
                readerPage.getContent(),
                readerPage.getTotalElements(),
                readerPage.getTotalPages(),
                readerPage.isLast()
        );
    }

    @Override
    @Transactional
    public Reader createReader(String firstName, String lastName, String middleName, String email) {

        if (readerRepository.findByEmail(email).isPresent()) {
            throw new AlreadyExistsException("library.errors.reader.already_exists");
        }

        Reader reader = new Reader();
        reader.setFirstName(firstName);
        reader.setLastName(lastName);
        Optional.ofNullable(middleName).ifPresent(reader::setMiddleName);
        reader.setEmail(email);

        reader = readerRepository.save(reader);
        log.info("Читатель создан id {}", reader.getId());
        return reader;
    }

    @Override
    @Transactional
    public void deleteReaderById(Integer readerId) {
        readerRepository.findById(readerId)
                .orElseThrow(() -> new NotExistsException("library.errors.reader.not_found"));

        List<Book> books = bookService.findBooksByReaderId(readerId);
        if (!CollectionUtils.isEmpty(books)) {
            throw new UnavailableOperationException("library.errors.reader.delete_unavailable");
        }
        readerRepository.deleteById(readerId);
        log.info("Читатель удален id {}", readerId);
    }
}
