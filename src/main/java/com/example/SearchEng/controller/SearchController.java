package com.example.SearchEng.controller;

import com.example.SearchEng.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public Map<String,Object> search(@RequestParam String q,
                                     @RequestParam(required = false) Integer k,
                                     @RequestParam(required = false) String author,
                                     @RequestParam(required = false) String topic,
                                     @RequestParam(required = false, name="date_start") String dateStart,
                                     @RequestParam(required = false, name="date_end") String dateEnd,
                                     @RequestParam(required = false) String source) {
        return searchService.search(q, k, author, topic, dateStart, dateEnd, source);
    }
}