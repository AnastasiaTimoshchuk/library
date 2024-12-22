package com.anastasiat.reader.service;

import com.anastasiat.book.entity.Book;
import com.anastasiat.book.service.BookService;
import com.anastasiat.exception.AlreadyExistsException;
import com.anastasiat.exception.NotExistsException;
import com.anastasiat.exception.UnavailableOperationException;
import com.anastasiat.reader.entity.Reader;
import com.anastasiat.reader.entity.ReaderPage;
import com.anastasiat.reader.repository.ReaderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReaderServiceImplTest {

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReaderServiceImpl readerService;

    @Test
    void findReaderById_ShouldReturnReader_WhenExists() {
        Reader reader = new Reader();
        when(readerRepository.findById(1)).thenReturn(Optional.of(reader));

        Optional<Reader> result = readerService.findReaderById(1);

        assertThat(result).contains(reader);
    }

    @Test
    void findReaderById_ShouldReturnEmpty_WhenNotExists() {
        when(readerRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Reader> result = readerService.findReaderById(1);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllReaders_ShouldReturnAllReaders() {
        Reader reader = new Reader();
        when(readerRepository.findAll()).thenReturn(List.of(reader));

        Iterable<Reader> result = readerService.findAllReaders();

        assertThat(result).containsExactly(reader);
    }

    @Test
    void findAllReaders_ShouldReturnPagedReaders() {
        Reader reader = new Reader();
        Page<Reader> page = new PageImpl<>(List.of(reader), PageRequest.of(0, 10), 1);
        when(readerRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        ReaderPage result = readerService.findAllReaders(0, 10);

        assertThat(result.getContent()).containsExactly(reader);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }

    @Test
    void createReader_ShouldThrowException_WhenEmailExists() {
        when(readerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Reader()));

        assertThatThrownBy(() -> readerService.createReader("Test", "Testov", null, "test@example.com"))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("library.errors.reader.already_exists");
    }

    @Test
    void createReader_ShouldSaveAndReturnReader_WhenEmailNotExists() {
        Reader reader = new Reader();
        reader.setId(1);
        when(readerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(readerRepository.save(any())).thenReturn(reader);

        Reader result = readerService.createReader("Test", "Testov", null, "test@example.com");

        ArgumentCaptor<Reader> captor = ArgumentCaptor.forClass(Reader.class);
        verify(readerRepository).save(captor.capture());
        Reader savedReader = captor.getValue();
        assertThat(savedReader.getFirstName()).isEqualTo("Test");
        assertThat(savedReader.getLastName()).isEqualTo("Testov");
        assertThat(savedReader.getEmail()).isEqualTo("test@example.com");
        assertThat(result).isEqualTo(reader);
    }

    @Test
    void deleteReaderById_ShouldThrowException_WhenReaderNotFound() {
        when(readerRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> readerService.deleteReaderById(1))
                .isInstanceOf(NotExistsException.class)
                .hasMessage("library.errors.reader.not_found");
    }

    @Test
    void deleteReaderById_ShouldThrowException_WhenBooksExist() {
        when(readerRepository.findById(1)).thenReturn(Optional.of(new Reader()));
        when(bookService.findBooksByReaderId(1)).thenReturn(List.of(new Book()));

        assertThatThrownBy(() -> readerService.deleteReaderById(1))
                .isInstanceOf(UnavailableOperationException.class)
                .hasMessage("library.errors.reader.delete_unavailable");
    }

    @Test
    void deleteReaderById_ShouldDeleteReader_WhenNoBooksExist() {
        when(readerRepository.findById(1)).thenReturn(Optional.of(new Reader()));
        when(bookService.findBooksByReaderId(1)).thenReturn(List.of());

        readerService.deleteReaderById(1);

        verify(readerRepository).deleteById(1);
    }
}
