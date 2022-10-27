package com.cloud.rest.webservices.webapp.controllers;


import com.cloud.rest.webservices.webapp.models.Document;
import com.cloud.rest.webservices.webapp.models.User;
import com.cloud.rest.webservices.webapp.repositories.DocumentRepository;
import com.cloud.rest.webservices.webapp.repositories.UserRepository;
import com.cloud.rest.webservices.webapp.services.DocServices;
import com.cloud.rest.webservices.webapp.services.DocumentServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
public class DocumentController {

    @Autowired
    private DocumentServices documentServices;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DocumentController(DocumentServices documentServices) {
        this.documentServices = documentServices;
    }

    @PostMapping("/v1/documents")
    public ResponseEntity<?> addDocument(@RequestParam(required = false) MultipartFile file, HttpServletRequest request) throws Exception{

        String loggedUser = "";
        String username = "";
        String password = "";

        try {
            loggedUser = authenticatedUser(request);
            username = loggedUser.split(" ")[0];
            password = loggedUser.split(" ")[1];
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Error");
        }

        User user = userRepository.findByUsername(username);

        if(!documentServices.isFilePresent(file)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"error\": \"Select a file\" }");


        String localPath = request.getServletContext().getRealPath("/images/");
        Document doc = documentServices.addDocument(user.getId(), file, localPath);
        return ResponseEntity.status(HttpStatus.OK).body(doc);
    }

    @GetMapping("v1/documents")
    public ResponseEntity<?> getFiles(HttpServletRequest request) {
        String logged = "";
        User user = null;
        String password = "";
        try {
            logged = authenticatedUser(request);
            String loggedUser = logged.split(" ")[0];
            password = logged.split(" ")[1];
            user = userRepository.findByUsername(loggedUser);
        }
        catch (Exception e){
            return new ResponseEntity("No Auth in GET", HttpStatus.UNAUTHORIZED);
        }
        if(!passwordEncoder.matches(password, user.getPassword())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to access");
        }
        List<Document> myList = documentServices.getAllDocument();
        ArrayList<Document> mydocumnets = new ArrayList<>();
        for (int i = 0; i<myList.size(); i++)
        {
            if (user.getId().toString().equals(myList.get(i).getUser_id().toString())){
                mydocumnets.add(myList.get(i));
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(mydocumnets);
    }

    private String authenticatedUser(HttpServletRequest request){

        String tokenEnc = request.getHeader("Authorization").split(" ")[1];
        byte[] token = Base64.getDecoder().decode(tokenEnc);
        String decodedStr = new String(token, StandardCharsets.UTF_8);

        String userName = decodedStr.split(":")[0];
        String passWord = decodedStr.split(":")[1];
        System.out.println("Value of Token" + " "+ decodedStr);

        return (userName + " " + passWord);

    }

    @GetMapping("v1/documents/{doc_id}")
    public ResponseEntity<?> getDocumentID(@PathVariable("doc_id") UUID doc_id, HttpServletRequest request)
    {
        Document document = null;
        String logged = "";
        User user = null;
        String password = "";
        String loggedUser ="";
        try {
            logged = authenticatedUser(request);
            loggedUser = logged.split(" ")[0];
            password = logged.split(" ")[1];
            user = userRepository.findByUsername(loggedUser);
        }
        catch (Exception e){
            return new ResponseEntity("No Auth in GET", HttpStatus.UNAUTHORIZED);
        }
        if(!passwordEncoder.matches(password, user.getPassword()) && !((user.getUsername().equals(loggedUser)))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to access");
        }
        document = documentRepository.findDocumentByDoc_id(doc_id);
        UUID userId = document.getUser_id();

        if (!user.getId().toString().equals((userId).toString())){
            return new ResponseEntity<>("DENY",HttpStatus.FORBIDDEN);
        }
        List<Document> myList = documentServices.getAllDocument();
        ArrayList<Document> mydocumnets = new ArrayList<>();
        for (int i = 0; i<myList.size(); i++)
        {
            if (user.getId().toString().equals(myList.get(i).getUser_id().toString()) &&
                    myList.get(i).getDoc_id().equals(doc_id)){
                mydocumnets.add(myList.get(i));
            }

        }
        if (mydocumnets.isEmpty()){
            ResponseEntity.status(HttpStatus.OK).body("No Data Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(mydocumnets);
    }

    @DeleteMapping(value = "v1/documents/{doc_id}")
    public ResponseEntity<?> deleteFile(@PathVariable("doc_id") UUID doc_id, HttpServletRequest request) throws Exception {

        Document document = null;
        String logged = "";
        String loggedUser ="";
        User user = null;
        String password = "";
        try {
            logged = authenticatedUser(request);
            loggedUser = logged.split(" ")[0];
            password = logged.split(" ")[1];
            user = userRepository.findByUsername(loggedUser);
        }
        catch (Exception e){
            return new ResponseEntity("No Auth in GET", HttpStatus.UNAUTHORIZED);
        }

        if(!passwordEncoder.matches(password, user.getPassword()) && !((user.getUsername().equals(loggedUser)))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to access");
        }

        document = documentRepository.findDocumentByDoc_id(doc_id);
        UUID userId = document.getUser_id();

        if (!user.getId().toString().equals((userId).toString())){
            return new ResponseEntity<>("DENY",HttpStatus.FORBIDDEN);
        }

        if (document == null){
            return new ResponseEntity<>("No Record Found",HttpStatus.NOT_FOUND);
        }


        boolean isRemoved = documentServices.deleteFile(doc_id);

        return new ResponseEntity<>("Document is deleted" + " " + doc_id, HttpStatus.OK);

    }
}
