package ru.otus.spring.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import ru.otus.spring.config.TestFileNameProvider;
import ru.otus.spring.dao.dto.QuestionDto;
import ru.otus.spring.domain.Question;
import ru.otus.spring.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        List<Question> questions = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(fileNameProvider.getTestFileName());

        try (InputStreamReader sr = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            CsvToBean<QuestionDto> csvToBean = buildCsvToBeans(sr);
            for (QuestionDto dto : csvToBean) {
                questions.add(dto.toDomainObject());
            }
        } catch (IOException e) {
            throw new QuestionReadException("File reading exception " + fileNameProvider.getTestFileName(), e);
        }

        return questions;
    }

    private static CsvToBean<QuestionDto> buildCsvToBeans(Reader reader) {
        return new CsvToBeanBuilder<QuestionDto>(reader)
                .withSkipLines(1)
                .withIgnoreLeadingWhiteSpace(true)
                .withSeparator(';')
                .withType(QuestionDto.class)
                .build();
    }
}
