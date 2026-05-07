package com.bank.securebank.controller;

import com.bank.securebank.model.Loan;
import com.bank.securebank.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = "http://localhost:3000")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> applyLoan(@RequestBody Map<String, Object> body) {

        Loan loan = new Loan();
        loan.setPrincipal(((Number) body.get("principal")).doubleValue());
        loan.setInterestRate(((Number) body.get("interestRate")).doubleValue());
        loan.setTenureMonths((Integer) body.get("tenureMonths"));
        loan.setUsername((String) body.get("username"));
        loan.setStatus("PENDING");
        loan.setStartDate(LocalDate.now());
        
        // Calculate EMI (simple calculation)
        double principal = loan.getPrincipal();
        double rate = loan.getInterestRate() / 100 / 12; // monthly rate
        int tenure = loan.getTenureMonths();
        double emi = principal * rate * Math.pow(1 + rate, tenure) / (Math.pow(1 + rate, tenure) - 1);
        loan.setEmiAmount(emi);

        Loan savedLoan = loanRepository.save(loan);

        return ResponseEntity.ok(savedLoan);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getLoans(@RequestParam String username) {
        List<Loan> userLoans = loanRepository.findAll().stream()
                .filter(loan -> loan.getUsername().equals(username))
                .toList();

        return ResponseEntity.ok(userLoans);
    }

    @GetMapping("/all-loans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllLoans() {
        List<Loan> allLoans = loanRepository.findAll();
        return ResponseEntity.ok(allLoans);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveLoan(@PathVariable Long id) {
        try {
            Loan loan = loanRepository.findById(id).orElse(null);
            if (loan != null) {
                loan.setStatus("APPROVED");
                loanRepository.save(loan);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Loan approved successfully");
                response.put("loanId", id);
                response.put("username", loan.getUsername());
                response.put("status", loan.getStatus());
                response.put("principal", loan.getPrincipal());
                response.put("emiAmount", loan.getEmiAmount());
                
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Loan not found with ID: " + id
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error approving loan: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectLoan(@PathVariable Long id) {
        try {
            Loan loan = loanRepository.findById(id).orElse(null);
            if (loan != null) {
                loan.setStatus("REJECTED");
                loanRepository.save(loan);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Loan rejected successfully");
                response.put("loanId", id);
                response.put("username", loan.getUsername());
                response.put("status", loan.getStatus());
                
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Loan not found with ID: " + id
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error rejecting loan: " + e.getMessage()
            ));
        }
    }
}