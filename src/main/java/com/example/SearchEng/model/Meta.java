package com.example.SearchEng.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "index_meta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Meta {
    @Id
    private String key;
    private int N;
    private double avgdl;
    private Map<String, Integer> docLen;

}
