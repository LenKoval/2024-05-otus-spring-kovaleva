package ru.otus.spring.service;

import lombok.RequiredArgsConstructor;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.Answer;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        questionDao.findAll().forEach(question -> {
            ioService.printLine(question.text());
            printAnswer(question.answers());
            ioService.printLine("");
        });
    }

    public void printAnswer(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("%d. %s", i + 1, answers.get(i).text());
        }
    }
}
