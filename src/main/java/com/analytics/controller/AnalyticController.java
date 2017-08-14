package com.analytics.controller;

import com.analytics.request.DAURequest;
import com.analytics.request.RetentionRequest;
import com.analytics.entity.DAUEntity;
import com.analytics.entity.RetentionEntity;
import com.analytics.service.AnalyticService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.sql.SQLException;
import java.util.List;

@RestController
public class AnalyticController {

    private AnalyticService service;
    private final int internalServerErrorCode = 500;

    @Autowired
    public AnalyticController(AnalyticService service) {
        this.service = service;
    }

    @RequestMapping(path = "/dau", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> dau(HttpEntity<String> httpEntity) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        String body = httpEntity.getBody();
        DAURequest request;

        try {
            request = gson.fromJson(body, DAURequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Wrong request\"}");
        }

        if (request == null || request.getDates() == null) {
            return ResponseEntity.badRequest().body("{\"message\": \"Wrong argument\"}");
        } else {
            try {
                List<DAUEntity> dauList = service.getDAU(request);
                String response = gson.toJson(dauList);
                return ResponseEntity.ok(response);
            } catch (SQLException e) {
                return ResponseEntity.status(internalServerErrorCode).body("{\"message\": \"Internal server error\"}");
            }
        }
    }

    @RequestMapping(path = "/retention", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> retention(HttpEntity<String> httpEntity) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        String body = httpEntity.getBody();
        RetentionRequest request;

        try {
            request = gson.fromJson(body, RetentionRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Wrong request\"}");
        }

        if (request == null || request.getInstallDate() == null || request.getRetentionType() == null) {
            return ResponseEntity.badRequest().body("{\"message\": \"Wrong argument\"}");
        } else {
            try {
                List<RetentionEntity> dauList = service.getRetention(request);
                String response = gson.toJson(dauList);
                return ResponseEntity.ok(response);
            } catch (SQLException e) {
                return ResponseEntity.status(internalServerErrorCode).body("{\"message\": \"Internal server error\"}");
            }
        }
    }
}
