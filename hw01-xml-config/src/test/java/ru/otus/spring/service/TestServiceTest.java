package ru.otus.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.spring.dao.QuestionDao;

import static org.mockito.Mockito.*;

public class TestServiceTest {
    private QuestionDao questionDao;
    private IOService ioService;

    @BeforeEach
    void mockUp() {
        questionDao = mock(QuestionDao.class);
        ioService = mock(IOService.class);
    }

    @Test
    void testExecuteTest() {
        TestService testService = new TestServiceImpl(ioService, questionDao);
        testService.executeTest();
        verify(questionDao, times(1)).findAll();
    }

}
