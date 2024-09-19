package ru.otus.spring.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.spring.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе MongoDatabase для работы с жанрами книг")
@DataMongoTest
public class MongoGenresRepositoryTest {

    @Autowired
    GenreRepository genreRepository;

    List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualBooks = genreRepository.findAll();
        var expectedBooks = dbGenres;

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен загружать список всех жанров по id")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void shouldReturnCorrectGenresByIds(Genre expectedGenre) {
        var actualGenres = genreRepository.findAllById(Set.of(expectedGenre.getId()));
        for (Genre actualGenre : actualGenres) {
            assertThat(actualGenre)
                    .isEqualTo(expectedGenre);
        }
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(String.format("%s", id), "Genre_" + id))
                .toList();
    }
}
