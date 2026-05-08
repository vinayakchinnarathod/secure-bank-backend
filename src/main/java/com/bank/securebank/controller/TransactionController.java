package com.bank.securebank.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @GetMapping
    public List<Map<String, Object>> getTransactions(
            @RequestParam String username) {

        List<Map<String, Object>> transactions =
                new ArrayList<>();

        Map<String, Object> t1 =
                new java.util.HashMap<>();

        t1.put("id", 1L);
        t1.put("timestamp", "2024-03-01 10:30:00");
        t1.put("type", "CREDIT");
        t1.put("amount", 5000.00);
        t1.put("description", "Salary Credit");
        t1.put("username", username);

        Map<String, Object> t2 =
                new java.util.HashMap<>();

        t2.put("id", 2L);
        t2.put("timestamp", "2024-02-28 15:45:00");
        t2.put("type", "DEBIT");
        t2.put("amount", 1200.00);
        t2.put("description", "ATM Withdrawal");
        t2.put("username", username);

        transactions.add(t1);
        transactions.add(t2);

        return transactions;
    }

    @PostMapping
    public Map<String, Object> addTransaction(
            @RequestBody Map<String, Object> transaction) {

        transaction.put("id", System.currentTimeMillis());

        return transaction;
    }
}