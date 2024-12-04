package ru.otus.spring.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.spring.models.jpa.JpaAuthor;
import ru.otus.spring.models.jpa.JpaBook;
import ru.otus.spring.models.jpa.JpaComment;
import ru.otus.spring.models.jpa.JpaGenre;
import ru.otus.spring.models.mongo.MongoAuthor;
import ru.otus.spring.models.mongo.MongoBook;
import ru.otus.spring.models.mongo.MongoComment;
import ru.otus.spring.models.mongo.MongoGenre;
import ru.otus.spring.processor.AuthorProcessor;
import ru.otus.spring.processor.BookProcessor;
import ru.otus.spring.processor.CommentProcessor;
import ru.otus.spring.processor.GenreProcessor;
import ru.otus.spring.repositories.jpa.JpaAuthorRepository;
import ru.otus.spring.repositories.jpa.JpaBookRepository;
import ru.otus.spring.repositories.jpa.JpaCommentRepository;
import ru.otus.spring.repositories.jpa.JpaGenreRepository;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@EnableCaching
@Configuration
public class JobConfig {

    public static final String CURRENT_TIME_PARAM_NAME = "currentTimeMillis";

    public static final String IMPORT_LIBRARY_JOB_NAME = "importLibraryJobName";

    private static final int CHUNK_SIZE = 5;

    private final Logger logger = LoggerFactory.getLogger("Batch");

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final MongoTemplate mongoTemplate;

    private final JpaAuthorRepository jpaAuthorRepository;

    private final JpaGenreRepository jpaGenreRepository;

    private final JpaCommentRepository jpaCommentRepository;

    private final JpaBookRepository jpaBookRepository;

    private final Map<String, Sort.Direction> sortingMap = Collections.singletonMap("id", Sort.Direction.ASC);

    @StepScope
    @Bean
    public RepositoryItemReader<JpaAuthor> authorRepositoryItemReader() {
        RepositoryItemReader<JpaAuthor> reader = new RepositoryItemReader<>();
        reader.setName("authorReader");

        reader.setSort(sortingMap);
        reader.setRepository(jpaAuthorRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }

    @StepScope
    @Bean
    public RepositoryItemReader<JpaGenre> genreRepositoryItemReader() {
        RepositoryItemReader<JpaGenre> reader = new RepositoryItemReader<>();
        reader.setName("genreReader");

        reader.setSort(sortingMap);
        reader.setRepository(jpaGenreRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }

    @StepScope
    @Bean
    public RepositoryItemReader<JpaComment> commentRepositoryItemReader() {
        RepositoryItemReader<JpaComment> reader = new RepositoryItemReader<>();
        reader.setName("commentReader");

        reader.setSort(sortingMap);
        reader.setRepository(jpaCommentRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }

    @StepScope
    @Bean
    public RepositoryItemReader<JpaBook> bookRepositoryItemReader() {
        RepositoryItemReader<JpaBook> reader = new RepositoryItemReader<>();
        reader.setName("bookReader");

        reader.setSort(sortingMap);
        reader.setRepository(jpaBookRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }

    @Bean
    public AuthorProcessor authorProcessor() {
        logger.info("Creating AuthorProcessor bean");
        return new AuthorProcessor();
    }

    @Bean
    public GenreProcessor genreProcessor() {
        logger.info("Creating GenreProcessor bean");
        return new GenreProcessor();
    }

    @Bean
    public CommentProcessor commentProcessor() {
        logger.info("Creating CommentProcessor bean");
        return new CommentProcessor();
    }

    @Bean
    public BookProcessor bookProcessor() {
        logger.info("Creating BookProcessor bean");
        return new BookProcessor();
    }

    @Bean
    public MongoItemWriter<MongoAuthor> mongoAuthorWriter() {
        logger.debug("Creating MongoAuthorWriter bean");
        MongoItemWriter<MongoAuthor> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("authors");
        logger.info("MongoAuthorWriter bean created");
        return writer;
    }

    @Bean
    public MongoItemWriter<MongoGenre> mongoGenreWriter() {
        MongoItemWriter<MongoGenre> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("genres");
        logger.info("MongoGenreWriter bean created");
        return writer;
    }

    @Bean
    public MongoItemWriter<MongoComment> mongoCommentWriter() {
        MongoItemWriter<MongoComment> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("comments");
        logger.info("MongoCommentWriter bean created");
        return writer;
    }

    @Bean
    public MongoItemWriter<MongoBook> mongoBookWriter() {
        MongoItemWriter<MongoBook> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("books");
        logger.info("MongoBookWriter bean created");
        return writer;
    }

    @Bean
    public Job importFromDatabaseJob(Step transformAuthorStep,
                                     Step transformGenreStep,
                                     Step transformCommentStep,
                                     Step transformBookStep) {
        return new JobBuilder(IMPORT_LIBRARY_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(transformAuthorStep)
                .next(transformGenreStep)
                .next(transformBookStep)
                .next(transformCommentStep)
                .end().build();
    }

    @Bean
    public Step transformAuthorStep(RepositoryItemReader<JpaAuthor> reader,
                                    MongoItemWriter<MongoAuthor> writer,
                                    AuthorProcessor authorProcessor) {
        return new StepBuilder("transformAuthorStep", jobRepository)
                .<JpaAuthor, MongoAuthor>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(authorProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step transformGenreStep(RepositoryItemReader<JpaGenre> reader,
                                    MongoItemWriter<MongoGenre> writer,
                                    GenreProcessor genreProcessor) {
        return new StepBuilder("transformGenreStep", jobRepository)
                .<JpaGenre, MongoGenre>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(genreProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step transformBookStep(RepositoryItemReader<JpaBook> reader,
                                    MongoItemWriter<MongoBook> writer,
                                    BookProcessor bookProcessor) {
        return new StepBuilder("transformBookStep", jobRepository)
                .<JpaBook, MongoBook>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(bookProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step transformCommentStep(RepositoryItemReader<JpaComment> reader,
                                    MongoItemWriter<MongoComment> writer,
                                    CommentProcessor commentProcessor) {
        return new StepBuilder("transformCommentStep", jobRepository)
                .<JpaComment, MongoComment>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(commentProcessor)
                .writer(writer)
                .build();
    }
}
