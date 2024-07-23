package ru.otus.spring.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.spring.service.TestRunnerService;

@ShellComponent(value = "Application Shell Commands")
@RequiredArgsConstructor
public class ApplicationShellCommands {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Run Test command", key = {"test"})
    public String runTest() {
        testRunnerService.run();
        return "Test done.";
    }
}
