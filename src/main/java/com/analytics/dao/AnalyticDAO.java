package com.analytics.dao;

import com.analytics.entity.DAUEntity;
import com.analytics.entity.RetentionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnalyticDAO {

    private static final Logger log = LoggerFactory.getLogger(AnalyticDAO.class);

    private static final String DAU_QUERY = "SELECT req_date as `date`, count(*) AS `dau`\n" +
            "FROM (SELECT DISTINCT\n" +
            "        user_id,\n" +
            "        DATE(request_date) AS 'req_date'\n" +
            "      FROM request\n" +
            "      WHERE DATE(request_date) IN (%s)) AS `uniq_req`\n" +
            "GROUP BY uniq_req.req_date";

    private static final String RETENTION_QUERY = "SELECT\n" +
            "  req_stat.ins_date AS `ins_date`,\n" +
            "  req_date,\n" +
            "  (requests / ins_count * 100) as `percentage`\n" +
            "FROM (\n" +
            "       SELECT\n" +
            "         ins_date,\n" +
            "         req_date,\n" +
            "         count(req_date) AS `requests`\n" +
            "       FROM (SELECT DISTINCT\n" +
            "               user_id,\n" +
            "               date(install_date) AS `ins_date`\n" +
            "             FROM user\n" +
            "             WHERE date(install_date) BETWEEN ? AND ?) `installed`\n" +
            "         JOIN (SELECT DISTINCT\n" +
            "                      user_id,\n" +
            "                      date(request_date) AS `req_date`\n" +
            "                    FROM request\n" +
            "                    WHERE date(\n" +
            "                        request_date) BETWEEN ? AND ?) `requested`\n" +
            "           ON (installed.user_id = requested.user_id AND installed.ins_date <= requested.req_date)\n" +
            "       WHERE req_date IS NOT NULL\n" +
            "       GROUP BY ins_date, req_date) `req_stat`\n" +
            "  JOIN (SELECT DISTINCT\n" +
            "          date(install_date) AS `ins_date`,\n" +
            "          count(*)           AS `ins_count`\n" +
            "        FROM user\n" +
            "        WHERE date(install_date) BETWEEN ? AND ?\n" +
            "        GROUP BY date(install_date)) ins_stat\n" +
            "    ON req_stat.ins_date = ins_stat.ins_date\n" +
            "ORDER BY ins_date, req_date";

    private DataSource dataSource;

    @Autowired
    public AnalyticDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<DAUEntity> getDAU(String dates) throws SQLException {
        String query = String.format(DAU_QUERY, dates);
        List<DAUEntity> result = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(query)
        ) {
            while (resultSet.next()) {
                DAUEntity response = new DAUEntity(resultSet.getDate("date"), resultSet.getInt("dau"));
                result.add(response);
            }

        } catch (SQLException e) {
            log.error("SQL exception", e);
            throw e;
        }
        return result;
    }


    public List<RetentionEntity> getRetention(String start, String end) throws SQLException {
        List<RetentionEntity> result = new ArrayList<>();
        try (
                Connection con = dataSource.getConnection();
                PreparedStatement statement = con.prepareStatement(RETENTION_QUERY)
        ) {
            for (int i = 1; i <= 6; i++) {
                if (i % 2 == 0) {
                    statement.setString(i, end);
                } else {
                    statement.setString(i, start);
                }
            }

            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RetentionEntity response = new RetentionEntity(
                            resultSet.getDate("ins_date"),
                            resultSet.getDate("req_date"),
                            resultSet.getDouble("percentage")
                    );
                    result.add(response);
                }
            }

        } catch (SQLException e) {
            log.error("SQL exception", e);
            throw e;
        }
        return result;

    }

}
