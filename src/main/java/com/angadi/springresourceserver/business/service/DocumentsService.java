package com.angadi.springresourceserver.business.service;


import com.angadi.springresourceserver.business.domain.Document;
import com.angadi.springresourceserver.business.domain.UsersDomain;
import com.angadi.springresourceserver.data.entities.GDocument;
import com.angadi.springresourceserver.data.entities.Users;
import com.angadi.springresourceserver.data.repository.GDocumentRepository;
import com.angadi.springresourceserver.data.repository.UserRepository;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class DocumentsService {


    public final GDocumentRepository gDocumentRepository;
    public final UserRepository userRepository;


    @Autowired
    public DocumentsService(GDocumentRepository gDocumentRepository, UserRepository userRepository){
        this.gDocumentRepository = gDocumentRepository;
        this.userRepository = userRepository;
    }

    public List<Document> getDocumentsByName(String documentName,@AuthenticationPrincipal Jwt jwt){

        UsersDomain user = null;
        if(jwt.getClaims().get("name")!=null){
            user = this.getUserDetails((String) jwt.getClaims().get("name"));
        }

        if(documentName.isEmpty() || documentName.toLowerCase().equals("get all")){
            return this.documentsMapping(gDocumentRepository.findGdocumentByUserid(user.getUserID()));
        }
        else {
            return this.documentsMapping(gDocumentRepository.findGDocumentByUseridAndName(user.getUserID(),documentName));
        }



    }


    public List<Document> getDocumentsByType(@Nullable Collection<String> documentType, @AuthenticationPrincipal Jwt jwt){

        UsersDomain user = null;
        if(jwt.getClaims().get("name")!=null){
            user = this.getUserDetails((String) jwt.getClaims().get("name"));
        assert user != null;
        if(documentType == null || documentType.contains("Get All")){
            return this.documentsMapping(gDocumentRepository.findGdocumentByUserid(user.getUserID()));
        }
        else {

            return this.documentsMapping(gDocumentRepository.findGDocumentByUseridAndTypeIn(user.getUserID(),documentType));
        }

        }
        return null;


    }

    public List<Document> documentsMapping(Iterable<GDocument> gDocuments){
        List<Document> documentsList = new ArrayList<>();
        gDocuments.forEach( gDocument -> {

            Document document = new Document();
            document.setDocName(gDocument.getName());
            document.setDocLocation(gDocument.getLocation());
            document.setDocYear(gDocument.getYear());
            document.setDocType(gDocument.getType());
            document.setDocValidity(gDocument.getValidity());
            documentsList.add(document);
        });
        return documentsList;
    }

    public UsersDomain getUserDetails(String userName){

        Iterable<Users> users = userRepository.findUsersByUserName(userName);
        UsersDomain userDomain = StreamSupport.stream(users.spliterator(),false)
                .findFirst()
                .map ( o1 -> new UsersDomain(o1.getUserName(),o1.getUserId())).orElseGet(null);

        return userDomain;
    }

}
