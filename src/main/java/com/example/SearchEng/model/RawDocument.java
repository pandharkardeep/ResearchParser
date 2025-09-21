package com.example.SearchEng.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Document(collection = "raw_documents")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Getter
@Setter
public class RawDocument {
    @Id
    private String docId;
    private String title;
    private String abstractText;
    private List<String> authors;
    private List<String> topics;
    private String published;
    private String url;
    private String source;

}
