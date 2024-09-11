package ru.otus.spring.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    @Indexed(unique = true)
    private String text;

    @Indexed(unique = true)
    private Book book;

    public Comment(String text, Book book) {
        this.text = text;
        this.book = book;
    }
}
