package com.angadi.springresourceserver.data.repository;

import com.angadi.springresourceserver.data.entities.GDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;


@Repository
public interface GDocumentRepository extends JpaRepository<GDocument,Long> {
    Iterable<GDocument> findGDocumentByName(String documentName);
    Iterable<GDocument> findGdocumentByUserid(Long userId);
    Iterable<GDocument> findGDocumentByUseridAndName(Long userID,String documentName);
    Iterable<GDocument> findGDocumentByUseridAndType(Long userID,String documentName);
    Iterable<GDocument> findGDocumentByUseridAndTypeIn(Long userID, Collection<String> documentName);



}
