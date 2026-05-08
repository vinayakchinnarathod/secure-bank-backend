package com.bank.securebank.controller;

import com.bank.securebank.service.AutoKYCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/autokyc")
@CrossOrigin(origins = "*")
public class AutoKYCController {

    @Autowired
    private AutoKYCService autoKYCService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("service_healthy", autoKYCService.isServiceHealthy());
        response.put("timestamp", new java.util.Date().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-document")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("document_type") String documentType) {

        try {
            Map<String, Object> result =
                    autoKYCService.uploadDocument(file, documentType);

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Document upload failed: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/analyze-document")
    public ResponseEntity<Map<String, Object>> analyzeDocument(
            @RequestBody Map<String, String> request) {

        try {

            String filePath = request.get("file_path");

            Map<String, Object> result =
                    autoKYCService.analyzeDocument(filePath);

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Document analysis failed: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/face-verification")
    public ResponseEntity<Map<String, Object>> verifyFace(
            @RequestParam("face_image") MultipartFile faceImage,
            @RequestParam(value = "liveness_check", defaultValue = "true")
            boolean livenessCheck) {

        try {

            Map<String, Object> result =
                    autoKYCService.verifyFace(faceImage, livenessCheck);

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Face verification failed: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/liveness-session")
    public ResponseEntity<Map<String, Object>> createLivenessSession(
            @RequestBody Map<String, Object> request) {

        try {

            String deviceCorrelationId =
                    (String) request.get("deviceCorrelationId");

            Map<String, Object> result =
                    autoKYCService.createLivenessSession(deviceCorrelationId);

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Liveness session creation failed: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/complete-verification")
    public ResponseEntity<Map<String, Object>> performCompleteVerification(
            @RequestParam("document_file") MultipartFile documentFile,
            @RequestParam("document_type") String documentType,
            @RequestParam("user_id") String userId,
            @RequestParam(value = "face_image", required = false)
            MultipartFile faceImage) {

        try {

            Map<String, Object> result =
                    autoKYCService.performCompleteVerification(
                            documentFile,
                            documentType,
                            userId,
                            faceImage
                    );

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Complete verification failed: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Map<String, Object>>> getAllCustomers() {

        try {

            List<Map<String, Object>> customers =
                    autoKYCService.getAllCustomers();

            return ResponseEntity.ok(customers);

        } catch (Exception e) {

            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomer(
            @PathVariable String customerId) {

        try {

            Map<String, Object> customer =
                    autoKYCService.getCustomer(customerId);

            return ResponseEntity.ok(customer);

        } catch (Exception e) {

            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Failed to get customer: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/update-customer")
    public ResponseEntity<Map<String, Object>> updateCustomer(
            @RequestBody Map<String, Object> customerData) {

        try {

            Map<String, Object> result =
                    autoKYCService.updateCustomer(customerData);

            return ResponseEntity.ok(result);

        } catch (Exception e) {

            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message",
                    "Failed to update customer: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}