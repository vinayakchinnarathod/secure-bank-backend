package com.bank.securebank.controller;

import com.bank.securebank.model.Loan;
import com.bank.securebank.repository.LoanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = "*")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLoan(
            @RequestBody Map<String, Object> body) {

        Loan loan = new Loan();

        loan.setPrincipal(
                ((Number) body.get("principal")).doubleValue());

        loan.setInterestRate(
                ((Number) body.get("interestRate")).doubleValue());

        loan.setTenureMonths(
                (Integer) body.get("tenureMonths"));

        loan.setUsername((String) body.get("username"));

        loan.setStatus("PENDING");

        loan.setStartDate(LocalDate.now());

        double principal = loan.getPrincipal();

        double rate =
                loan.getInterestRate() / 100 / 12;

        int tenure = loan.getTenureMonths();

        double emi =
                principal * rate * Math.pow(1 + rate, tenure)
                        / (Math.pow(1 + rate, tenure) - 1);

        loan.setEmiAmount(emi);

        Loan savedLoan = loanRepository.save(loan);

        return ResponseEntity.ok(savedLoan);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getLoans(
            @RequestParam String username) {

        List<Loan> userLoans =
                loanRepository.findAll()
                        .stream()
                        .filter(loan ->
                                loan.getUsername().equals(username))
                        .toList();

        return ResponseEntity.ok(userLoans);
    }

    @GetMapping("/all-loans")
    public ResponseEntity<?> getAllLoans() {

        List<Loan> allLoans = loanRepository.findAll();

        return ResponseEntity.ok(allLoans);
    }
}