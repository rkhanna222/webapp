package com.cloud.rest.webservices.webapp.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID doc_id;

    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID user_id;

    @JsonProperty("name")
    private String docName;

    @JsonProperty("s3_bucket_path")
    private String path;

    @JsonProperty(value = "date_created",access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dateCreated;

    public Document() {
    }

    public Document(UUID user_id, String docName, String path, LocalDateTime dateCreated) {
        this.user_id = user_id;
        this.docName = docName;
        this.path = path;
        this.dateCreated = dateCreated;
    }

    public UUID getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(UUID doc_id) {
        this.doc_id = doc_id;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }
}
