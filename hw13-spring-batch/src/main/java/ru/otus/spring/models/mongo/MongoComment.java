package ru.otus.spring.models.mongo;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Document(collection = "comments")
public class MongoComment {

    @Id
    private String id;

    private String text;

    @DBRef(lazy = true)
    private MongoBook book;
}
