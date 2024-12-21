package com.anastasiat.author.repository;

import com.anastasiat.author.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Integer> {

    Page<Author> findAll(Pageable pageable);

    Optional<Author> findByFirstNameAndLastNameAndMiddleNameAndBirthDate(
            String firstName,
            String lastName,
            String middleName,
            LocalDate birthDate
    );
}
