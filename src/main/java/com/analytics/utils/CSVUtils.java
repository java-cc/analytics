package com.analytics.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CSVUtils {

    public static final String DATETIME_PATTERN_WITH_TIMEZONE = "yyyy-MM-dd hh:mm:ssZ";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
    private static final String COMMA_DELIMITER = ",";
    private static final Logger log = LoggerFactory.getLogger(CSVUtils.class);

    public static List<Object[]> getArgsListFromCSVForBatch(BufferedReader reader, String datePattern, int batchSize) {
        try {

            List<Object[]> result = new ArrayList<>();
            String current;
            int count = 0;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

            while (count < batchSize) {
                current = reader.readLine();
                if (current == null) {
                    break;
                }
                String[] argsInString = current.split(COMMA_DELIMITER);
                if (argsInString.length == 2) {
                    String string = argsInString[0].trim();
                    String dateArg = argsInString[1].trim();
                    Date date = null;
                    if (!dateArg.equals("null")) {
                        date = simpleDateFormat.parse(argsInString[1].trim());
                    }
                    result.add(new Object[]{string, date});
                    count++;
                }
            }

            return result;

        } catch (IOException e) {
            log.error("Read CSV file error.", e);
        } catch (ParseException e) {
            log.error("Date parse error.", e);
        }
        return new ArrayList<>();
    }
}
