package ru.chemistry.service;

import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import ru.chemistry.models.DocumentFile;
import ru.chemistry.service.repository.DocumentRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<DocumentFile> getAllDocuments() {
        return documentRepository.findAll();
    }

    public DocumentFile addDocument(DocumentFile document) {
        return documentRepository.save(document);
    }

    public DocumentFile updateDocument(String id, DocumentFile updatedDocument) {
        return documentRepository.findById(id)
                .map(document -> {
                    document.setFilePath(updatedDocument.getFilePath());
                    return documentRepository.save(document);
                })
                .orElseGet(() -> {
                    updatedDocument.setId(id);
                    return documentRepository.save(updatedDocument);
                });
    }

    public void deleteDocument(String id) {
        documentRepository.deleteById(id);
    }

    public Optional<DocumentFile> getDocumentById(String id) {
        return documentRepository.findById(id);
    }

    public String getDocxContentAsHtml(String id) {
        Optional<DocumentFile> documentFile = documentRepository.findById(id);
        if (documentFile.isPresent()) {
            String filePath = documentFile.get().getFilePath();
            try (FileInputStream fis = new FileInputStream(new File(filePath));
                 XWPFDocument document = new XWPFDocument(OPCPackage.open(fis))) {
                XHTMLOptions options = XHTMLOptions.create();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                XHTMLConverter.getInstance().convert(document, baos, options);
                return new String(baos.toByteArray(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error converting document: " + e.getMessage();
            }
        }
        return "Document not found";
    }
}
