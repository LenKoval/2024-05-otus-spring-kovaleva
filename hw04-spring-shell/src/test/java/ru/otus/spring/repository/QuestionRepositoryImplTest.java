package ru.otus.spring.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.spring.config.TestFileNameProvider;
import ru.otus.spring.exceptions.QuestionReadException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = QuestionRepositoryImpl.class)
public class QuestionRepositoryImplTest {

    private static final String notExist = "not_exist.csv";

    private static final String emptyTests = "test-questions.csv";

    private static final String trueTests = "questions-true.csv";

    @Autowired
    private static QuestionRepository questionRepository;

    @MockBean
    private static TestFileNameProvider fileNameProvider;

    @BeforeEach
    void getUp() {
        questionRepository = new QuestionRepositoryImpl(fileNameProvider);
    }

    @Test
    void testQuestionReadException() {
        when(fileNameProvider.getTestFileName()).thenReturn(notExist);
        assertThrows(QuestionReadException.class, questionRepository::getQuestions);
    }

    @Test
    void testEmptyTests() {
        when(fileNameProvider.getTestFileName()).thenReturn(emptyTests);
        assertThat(questionRepository.getQuestions().isEmpty());
        verify(fileNameProvider, times(1)).getTestFileName();
    }

    @Test
    void testGetQuestions() {
        when(fileNameProvider.getTestFileName()).thenReturn(trueTests);
        var questions = questionRepository.getQuestions();

        assertThat(!questions.isEmpty());
        assertEquals(5, questions.size());
    }
}
