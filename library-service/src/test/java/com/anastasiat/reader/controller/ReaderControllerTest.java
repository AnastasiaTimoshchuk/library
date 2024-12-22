package com.anastasiat.reader.controller;

import com.anastasiat.exception.NotExistsException;
import com.anastasiat.reader.controller.request.CreateReaderRequest;
import com.anastasiat.reader.entity.Reader;
import com.anastasiat.reader.entity.ReaderPage;
import com.anastasiat.reader.service.ReaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReaderControllerTest {

    @Mock
    private ReaderService readerService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ReaderController readerController;

    @Test
    void findReaderById_ShouldReturnReader_WhenExists() {
        Reader reader = new Reader();
        reader.setId(1);
        reader.setFirstName("Test");
        reader.setLastName("Testov");
        when(readerService.findReaderById(1)).thenReturn(Optional.of(reader));

        Reader result = readerController.findReaderById(1);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Test");
        verify(readerService, times(1)).findReaderById(1);
    }

    @Test
    void findReaderById_ShouldThrowNotExistsException_WhenNotFound() {
        when(readerService.findReaderById(1)).thenReturn(Optional.empty());

        assertThrows(NotExistsException.class, () -> readerController.findReaderById(1));
        verify(readerService, times(1)).findReaderById(1);
    }

    @Test
    void findAllReaders_ShouldReturnAllReaders() {
        List<Reader> readers = List.of(new Reader(), new Reader());
        when(readerService.findAllReaders()).thenReturn(readers);

        Iterable<Reader> result = readerController.findAllReaders();

        assertThat(result).hasSize(2);
        verify(readerService, times(1)).findAllReaders();
    }

    @Test
    void findReaders_ShouldReturnPagedReaders() {
        ReaderPage readerPage = new ReaderPage(List.of(new Reader(), new Reader()), 2, 1, true);
        when(readerService.findAllReaders(0, 10)).thenReturn(readerPage);

        ReaderPage result = readerController.findReaders(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(readerService, times(1)).findAllReaders(0, 10);
    }

    @Test
    void createReader_ShouldSaveReader_WhenValid() throws Exception {
        CreateReaderRequest request = new CreateReaderRequest("Test", "Testov", null, "test.test@example.com");
        Reader savedReader = new Reader();
        savedReader.setId(1);
        savedReader.setFirstName("Test");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(readerService.createReader("Test", "Testov", null, "test.test@example.com")).thenReturn(savedReader);

        ResponseEntity<?> response = readerController.createReader(request, bindingResult, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(savedReader);
        verify(readerService, times(1)).createReader("Test", "Testov", null, "test.test@example.com");
    }

    @Test
    void createReader_ShouldThrowBindException_WhenInvalid() {
        CreateReaderRequest request = new CreateReaderRequest("", "", null, "invalid-email");
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(Exception.class, () -> readerController.createReader(request, bindingResult, null));
        verify(readerService, never()).createReader(anyString(), anyString(), any(), anyString());
    }

    @Test
    void deleteReader_ShouldRemoveReader_WhenExists() {
        doNothing().when(readerService).deleteReaderById(1);

        ResponseEntity<Void> response = readerController.deleteReader(1);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(readerService, times(1)).deleteReaderById(1);
    }
}