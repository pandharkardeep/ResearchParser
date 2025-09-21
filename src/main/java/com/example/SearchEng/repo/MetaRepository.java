package com.example.SearchEng.repo;

import com.example.SearchEng.model.Meta;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MetaRepository extends MongoRepository<Meta, String> {
}
