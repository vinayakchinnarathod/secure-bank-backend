// DocumentController.java

package com.bank.securebank.controller;

import com.bank.securebank.model.Document;
import com.bank.securebank.repository.DocumentRepository;
import com.bank.securebank.service.FileStorageService;

import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;

import org.springframework.util.LinkedMultiValueMap;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username,
            @RequestParam("documentType") String documentType) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Document> existingDocs =
                    documentRepository.findByUsernameAndDocumentType(
                            username,
                            documentType.toUpperCase()
                    );

            if (!existingDocs.isEmpty()) {

                response.put("success", false);
                response.put("message",
                        documentType + " document already uploaded.");

                return ResponseEntity.badRequest().body(response);
            }

            String filePath = fileStorageService.storeFile(
                    file,
                    username,
                    documentType.toUpperCase()
            );

            Document document = new Document();

            document.setUsername(username);
            document.setDocumentType(documentType.toUpperCase());
            document.setFileName(file.getOriginalFilename());
            document.setFilePath(filePath);
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setUploadStatus("UPLOADED");

            Document savedDocument = documentRepository.save(document);

            response.put("success", true);
            response.put("message", "Document uploaded successfully");
            response.put("document", savedDocument);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put("message",
                    "Failed to upload document: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Document>> getUserDocuments(
            @PathVariable String username) {

        List<Document> documents =
                documentRepository.findByUsername(username);

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long documentId) {

        try {

            Optional<Document> documentOpt =
                    documentRepository.findById(documentId);

            if (!documentOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Document document = documentOpt.get();

            if (!fileStorageService.fileExists(document.getFilePath())) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent =
                    fileStorageService.getFileContent(document.getFilePath());

            ByteArrayResource resource =
                    new ByteArrayResource(fileContent);

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.parseMediaType(
                                    document.getContentType()
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" +
                                    document.getFileName() + "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/view/{documentId}")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long documentId) {

        try {

            Optional<Document> documentOpt =
                    documentRepository.findById(documentId);

            if (!documentOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Document document = documentOpt.get();

            if (!fileStorageService.fileExists(document.getFilePath())) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent =
                    fileStorageService.getFileContent(document.getFilePath());

            ByteArrayResource resource =
                    new ByteArrayResource(fileContent);

            MediaType contentType;

            String fileName =
                    document.getFileName().toLowerCase();

            if (fileName.endsWith(".pdf")) {

                contentType = MediaType.APPLICATION_PDF;

            } else if (fileName.endsWith(".jpg")
                    || fileName.endsWith(".jpeg")) {

                contentType = MediaType.IMAGE_JPEG;

            } else if (fileName.endsWith(".png")) {

                contentType = MediaType.IMAGE_PNG;

            } else {

                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }

            return ResponseEntity.ok()
                    .contentType(contentType)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    document.getFileName() + "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/verify/{documentId}")
    public ResponseEntity<Map<String, Object>> verifyDocument(
            @PathVariable Long documentId,
            @RequestBody Map<String, String> verificationData) {

        Map<String, Object> response = new HashMap<>();

        try {

            Optional<Document> documentOpt =
                    documentRepository.findById(documentId);

            if (!documentOpt.isPresent()) {

                response.put("success", false);
                response.put("message", "Document not found");

                return ResponseEntity.notFound().build();
            }

            Document document = documentOpt.get();

            String status = verificationData.get("status");
            String comments = verificationData.get("comments");

            document.setUploadStatus(status.toUpperCase());
            document.setVerificationComments(comments);

            documentRepository.save(document);

            response.put("success", true);
            response.put("message",
                    "Document verification updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put("message",
                    "Failed to verify document: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Document>> getAllDocuments() {

        List<Document> documents = documentRepository.findAll();

        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> deleteDocument(
            @PathVariable Long documentId) {

        Map<String, Object> response = new HashMap<>();

        try {

            Optional<Document> documentOpt =
                    documentRepository.findById(documentId);

            if (!documentOpt.isPresent()) {

                response.put("success", false);
                response.put("message", "Document not found");

                return ResponseEntity.notFound().build();
            }

            Document document = documentOpt.get();

            try {

                Path filePath =
                        Paths.get("uploads")
                                .resolve(document.getFilePath())
                                .normalize();

                Files.deleteIfExists(filePath);

            } catch (Exception e) {
            }

            documentRepository.delete(document);

            response.put("success", true);
            response.put("message",
                    "Document deleted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put("message",
                    "Failed to delete document: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}