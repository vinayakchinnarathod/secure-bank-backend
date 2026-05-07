package com.bank.securebank.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final CheckbookRepository checkbookRepository;
    private final LoanRepository loanRepository;

    public AdminController(UserRepository userRepository, CheckbookRepository checkbookRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.checkbookRepository = checkbookRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/kyc")
    public ResponseEntity<List<Map<String, Object>>> getKyc() {
        List<Map<String, Object>> kycApplications = new ArrayList<>();
        
        // Get all users from database
        List<User> users = userRepository.findAll();
        System.out.println("=== DEBUG: Total users found: " + users.size());
        
        for (User user : users) {
            System.out.println("=== DEBUG: User: " + user.getUsername());
            System.out.println("=== DEBUG: KYC Status: " + user.getKycStatus());
            System.out.println("=== DEBUG: Aadhaar: " + user.getAadhaarNumber());
            System.out.println("=== DEBUG: PAN: " + user.getPanNumber());
            System.out.println("=== DEBUG: Address: " + user.getAddress());
            
            Map<String, Object> kycData = new HashMap<>();
            kycData.put("id", user.getId());
            kycData.put("username", user.getUsername());
            kycData.put("fullName", user.getFullName() != null ? user.getFullName() : user.getUsername());
            kycData.put("email", user.getEmail() != null ? user.getEmail() : "Not Provided");
            kycData.put("aadhaarNumber", user.getAadhaarNumber() != null ? user.getAadhaarNumber() : "Not Provided");
            kycData.put("panNumber", user.getPanNumber() != null ? user.getPanNumber() : "Not Provided");
            kycData.put("address", user.getAddress() != null ? user.getAddress() : "Not Provided");
            kycData.put("phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "Not Provided");
            kycData.put("status", user.getKycStatus() != null ? user.getKycStatus() : "PENDING");
            
            kycApplications.add(kycData);
        }
        
        System.out.println("=== DEBUG: Returning " + kycApplications.size() + " KYC applications");
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
            kycData.put("status", user.getKycStatus() != null ? user.getKycStatus() : "PENDING");
            return ResponseEntity.ok(kycData);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/kyc/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveKyc(@PathVariable Long id, @RequestBody Map<String, String> reviewData) {
        try {
            System.out.println("Attempting to approve KYC for user ID: " + id);
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                System.out.println("Found user: " + user.getUsername());
                user.setKycStatus("APPROVED");
                userRepository.save(user);
                System.out.println("Successfully updated KYC status to APPROVED");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "KYC approved successfully");
                response.put("kycId", user.getId());
                response.put("username", user.getUsername());
                response.put("status", user.getKycStatus());
                response.put("reviewedBy", reviewData.get("reviewedBy"));
                
                return ResponseEntity.ok(response);
            }
            System.out.println("User not found with ID: " + id);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "User not found with ID: " + id,
                "kycId", id
            ));
        } catch (Exception e) {
            System.out.println("Error approving KYC: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error approving KYC: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/kyc/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectKyc(@PathVariable Long id, @RequestBody Map<String, String> rejectionData) {
        try {
            System.out.println("Attempting to reject KYC for user ID: " + id);
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                System.out.println("Found user: " + user.getUsername());
                user.setKycStatus("REJECTED");
                userRepository.save(user);
                System.out.println("Successfully updated KYC status to REJECTED");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "KYC rejected successfully");
                response.put("kycId", user.getId());
                response.put("username", user.getUsername());
                response.put("status", user.getKycStatus());
                response.put("reviewedBy", rejectionData.get("reviewedBy"));
                response.put("rejectionReason", rejectionData.get("reason"));
                
                return ResponseEntity.ok(response);
            }
            System.out.println("User not found with ID: " + id);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "User not found with ID: " + id,
                "kycId", id
            ));
        } catch (Exception e) {
            System.out.println("Error rejecting KYC: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error rejecting KYC: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("=== ADMIN DEBUG: getAllUsers endpoint called");
        
        // Get current authentication
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("=== ADMIN DEBUG: Authenticated user: " + auth.getName());
            System.out.println("=== ADMIN DEBUG: User authorities: " + auth.getAuthorities());
        } else {
            System.out.println("=== ADMIN DEBUG: No authentication found!");
        }
        
        List<User> users = userRepository.findAll();
        System.out.println("=== DEBUG: Users endpoint called, found " + users.size() + " users");
        for (User user : users) {
            System.out.println("=== DEBUG: User - ID: " + user.getId() + ", Username: " + user.getUsername() + ", Role: " + user.getRole());
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        System.out.println("=== ADMIN DEBUG: testDatabase endpoint called");
        
        // Get current authentication
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("=== ADMIN DEBUG: Authenticated user: " + auth.getName());
            System.out.println("=== ADMIN DEBUG: User authorities: " + auth.getAuthorities());
        } else {
            System.out.println("=== ADMIN DEBUG: No authentication found!");
        }
        
        Map<String, Object> response = new HashMap<>();
        List<User> users = userRepository.findAll();
        response.put("totalUsers", users.size());
        response.put("users", users.stream().map(u -> Map.of(
            "id", u.getId(),
            "username", u.getUsername(),
            "role", u.getRole(),
            "aadhaar", u.getAadhaarNumber(),
            "pan", u.getPanNumber(),
            "address", u.getAddress()
        )).toList());
        response.put("authenticatedUser", auth != null ? auth.getName() : "none");
        response.put("authorities", auth != null ? auth.getAuthorities().toString() : "none");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkbook-requests")
    public ResponseEntity<List<Map<String, Object>>> getCheckbookRequests() {
        List<Map<String, Object>> requests = new ArrayList<>();
        
        // Get actual checkbook requests from database
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
        
        // Get actual loans from database
        List<Loan> loanList = loanRepository.findAll();
        
        for (Loan loan : loanList) {
            Map<String, Object> loanData = new HashMap<>();
            loanData.put("id", loan.getId());
            loanData.put("principal", loan.getPrincipal());
            loanData.put("interestRate", loan.getInterestRate());
            loanData.put("tenureMonths", loan.getTenureMonths());
            loanData.put("status", loan.getStatus());
            loanData.put("username", loan.getUsername());
            if (loan.getEmiAmount() != null) {
                loanData.put("emiAmount", loan.getEmiAmount());
            }
            loans.add(loanData);
        }
        
        return ResponseEntity.ok(loans);
    }
}