package ru.otus.spring.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.spring.dtos.CommentUpdateDto;
import ru.otus.spring.dtos.CommentCreateDto;
import ru.otus.spring.dtos.CommentDto;
import ru.otus.spring.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/books/{bookId}/comments")
    public String getPageCommentList(@PathVariable("bookId") long bookId, Model model) {
        List<CommentDto> comments = commentService.findCommentByBookId(bookId);
        model.addAttribute("comments", comments);
        model.addAttribute("bookId", bookId);
        model.addAttribute("createComment", new CommentCreateDto());

        return "page-comment-list";
    }

    @PostMapping("/books/{bookId}/comments")
    public String createComment(@PathVariable long bookId,
                                @ModelAttribute("comment") @Valid CommentCreateDto commentDto) {
        commentService.create(commentDto);

        return "redirect:/books/%d/comments".formatted(bookId);
    }

    @PostMapping("/books/{bookId}/comments/{commentId}/update")
    public String updateComment(@PathVariable("bookId") long bookId, @PathVariable("commentId") long commentId,
                                @RequestParam @Valid @NotBlank String updatedText) {

        CommentUpdateDto commentDto = new CommentUpdateDto(commentId, updatedText);

        commentService.update(commentDto);

        return "redirect:/books/%d/comments".formatted(bookId);
    }

    @PostMapping("books/{bookId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable long bookId, @PathVariable long commentId) {
        commentService.deleteById(commentId);

        return "redirect:/books/%d/comments".formatted(bookId);
    }
}
