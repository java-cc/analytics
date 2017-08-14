package com.analytics.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DatabaseFillerOnStartup implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(DatabaseFillerOnStartup.class);
    private final int batchSize;

    @Resource(name = "isDBInit")
    private AtomicBoolean isDBInit;

    @Resource
    ApplicationContext context;

    private Environment environment;
    private DataSource dataSource;

    @Autowired
    public DatabaseFillerOnStartup(Environment environment, DataSource dataSource) {
        this.environment = environment;
        this.dataSource = dataSource;
        batchSize = environment.getProperty("db.batch.size", int.class);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!isDBInit.get() && Boolean.valueOf(environment.getProperty("db.init"))) {
            isDBInit.set(true);

            try (
                    Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()
            ) {

                log.info("Start filling DB");

                connection.setAutoCommit(false);

                statement.executeUpdate(environment.getProperty("db.table.request.drop"));
                statement.executeUpdate(environment.getProperty("db.table.user.drop"));
                connection.commit();
                log.info("Tables drop complete");

                statement.executeUpdate(environment.getProperty("db.table.user.create"));
                statement.executeUpdate(environment.getProperty("db.table.request.create"));
                connection.commit();
                log.info("Tables create complete");

                ClassLoader classLoader = getClass().getClassLoader();

                log.info("Filling user table");
                File file = new File(classLoader.getResource("users.csv").getFile());
                fillTable(connection, file, environment.getProperty("db.table.user.insert"), CSVUtils.DATETIME_PATTERN_WITH_TIMEZONE);

                log.info("Filling request table");
                file = new File(classLoader.getResource("requests.csv").getFile());
                fillTable(connection, file, environment.getProperty("db.table.request.insert"), CSVUtils.DATETIME_PATTERN);

                log.info("End filling DB");
            } catch (SQLException e) {
                log.error("SQL error.", e);
            }
        }
    }

    private void fillTable(Connection connection, File csvFile, String preparedQuery, String datePattern) {
        try (
                BufferedReader reader = new BufferedReader(new FileReader(csvFile));
                PreparedStatement preparedStatement = connection.prepareStatement(preparedQuery)
        ) {
            reader.readLine();
            List<Object[]> argsList;
            int count = 0;
            while (true) {
                count++;
                argsList = CSVUtils.getArgsListFromCSVForBatch(reader, datePattern, batchSize);
                if (argsList.isEmpty()) {
                    break;
                }

                log.info("Create batch #" + count);
                for (Object[] args : argsList) {
                    preparedStatement.setObject(1, args[0]);
                    preparedStatement.setObject(2, args[1]);
                    preparedStatement.addBatch();
                }

                log.info("Execute batch #" + count);
                preparedStatement.executeBatch();
                connection.commit();
                log.info("Data from batch #" + count + " inserted");
            }
        } catch (IOException e) {
            log.error("Read CSV file error.", e);
        } catch (SQLException e) {
            log.error("SQL error.", e);
        }
    }
}
