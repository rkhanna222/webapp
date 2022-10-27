package com.cloud.rest.webservices.webapp.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.cloud.rest.webservices.webapp.models.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface DocServices {

    default boolean isFilePresent(MultipartFile file) {
        if(file == null) return false;
        return true;
    }

    String writeFile(MultipartFile file, String localPath) throws Exception;

    Document addDocument(UUID id,MultipartFile imageFile, String localPath) throws Exception;

    boolean deleteFile(UUID doc_id) throws Exception;

}
