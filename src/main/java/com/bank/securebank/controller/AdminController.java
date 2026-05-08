// AdminController.java

package com.bank.securebank.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bank.securebank.model.User;
import com.bank.securebank.model.CheckbookRequest;
import com.bank.securebank.model.Loan;

import com.bank.securebank.repository.UserRepository;
import com.bank.securebank.repository.CheckbookRepository;
import com.bank.securebank.repository.LoanRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final CheckbookRepository checkbookRepository;
    private final LoanRepository loanRepository;

    public AdminController(UserRepository userRepository,
                           CheckbookRepository checkbookRepository,
                           LoanRepository loanRepository) {

        this.userRepository = userRepository;
        this.checkbookRepository = checkbookRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/kyc")
    public ResponseEntity<List<Map<String, Object>>> getKyc() {

        List<Map<String, Object>> kycApplications = new ArrayList<>();

        List<User> users = userRepository.findAll();

        for (User user : users) {

            Map<String, Object> kycData = new HashMap<>();

            kycData.put("id", user.getId());
            kycData.put("username", user.getUsername());
            kycData.put("fullName", user.getFullName());
            kycData.put("email", user.getEmail());
            kycData.put("aadhaarNumber", user.getAadhaarNumber());
            kycData.put("panNumber", user.getPanNumber());
            kycData.put("address", user.getAddress());
            kycData.put("phoneNumber", user.getPhoneNumber());
            kycData.put("status", user.getKycStatus());

            kycApplications.add(kycData);
        }

        return ResponseEntity.ok(kycApplications);
    }

    @GetMapping("/kyc/{id}")
    public ResponseEntity<Map<String, Object>> getKycById(@PathVariable Long id) {

        User user = userRepository.findById(id).orElse(null);

        if (user != null) {

            Map<String, Object> kycData = new HashMap<>();

            kycData.put("id", user.getId());
            kycData.put("username", user.getUsername());
            kycData.put("fullName", user.getFullName());
            kycData.put("email", user.getEmail());
            kycData.put("aadhaarNumber", user.getAadhaarNumber());
            kycData.put("panNumber", user.getPanNumber());
            kycData.put("address", user.getAddress());
            kycData.put("phoneNumber", user.getPhoneNumber());
            kycData.put("status", user.getKycStatus());

            return ResponseEntity.ok(kycData);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/kyc/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveKyc(
            @PathVariable Long id,
            @RequestBody Map<String, String> reviewData) {

        User user = userRepository.findById(id).orElse(null);

        if (user != null) {

            user.setKycStatus("APPROVED");
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();

            response.put("success", true);
            response.put("message", "KYC approved successfully");

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "User not found"
        ));
    }

    @PostMapping("/kyc/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectKyc(
            @PathVariable Long id,
            @RequestBody Map<String, String> rejectionData) {

        User user = userRepository.findById(id).orElse(null);

        if (user != null) {

            user.setKycStatus("REJECTED");
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();

            response.put("success", true);
            response.put("message", "KYC rejected successfully");

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "User not found"
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = userRepository.findAll();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testDatabase() {

        Map<String, Object> response = new HashMap<>();

        List<User> users = userRepository.findAll();

        response.put("totalUsers", users.size());
        response.put("users", users);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkbook-requests")
    public ResponseEntity<List<Map<String, Object>>> getCheckbookRequests() {

        List<Map<String, Object>> requests = new ArrayList<>();

        List<CheckbookRequest> checkbookRequests = checkbookRepository.findAll();

        for (CheckbookRequest request : checkbookRequests) {

            Map<String, Object> requestData = new HashMap<>();

            requestData.put("id", request.getId());
            requestData.put("accountNumber", request.getAccountNumber());
            requestData.put("requestDate", request.getRequestDate().toString());
            requestData.put("status", request.getStatus());
            requestData.put("username", request.getUsername());

            requests.add(requestData);
        }

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/loans")
    public ResponseEntity<List<Map<String, Object>>> getLoans() {

        List<Map<String, Object>> loans = new ArrayList<>();

        List<Loan> loanList = loanRepository.findAll();

        for (Loan loan : loanList) {

            Map<String, Object> loanData = new HashMap<>();

            loanData.put("id", loan.getId());
            loanData.put("principal", loan.getPrincipal());
            loanData.put("interestRate", loan.getInterestRate());
            loanData.put("tenureMonths", loan.getTenureMonths());
            loanData.put("status", loan.getStatus());
            loanData.put("username", loan.getUsername());

            loans.add(loanData);
        }

        return ResponseEntity.ok(loans);
    }
}