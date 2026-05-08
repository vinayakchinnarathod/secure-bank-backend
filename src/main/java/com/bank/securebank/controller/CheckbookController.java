package com.bank.securebank.controller;

import com.bank.securebank.model.CheckbookRequest;
import com.bank.securebank.repository.CheckbookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkbook")
@CrossOrigin(origins = "*")
public class CheckbookController {

    @Autowired
    private CheckbookRepository checkbookRepository;

    @PostMapping("/request")
    public ResponseEntity<?> requestCheckbook(
            @RequestBody Map<String, String> body) {

        String username = body.get("username");

        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username required");
        }

        CheckbookRequest request = new CheckbookRequest();

        request.setUsername(username);
        request.setAccountNumber(
                "XXXX-" + System.currentTimeMillis() % 10000);

        request.setRequestDate(LocalDate.now());
        request.setStatus("PENDING");

        CheckbookRequest savedRequest =
                checkbookRepository.save(request);

        return ResponseEntity.ok(
                "Checkbook requested successfully for "
                        + username
                        + ". Request ID: "
                        + savedRequest.getId()
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<CheckbookRequest>> getAllCheckbookRequests() {

        List<CheckbookRequest> requests =
                checkbookRepository.findAll();

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<CheckbookRequest>> getUserCheckbookRequests(
            @PathVariable String username) {

        List<CheckbookRequest> requests =
                checkbookRepository.findAll()
                        .stream()
                        .filter(req -> req.getUsername().equals(username))
                        .toList();

        return ResponseEntity.ok(requests);
    }
}