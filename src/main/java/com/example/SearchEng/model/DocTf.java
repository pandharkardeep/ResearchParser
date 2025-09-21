package com.example.SearchEng.model;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
@Getter
@Setter
public class DocTf {
    private String docId;
    private int tf;
}
