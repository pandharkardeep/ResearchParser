package com.example.SearchEng.repo;

import com.example.SearchEng.model.Posting;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostingRepository extends MongoRepository<Posting, String> {
}
