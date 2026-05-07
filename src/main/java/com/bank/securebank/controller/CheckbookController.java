package com.bank.securebank.controller;

import com.bank.securebank.model.CheckbookRequest;
import com.bank.securebank.repository.CheckbookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkbook")
@CrossOrigin(origins = "http://localhost:3000")
public class CheckbookController {

    @Autowired
    private CheckbookRepository checkbookRepository;

    @PostMapping("/request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> requestCheckbook(
            @RequestBody Map<String, String> body) {

        String username = body.get("username");

        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username required");
        }

        // Create and save checkbook request
        CheckbookRequest request = new CheckbookRequest();
        request.setUsername(username);
        request.setAccountNumber("XXXX-" + System.currentTimeMillis() % 10000); // Generate mock account number
        request.setRequestDate(LocalDate.now());
        request.setStatus("PENDING");

        CheckbookRequest savedRequest = checkbookRepository.save(request);

        return ResponseEntity.ok("Checkbook requested successfully for " + username + ". Request ID: " + savedRequest.getId());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CheckbookRequest>> getAllCheckbookRequests() {
        List<CheckbookRequest> requests = checkbookRepository.findAll();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CheckbookRequest>> getUserCheckbookRequests(@PathVariable String username) {
        List<CheckbookRequest> requests = checkbookRepository.findAll().stream()
                .filter(req -> req.getUsername().equals(username))
                .toList();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveCheckbookRequest(@PathVariable Long id) {
        try {
            CheckbookRequest request = checkbookRepository.findById(id).orElse(null);
            if (request != null) {
                request.setStatus("APPROVED");
                checkbookRepository.save(request);
                
                Map<String, Object> response = new java.util.HashMap<>();
                response.put("success", true);
                response.put("message", "Checkbook request approved successfully");
                response.put("requestId", id);
                response.put("username", request.getUsername());
                response.put("status", request.getStatus());
                
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", "Checkbook request not found with ID: " + id
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                "success", false,
                "message", "Error approving checkbook request: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectCheckbookRequest(@PathVariable Long id) {
        try {
            CheckbookRequest request = checkbookRepository.findById(id).orElse(null);
            if (request != null) {
                request.setStatus("REJECTED");
                checkbookRepository.save(request);
                
                Map<String, Object> response = new java.util.HashMap<>();
                response.put("success", true);
                response.put("message", "Checkbook request rejected successfully");
                response.put("requestId", id);
                response.put("username", request.getUsername());
                response.put("status", request.getStatus());
                
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", "Checkbook request not found with ID: " + id
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                "success", false,
                "message", "Error rejecting checkbook request: " + e.getMessage()
            ));
        }
    }
}