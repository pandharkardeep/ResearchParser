package com.example.SearchEng.controller;

import com.example.SearchEng.service.IndexService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {
    private final IndexService idxSer;
    public IndexController(IndexService idxSer) {
        this.idxSer = idxSer;
    }
    @PostMapping("/rebuild")
    public String rebuild() {
        var res = idxSer.rebuildIndex();
        return "Indexed " + res.docs() + " docs; avgdl=" + String.format("%.2f", res.avgdl());
    }
}