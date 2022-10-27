package com.cloud.rest.webservices.webapp.repositories;

import com.cloud.rest.webservices.webapp.models.Document;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface DocumentRepository extends CrudRepository<Document, UUID> {

    @Query("SELECT a FROM Document a WHERE a.doc_id = :doc_id")
    Document findDocumentByDoc_id(UUID doc_id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Document e WHERE e.doc_id = :doc_id")
    void deleteByDoc_id(UUID doc_id);
}
