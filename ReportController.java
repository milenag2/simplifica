package com.simplifica.simplificajava.controller;

import com.simplifica.simplificajava.dto.CategoryReportDTO;
import com.simplifica.simplificajava.dto.TransactionDTO;
import com.simplifica.simplificajava.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/balance/monthly")
    public ResponseEntity<Map<String, Double>> getMonthlyBalance(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        int currentYear = year != null ? year : LocalDate.now().getYear();
        int currentMonth = month != null ? month : LocalDate.now().getMonthValue();

        Map<String, Double> balance = transactionService.getMonthlyBalance(userId, currentYear, currentMonth);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/daily")
    public ResponseEntity<List<TransactionDTO>> getDailyReport(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TransactionDTO> transactions = transactionService.getDailyReport(userId, date);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<TransactionDTO>> getMonthlyReport(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        int currentYear = year != null ? year : LocalDate.now().getYear();
        int currentMonth = month != null ? month : LocalDate.now().getMonthValue();

        List<TransactionDTO> transactions = transactionService.getMonthlyReport(userId, currentYear, currentMonth);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/annual")
    public ResponseEntity<Map<Integer, Map<String, Double>>> getAnnualReport(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer year) {

        int currentYear = year != null ? year : LocalDate.now().getYear();

        Map<Integer, Map<String, Double>> annualReport = transactionService.getAnnualReport(userId, currentYear);
        return ResponseEntity.ok(annualReport);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryReportDTO>> getCategoryReport(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type) {
        List<CategoryReportDTO> categoryReport = transactionService.getCategoryReport(userId, startDate, endDate, type);
        return ResponseEntity.ok(categoryReport);
    }
}

