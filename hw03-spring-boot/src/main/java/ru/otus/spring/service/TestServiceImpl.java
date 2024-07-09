package ru.otus.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;
import ru.otus.spring.domain.Student;
import ru.otus.spring.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            printQuestion(question);

            Answer answer = getStudentAnswer(question.answers());
            ioService.printLine("");

            testResult.applyAnswer(question, answer.isCorrect());
        }
        return testResult;
    }

    private void printQuestion(Question question) {
        ioService.printLine(question.text());
        printAnswers(question.answers());
        ioService.printLine("");
    }

    private void printAnswers(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("%d) %s", i + 1, answers.get(i).text());
        }
    }

    private Answer getStudentAnswer(List<Answer> answers) {
        int answerIdx = ioService.readIntForRange(1, answers.size(), "Wrong answer number. Try again...\n");
        return answers.get(answerIdx - 1);
    }
}
