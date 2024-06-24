package ru.otus.spring.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.spring.config.TestFileNameProvider;
import ru.otus.spring.domain.Question;
import ru.otus.spring.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TestCsvQuestionDao {

    private static final String notExist = "not_exist.csv";

    private static final String emptyTests = "test-questions.csv";

    private static final String trueTests = "questions-true.csv";

    private static QuestionDao dao;

    private static TestFileNameProvider fileNameProvider;

    @BeforeEach
    void mockUp() {
        fileNameProvider = mock(TestFileNameProvider.class);
        dao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    void testQuestionReadException() {
        when(fileNameProvider.getTestFileName()).thenReturn(notExist);
        assertThrows(QuestionReadException.class, dao::findAll);
    }

    @Test
    void testEmptyTests() {
        when(fileNameProvider.getTestFileName()).thenReturn(emptyTests);
        assertThat(dao.findAll().isEmpty());
        verify(fileNameProvider, times(1)).getTestFileName();
    }

    @Test
    void testFindAll() {
        when(fileNameProvider.getTestFileName()).thenReturn(trueTests);
        List<Question> questions = dao.findAll();
        assertEquals(questions.size(), 5);
        assertEquals(questions.get(2).text(), "Which option is a good way to handle the exception?");
        verify(fileNameProvider, times(1)).getTestFileName();
    }
}
