package com.anastasiat.author.service;

import com.anastasiat.author.entity.Author;
import com.anastasiat.author.entity.AuthorPage;
import com.anastasiat.author.repository.AuthorRepository;
import com.anastasiat.book.entity.Book;
import com.anastasiat.book.service.BookService;
import com.anastasiat.exception.AlreadyExistsException;
import com.anastasiat.exception.NotExistsException;
import com.anastasiat.exception.UnavailableOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookService bookService;

    @Override
    public Optional<Author> findAuthorById(Integer authorId) {
        return authorRepository.findById(authorId);
    }

    @Override
    public Iterable<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public AuthorPage findAllAuthors(int page, int size) {
        Page<Author> authorPage = authorRepository.findAll(PageRequest.of(page, size));
        return new AuthorPage(
                authorPage.getContent(),
                authorPage.getTotalElements(),
                authorPage.getTotalPages(),
                authorPage.isLast()
        );
    }

    @Override
    @Transactional
    public Author createAuthor(String firstName, String lastName, String middleName, LocalDate birthDate) {
        if (authorRepository.findByFirstNameAndLastNameAndMiddleNameAndBirthDate(
                firstName,
                lastName,
                middleName,
                birthDate).isPresent()
        ) {
            throw new AlreadyExistsException("library.errors.author.already_exists");
        }

        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        Optional.ofNullable(middleName).ifPresent(author::setMiddleName);
        author.setBirthDate(birthDate);

        author = authorRepository.save(author);
        log.info("Автор создан id {}", author.getId());
        return author;
    }

    @Override
    @Transactional
    public void deleteAuthor(Integer authorId) {
        authorRepository.findById(authorId)
                .orElseThrow(() -> new NotExistsException("library.errors.author.not_found"));

        List<Book> books = bookService.findBooksByAuthorId(authorId);
        if (!CollectionUtils.isEmpty(books)) {
            throw new UnavailableOperationException("library.errors.author.delete_unavailable");
        }
        authorRepository.deleteById(authorId);
        log.info("Автор удален id {}", authorId);
    }
}
