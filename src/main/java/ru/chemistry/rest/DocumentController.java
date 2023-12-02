package ru.chemistry.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.chemistry.models.DocumentFile;
import ru.chemistry.service.DocumentService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public List<DocumentFile> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getDocumentById(@PathVariable String id) {
        String htmlContent = documentService.getDocxContentAsHtml(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8");
        return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
    }


    @PostMapping
    public DocumentFile addDocument(@RequestBody DocumentFile document) {
        return documentService.addDocument(document);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentFile> updateDocument(@PathVariable String id, @RequestBody DocumentFile document) {
        return documentService.getDocumentById(id)
                .map(existingDocument -> ResponseEntity.ok(documentService.updateDocument(id, document)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        return documentService.getDocumentById(id)
                .map(document -> {
                    documentService.deleteDocument(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
