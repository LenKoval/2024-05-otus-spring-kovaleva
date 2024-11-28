package ru.otus.spring.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import static ru.otus.spring.config.JobConfig.CURRENT_TIME_PARAM_NAME;

@Slf4j
@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    private final Job importFromDatabaseJob;

    private final JobLauncher jobLauncher;

    @ShellMethod(value = "startMigrationJobWithJobLauncher", key = "sm-jl")
    public void processMigrationJob() {
        try {
            JobExecution execution = jobLauncher.run(importFromDatabaseJob, getJobParameters());
            log.info("Job execution completed. Status: {}, ExitStatus: {}",
                    execution.getStatus(), execution.getExitStatus());
        } catch (Exception e) {
            log.error("Error occurred during job execution", e);
            throw new RuntimeException("Failed to execute migration job", e);
        }
    }

    private JobParameters getJobParameters() {
        return new JobParametersBuilder()
                .addLong(CURRENT_TIME_PARAM_NAME, System.currentTimeMillis())
                .toJobParameters();
    }
}
