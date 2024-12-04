package ru.otus.spring.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.spring.models.mongo.MongoAuthor;
import ru.otus.spring.models.mongo.MongoBook;
import ru.otus.spring.models.mongo.MongoComment;
import ru.otus.spring.models.mongo.MongoGenre;
import ru.otus.spring.repositories.mongo.MongoAuthorRepository;
import ru.otus.spring.repositories.mongo.MongoBookRepository;
import ru.otus.spring.repositories.mongo.MongoCommentRepository;
import ru.otus.spring.repositories.mongo.MongoGenreRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureDataMongo
@SpringBatchTest
@Import(JobConfig.class)
public class JobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private MongoAuthorRepository authorRepository;

    @Autowired
    private MongoGenreRepository genreRepository;

    @Autowired
    private MongoBookRepository bookRepository;

    @Autowired
    private MongoCommentRepository commentRepository;

    @Test
    void importFromDatabaseJob() throws Exception {
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(JobConfig.IMPORT_LIBRARY_JOB_NAME);

        JobParameters parameters = new JobParametersBuilder()
                .addLong(JobConfig.CURRENT_TIME_PARAM_NAME, System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        List<MongoBook> actualBooks = bookRepository.findAll().stream()
                .map(this::initializeBookLazyFields)
                .collect(Collectors.toList());

        List<MongoAuthor> actualAuthors = authorRepository.findAll().stream()
                .map(this::initializeAuthorLazyFields)
                .collect(Collectors.toList());

        List<MongoGenre> actualGenres = genreRepository.findAll().stream()
                .map(this::initializeGenreLazyFields)
                .collect(Collectors.toList());

        List<MongoComment> actualComments = commentRepository.findAll().stream()
                .map(this::initializeCommentLazyFields)
                .collect(Collectors.toList());

        assertThat(actualBooks)
                .isNotEmpty()
                .hasSameElementsAs(getTestBookList());

        assertThat(actualComments)
                .isNotEmpty()
                .hasSameElementsAs(getTestCommentList());

        assertThat(actualAuthors)
                .isNotEmpty()
                .hasSameElementsAs(getTestAuthorList());

        assertThat(actualGenres)
                .isNotEmpty()
                .hasSameElementsAs(getTestGenreList());
    }

    private MongoBook initializeBookLazyFields(MongoBook book) {
        return new MongoBook(book.getId(), book.getTitle(), book.getAuthor(), book.getGenres().stream().toList());
    }

    private MongoAuthor initializeAuthorLazyFields(MongoAuthor author) {
        return new MongoAuthor(author.getId(), author.getFullName());
    }

    private MongoGenre initializeGenreLazyFields(MongoGenre genre) {
        return new MongoGenre(genre.getId(), genre.getName());
    }

    private MongoComment initializeCommentLazyFields(MongoComment comment) {
        return new MongoComment(comment.getId(), comment.getText(), initializeBookLazyFields(comment.getBook()));
    }

    private List<MongoAuthor> getTestAuthorList() {
        return List.of(new MongoAuthor("1", "Author_1"),
                new MongoAuthor("2", "Author_2"),
                new MongoAuthor("3", "Author_3"));
    }

    private List<MongoGenre> getTestGenreList() {
        return List.of(new MongoGenre("1", "Genre_1"),
                new MongoGenre("2", "Genre_2"),
                new MongoGenre("3", "Genre_3"),
                new MongoGenre("4", "Genre_4"),
                new MongoGenre("5", "Genre_5"),
                new MongoGenre("6", "Genre_6"));
    }

    private List<MongoBook> getTestBookList() {
        List<MongoGenre> genreList = getTestGenreList();
        List<MongoAuthor> authorList = getTestAuthorList();

        return List.of(new MongoBook("1", "BookTitle_1", authorList.get(0), List.of(genreList.get(0), genreList.get(1))),
                new MongoBook("2", "BookTitle_2", authorList.get(1), List.of(genreList.get(2), genreList.get(3))),
                new MongoBook("3", "BookTitle_3", authorList.get(2), List.of(genreList.get(4), genreList.get(5))));
    }

    private List<MongoComment> getTestCommentList() {
        List<MongoBook> bookList = getTestBookList();

        return List.of(new MongoComment("1", "Comment_1", bookList.get(0)),
                new MongoComment("2", "Comment_2", bookList.get(1)),
                new MongoComment("3", "Comment_3", bookList.get(2)));
    }

}
