package com.anastasiat.author.controller;

import com.anastasiat.author.client.AuthorRestClient;
import com.anastasiat.author.controller.payload.NewAuthorPayload;
import com.anastasiat.author.entity.AuthorDTO;
import com.anastasiat.author.entity.AuthorPageDTO;
import com.anastasiat.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("library/authors")
public class AuthorController {

    private final AuthorRestClient authorRestClient;

    @GetMapping("list")
    public String getAuthorsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        AuthorPageDTO authorPage = authorRestClient.findAllAuthors(page, size);
        model.addAttribute("authors", authorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", authorPage.getTotalPages());
        return "library/authors/list";
    }

    @GetMapping("create")
    public String getNewAuthorPage() {
        return "library/authors/new_author";
    }

    @PostMapping("create")
    public String createAuthor(NewAuthorPayload payload, Model model) {
        try {
            AuthorDTO author = authorRestClient.createAuthor(
                    payload.firstName(),
                    payload.lastName(),
                    payload.middleName(),
                    payload.birthDate()
            );
            model.addAttribute("author", author);
            return "library/authors/author";
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());
            return "library/authors/new_author";
        }
    }

    @GetMapping("/{authorId}")
    public String getAuthor(@PathVariable("authorId") Integer authorId, Model model) {
        AuthorDTO author = authorRestClient.findAuthor(authorId);
        model.addAttribute("author", author);
        return "library/authors/author";
    }

    @PostMapping("/{authorId}/delete")
    public String deleteAuthor(@PathVariable Integer authorId, Model model, @ModelAttribute AuthorDTO author) {
        try {
            authorRestClient.deleteAuthor(authorId);
            return "redirect:/library/authors/list";
        } catch (BadRequestException exception) {
            model.addAttribute("author", author);
            model.addAttribute("errors", exception.getErrors());
            return "library/authors/author";
        }
    }
}
