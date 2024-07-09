package ru.otus.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import ru.otus.spring.domain.Student;
import ru.otus.spring.domain.TestResult;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class TestServiceImplTest {
    private TestService testService;
    private static LocalizedIOService ioService;
    private static QuestionDao questionDao;
    private final Student student = new Student("lena", "koval");

    @BeforeEach
    void mockUp() {
        ioService = mock(LocalizedIOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void shouldReturnSuccessfulTestResultForOneQuestion() {
        List<Question> questions = createTestQuestions();
        TestResult expectedTestResult = createTestResult(student, questions);

        given(questionDao.findAll()).willReturn(questions);
        given(ioService.readIntForRangeLocalized(anyInt(), anyInt(), any())).willReturn(1);

        TestService sut = new TestServiceImpl(ioService, questionDao);
        TestResult actualTestResult = sut.executeTestFor(student);
        assertThat(actualTestResult).usingRecursiveComparison().isEqualTo(expectedTestResult);
    }

    public List<Question> createTestQuestions() {
        return List.of(
                new Question("2 * 2 = ?",
                        List.of(new Answer("4", true),
                                new Answer("2", false),
                                new Answer("0", false)))
        );
    }

    public TestResult createTestResult(Student student, List<Question> questions) {
        TestResult testResult = new TestResult(student);
        testResult.getAnsweredQuestions().addAll(questions);
        testResult.setRightAnswersCount(1);
        return testResult;
    }
}
