package ru.otus.spring.config.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;

@ChangeLog(order = "001")
public class MongoChangelog {

    @ChangeSet(order = "000", id = "dropDb", author = "admin", runAlways = true)
    public void dropDb(MongoDatabase database) {
        database.drop();
    }
}
