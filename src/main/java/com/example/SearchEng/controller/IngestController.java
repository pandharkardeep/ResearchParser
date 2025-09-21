package com.example.SearchEng.controller;

import com.example.SearchEng.service.IngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingest")
@RequiredArgsConstructor
public class IngestController {
    private final IngestService ingestService;

    @PostMapping("/arxiv")
    public String ingestArxiv(@RequestParam(defaultValue = "cat:cs.IR") String query,
                              @RequestParam(defaultValue = "1000") int maxResults,
                              @RequestParam(defaultValue = "100") int batch,
                              @RequestParam(defaultValue = "800") long sleepMillis) throws Exception {
        int n = ingestService.ingestArxiv(query, maxResults, batch, sleepMillis);
        return "Ingested " + n + " arXiv docs";
    }
}
