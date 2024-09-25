package ru.otus.spring.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.spring.dtos.CommentViewDto;
import ru.otus.spring.dtos.CommentViewNotIdDto;
import ru.otus.spring.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/books/{bookId}/comments")
    public String getPageCommentList(@PathVariable long bookId, Model model) {
        List<CommentViewDto> comments = commentService.findCommentByBookId(bookId);
        model.addAttribute("comments", comments);
        model.addAttribute("bookId", bookId);
        model.addAttribute("createComment", new CommentViewNotIdDto());

        return "page-comment-list";
    }

    @PostMapping("/books/{bookId}/comments")
    public String createComment(@PathVariable long bookId, @ModelAttribute @Valid CommentViewNotIdDto commentDto) {
        commentService.create(commentDto.getText(), commentDto.getBookId());

        return "redirect:/books/%s/comments".formatted(bookId);
    }

    @PostMapping("/books/{bookId}/comments/{commentId}")
    public String updateComment(@PathVariable long bookId,
                                @PathVariable long commentId,
                                @RequestParam @Valid @NotBlank String text) {
        commentService.update(commentId, text);

        return "redirect:/books/%s/comments".formatted(bookId);
    }

    @PostMapping("books/{bookId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable long bookId, @PathVariable long commentId) {
        commentService.deleteById(commentId);

        return "redirect:/books/%s/comments".formatted(bookId);
    }
}
