package com.anastasiat.reader.repository;

import com.anastasiat.reader.entity.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderRepository extends CrudRepository<Reader, Integer> {

    Page<Reader> findAll(Pageable pageable);

    Optional<Reader> findByEmail(String email);
}
