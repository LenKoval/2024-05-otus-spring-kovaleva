package ru.otus.spring.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.shell.CommandNotFound;
import org.springframework.shell.InputProvider;
import org.springframework.shell.ResultHandlerService;
import org.springframework.shell.Shell;
import ru.otus.spring.service.TestRunnerService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ApplicationShellCommandsTest {

    //@MockBean
    private InputProvider inputProvider;

    @MockBean
    private TestRunnerService testRunnerService;

    @Autowired
    private Shell shell;

    @SpyBean
    private ResultHandlerService resultHandlerService;

    private ArgumentCaptor<Object> argumentCaptor;

    @BeforeEach
    void getUp() {
        inputProvider = mock(InputProvider.class);
        argumentCaptor = ArgumentCaptor.forClass(Object.class);
    }

    @Test
    void testRunTest() throws Exception {
        when(inputProvider.readInput())
                .thenReturn(() -> "test")
                .thenReturn(null);

        shell.run(inputProvider);
        verify(resultHandlerService, times(1)).handle(argumentCaptor.capture());
        List<Object> results = argumentCaptor.getAllValues();
        assertThat(results).containsExactlyInAnyOrder("Test done.");
    }

    @Test
    void testRunTestException() {
        when(inputProvider.readInput())
                .thenReturn(() -> "hello")
                .thenReturn(null);

        assertThatCode(() -> shell.run(inputProvider)).isInstanceOf(CommandNotFound.class);
    }
}
