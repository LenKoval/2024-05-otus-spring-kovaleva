package ru.otus.spring.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.spring.converters.CommentConverter;
import ru.otus.spring.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find comment by bookId", key = "cbid")
    public String findCommentById(String id) {
        return commentService.findById(id)
                .stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining(";" + System.lineSeparator()));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String text, String bookId) {
        return commentConverter.commentToString(commentService.create(text, bookId));
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(String id, String text) {
        return commentConverter.commentToString(commentService.update(id, text));
    }
}
