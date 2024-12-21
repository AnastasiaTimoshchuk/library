package com.anastasiat.reader.service;

import com.anastasiat.reader.entity.Reader;
import com.anastasiat.reader.entity.ReaderPage;

import java.util.Optional;

public interface ReaderService {

    Optional<Reader> findReaderById(Integer readerId);

    Iterable<Reader> findAllReaders();

    ReaderPage findAllReaders(int page, int size);

    Reader createReader(String firstName, String lastName, String middleName, String email);

    void deleteReaderById(Integer readerId);
}
