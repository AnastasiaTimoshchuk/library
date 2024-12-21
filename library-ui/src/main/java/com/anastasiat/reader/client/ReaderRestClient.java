package com.anastasiat.reader.client;

import com.anastasiat.reader.entity.ReaderDTO;
import com.anastasiat.reader.entity.ReaderPageDTO;

import java.util.List;

public interface ReaderRestClient {

    List<ReaderDTO> findAllReaders();

    ReaderPageDTO findAllReaders(int page, int size);

    ReaderDTO createReader(String firstName, String lastName, String middleName, String email);

    ReaderDTO findReader(Integer readerId);

    void deleteReader(Integer readerId);
}
