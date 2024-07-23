package ru.otus.spring.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.spring.domain.Question;
import ru.otus.spring.exceptions.QuestionReadException;
import ru.otus.spring.repository.QuestionRepository;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {
    private final QuestionRepository questionRepository;

    @Override
    public List<Question> findAll() {
        try {
            return questionRepository.getQuestions();
        } catch (Exception e) {
            throw new QuestionReadException("Error reading Question! ", e);
        }
    }

}
