package com.example.SearchEng.repo;

import com.example.SearchEng.model.DocStore;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocStoreRepository extends MongoRepository<DocStore, String> {
}
