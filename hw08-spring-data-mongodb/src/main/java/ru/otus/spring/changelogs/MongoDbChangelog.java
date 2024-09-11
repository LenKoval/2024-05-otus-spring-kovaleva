package ru.otus.spring.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.spring.models.Author;
import ru.otus.spring.models.Book;
import ru.otus.spring.models.Comment;
import ru.otus.spring.models.Genre;
import ru.otus.spring.repositories.AuthorRepository;
import ru.otus.spring.repositories.BookRepository;
import ru.otus.spring.repositories.CommentRepository;
import ru.otus.spring.repositories.GenreRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ChangeLog(order = "000")
public class MongoDbChangelog {

    private final Map<String, Author> authors = new HashMap<>();

    private final Map<String, Genre> genres = new HashMap<>();

    @ChangeSet(order = "000", id = "dropDb", author = "admin", runAlways = true)
    public void dropDatabase(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "insertGenres", author = "admin")
    public void insertGenres(GenreRepository repository) {
        genres.put("Southern gothic", repository.save(new Genre("Southern gothic")));
        genres.put("Western", repository.save(new Genre("Western")));
        genres.put("Novel", repository.save(new Genre("Novel")));
        genres.put("Military fiction", repository.save(new Genre("Military fiction")));
        genres.put("Autobiography", repository.save(new Genre("Autobiography")));
        genres.put("Popular science fiction", repository.save(new Genre("Popular science fiction")));
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "admin")
    public void insertAuthors(AuthorRepository repository) {
        authors.put("Cormac McCarthy", repository.save(new Author("Cormac McCarthy")));
        authors.put("Ernest Hemingway", repository.save(new Author("Ernest Hemingway")));
        authors.put("Richard Feynman", repository.save(new Author("Richard Feynman")));
    }

    @ChangeSet(order = "003", id = "insertBooks", author = "admin")
    public void insertBooks(BookRepository repository) {
        repository.save(new Book("Blood Meridian, Or the Evening Redness in the West",
                authors.get("Cormac McCarthy"),
                List.of(genres.get("Southern gothic"), genres.get("Western"), genres.get("Novel"))));
        repository.save(new Book("For Whom the Bell Tolls",
                authors.get("Ernest Hemingway"),
                List.of(genres.get("Novel"), genres.get("Military fiction"))));
        repository.save(new Book("Surely You are Joking, Mr. Feynman!",
                authors.get("Richard Feynman"),
                List.of(genres.get("Autobiography"), genres.get("Popular science fiction"))));
    }

    @ChangeSet(order = "004", id = "insertComments", author = "admin")
    public void insertComments(BookRepository bookRepository, CommentRepository commentRepository) {
        List<Book> books = bookRepository.findAll();
        commentRepository.save(new Comment("A true masterpiece", books.get(0)));
        commentRepository.save(new Comment("This book is a must read", books.get(1)));
        commentRepository.save(new Comment("This book is a real page turner", books.get(2)));
    }
}
