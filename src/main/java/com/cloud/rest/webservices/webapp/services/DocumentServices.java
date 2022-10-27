package com.cloud.rest.webservices.webapp.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.cloud.rest.webservices.webapp.models.Document;
import com.cloud.rest.webservices.webapp.repositories.DocumentRepository;
import com.cloud.rest.webservices.webapp.security.AmazonS3Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServices implements DocServices {

    @Autowired
    private DocumentRepository documentRepository;
    @Value("${bucket.name}")
    private String BUCKET_NAME;

    @Autowired
    private AmazonS3 s3;


    public List<Document> getAllDocument() {
        return (List<Document>) documentRepository.findAll();
    }



    @Override
    public String writeFile(MultipartFile file,String localPath) throws Exception {
        String fileNameEdited =  file.getOriginalFilename() + "-" + System.currentTimeMillis();
        //String fileName = file.getOriginalFilename();
        File nfile = multipartToFile(file, fileNameEdited);
        try {
            s3.putObject(BUCKET_NAME, fileNameEdited, nfile);
        } catch (AmazonServiceException exc) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exc.printStackTrace(pw);
            //LOGGER.error(exc.getMessage()+sw.toString());
        }
        return fileNameEdited;
    }

    public static File multipartToFile(MultipartFile mf, String fileName) throws IllegalStateException, IOException {
        File file = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        mf.transferTo(file);
        return file;
    }

    @Override
    public Document addDocument(UUID user_id,MultipartFile file, String localPath) throws Exception {
        String fileName = writeFile(file, localPath);

        Document doc = new Document();
        doc.setUser_id(user_id);
        doc.setDocName(fileName);
        doc.setDateCreated(LocalDateTime.now());
        doc.setPath(String.valueOf(s3.getUrl(BUCKET_NAME,fileName)));
        documentRepository.save(doc);
        //return getPresignedUrl(book.getImage().getId());
        return doc;
    }

    @Override
    public boolean deleteFile(UUID doc_id) throws Exception {
        Document doc = documentRepository.findDocumentByDoc_id(doc_id);
        String fileName = doc.getDocName();
        s3.deleteObject(BUCKET_NAME, fileName);
        documentRepository.deleteByDoc_id(doc_id);
        return true;
    }




}
