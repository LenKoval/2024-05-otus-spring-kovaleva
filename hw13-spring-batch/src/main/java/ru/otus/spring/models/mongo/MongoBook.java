package ru.otus.spring.models.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Document(collection = "books")
public class MongoBook {

    @Id
    private String id;

    private String title;

    @DBRef(lazy = true)
    private MongoAuthor author;

    @DBRef(lazy = true)
    private List<MongoGenre> genres;
}
