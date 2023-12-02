package ru.chemistry.models;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
public class DocumentFile {
    @Id
    private String id;
    private String filePath;

    public DocumentFile() {
    }

    public DocumentFile(String filePath) {
        this.filePath = filePath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
