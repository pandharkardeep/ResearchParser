package com.example.SearchEng.service;

import com.example.SearchEng.model.*;
import com.example.SearchEng.repo.DocStoreRepository;
import com.example.SearchEng.repo.MetaRepository;
import com.example.SearchEng.repo.PostingRepository;
import com.example.SearchEng.repo.RawDocumentRepository;
import com.example.SearchEng.util.TextUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IndexService {
    private final RawDocumentRepository RawRepo;
    private final DocStoreRepository docRepo;
    private final PostingRepository postingRepo;
    private final MetaRepository metaRepo;
    public record BuildResult(int docs, double avgdl) {}
    public BuildResult rebuildIndex() {
        List<RawDocument> raw = RawRepo.findAll();
        Map<String, RawDocument> byId =  new HashMap<>();
        for(var d : raw) byId.put(d.getDocId(), d);
        List<DocStore> docs = new ArrayList<>();
        for(var d : byId.values()) {
            docs.add(DocStore.builder()
                    .id(d.getDocId()).title(d.getTitle()).abstractText(d.getAbstractText())
                    .authors(d.getAuthors()).topics(d.getTopics())
                    .published(d.getPublished()).url(d.getUrl()).source(d.getSource())
                    .build()
            );
        }
        docRepo.deleteAll();
        docRepo.saveAll(docs);
        Map<String, List<DocTf>> postings = new HashMap<>();
        Map<String, Integer> docLen = new HashMap<>();
        int N = 0;
        long sumLen = 0;

        for(DocStore doc : docs){
            Map<String, Integer> tfCounter = new HashMap<>();
            int length = 0;
            length += addField(tfCounter, doc.getTitle(), 3.0);
            length += addField(tfCounter, doc.getAbstractText(), 1.0);
            for(var e : tfCounter.entrySet()) {
                String term = e.getKey();
                int tf = e.getValue();
                postings.computeIfAbsent(term, k -> new ArrayList<>()).add(new DocTf(doc.getId(), tf));
            }
            docLen.put(doc.getId(), Math.max(1,length));
            sumLen += length;
            N++;

        }
        double avgdl = (N == 0) ? 1.0 : (sumLen * 1.0 / N);
        postingRepo.deleteAll();
        List<Posting> batch = new ArrayList<>(2000);
        for(var entry : postings.entrySet()) {
            batch.add(Posting.builder().t(entry.getKey()).p(entry.getValue()).build());
            if(batch.size() >= 2000) {
                postingRepo.saveAll(batch);
                batch.clear();
            }
        }
        if(!batch.isEmpty()) postingRepo.saveAll(batch);
        metaRepo.deleteAll();
        metaRepo.save(Meta.builder().key("meta").N(N).avgdl(avgdl).docLen(docLen).build());
        return new BuildResult(N, avgdl);
    }

    private int addField(Map<String, Integer> tfCounter, String text, double weight){
        int tokens = 0;
        for(String t : TextUtil.tokenize(text, true)){
            int add = (int) weight;
            tfCounter.put(t, tfCounter.getOrDefault(t, 0) + add);
            tokens++;
        }
        return tokens;
    }
}
