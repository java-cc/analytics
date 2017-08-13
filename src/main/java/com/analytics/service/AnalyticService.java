package com.analytics.service;

import com.analytics.dao.AnalyticDAO;
import com.analytics.request.DAURequest;
import com.analytics.request.RetentionRequest;
import com.analytics.entity.DAUEntity;
import com.analytics.entity.RetentionEntity;
import com.analytics.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticService {

    private static final String DATE_DELIMITER = ", ";
    private AnalyticDAO dao;

    @Autowired
    public AnalyticService(AnalyticDAO dao) {
        this.dao = dao;
    }

    public List<DAUEntity> getDAU(DAURequest request) throws SQLException {
        String dates = request.getDates().stream()
                .map(DateUtil::getDateStringInQuotes)
                .collect(Collectors.joining(DATE_DELIMITER));
        return dao.getDAU(dates);
    }

    public List<RetentionEntity> getRetention(RetentionRequest request) throws SQLException {
        Date startDate = request.getInstallDate();
        RetentionRequest.RetentionType retentionType = request.getRetentionType();
        Date endDate = DateUtil.getEndRetentionDate(startDate, retentionType.getRetention());
        String start = DateUtil.getDateString(startDate);
        String end = DateUtil.getDateString(endDate);
        return dao.getRetention(start, end);
    }
}
