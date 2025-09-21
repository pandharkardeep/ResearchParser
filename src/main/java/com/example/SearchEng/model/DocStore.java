package com.example.SearchEng.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "index_docstore")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DocStore {
    @Id
    private String id;


    private String title;
    private String abstractText;
    private List<String> authors;
    private List<String> topics;
    private String published;
    private String url;
    private String source;
}
