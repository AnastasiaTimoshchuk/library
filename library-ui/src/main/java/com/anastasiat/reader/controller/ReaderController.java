package com.anastasiat.reader.controller;

import com.anastasiat.exception.BadRequestException;
import com.anastasiat.reader.client.ReaderRestClient;
import com.anastasiat.reader.controller.payload.NewReaderPayload;
import com.anastasiat.reader.entity.ReaderDTO;
import com.anastasiat.reader.entity.ReaderPageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("library/readers")
public class ReaderController {

    private final ReaderRestClient readerRestClient;

    @GetMapping("list")
    public String getReadersList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        ReaderPageDTO readerPage = readerRestClient.findAllReaders(page, size);
        model.addAttribute("readers", readerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", readerPage.getTotalPages());
        return "library/readers/list";
    }

    @GetMapping("create")
    public String getNewReaderPage() {
        return "library/readers/new_reader";
    }

    @PostMapping("create")
    public String createReader(NewReaderPayload payload, Model model) {
        try {
            ReaderDTO reader = readerRestClient.createReader(
                    payload.firstName(),
                    payload.lastName(),
                    payload.middleName(),
                    payload.email()
            );
            model.addAttribute("reader", reader);
            return "library/readers/reader";
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "library/readers/new_reader";
        }
    }

    @GetMapping("/{readerId}")
    public String getReader(@PathVariable("readerId") Integer readerId, Model model) {
        ReaderDTO reader = readerRestClient.findReader(readerId);
        model.addAttribute("reader", reader);
        return "library/readers/reader";
    }

    @PostMapping("/{readerId}/delete")
    public String deleteReader(@PathVariable("readerId") Integer readerId, Model model, @ModelAttribute ReaderDTO reader) {
        try {
            readerRestClient.deleteReader(readerId);
            return "redirect:/library/readers/list";
        } catch (BadRequestException exception) {
            model.addAttribute("reader", reader);
            model.addAttribute("errors", exception.getErrors());
            return "library/readers/reader";
        }
    }
}
