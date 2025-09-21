package com.example.SearchEng.repo;

import com.example.SearchEng.model.RawDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RawDocumentRepository extends MongoRepository<RawDocument, String> {
}
