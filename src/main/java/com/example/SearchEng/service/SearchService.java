package com.example.SearchEng.service;

import com.example.SearchEng.model.DocStore;
import com.example.SearchEng.model.DocTf;
import com.example.SearchEng.model.Meta;
import com.example.SearchEng.model.Posting;
import com.example.SearchEng.repo.DocStoreRepository;
import com.example.SearchEng.repo.MetaRepository;
import com.example.SearchEng.repo.PostingRepository;
import com.example.SearchEng.util.SnippetUtil;
import com.example.SearchEng.util.TextUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final DocStoreRepository docRepo;
    private final PostingRepository postRepo;
    private final MetaRepository metaRepo;

    @Value("${app.bm25.k1:1.2}") private double k1;
    @Value("${app.bm25.k1:1.2}") private double b;

    private Meta metaCache = null;
    private Map<String, DocStore> docCache = null;
    private final Map<String, List<DocTf>> postingsCache = new HashMap<>();

    private Meta meta(){
        if(metaCache == null) metaCache = metaRepo.findById("meta").orElse(null);
        return metaCache;
    }

    private Map<String, DocStore> docs() {
        if(docCache == null){
            docCache = docRepo.findAll().stream().collect(Collectors.toMap(DocStore::getId, d -> d));
        }
        return docCache;
    }

    private List<DocTf> postings(String term) {
        return postingsCache.computeIfAbsent(term, t -> postRepo.findById(t).map(Posting::getP).orElse(List.of()));
    }

    public Map<String, Object> search(String q, Integer k,
                                      String author, String topic, String dateStart, String dateEnd, String source) {
        if(k == null || k <= 0) k = 20;
        List<String> terms = TextUtil.tokenize(q, true);
        if(terms.isEmpty())
            return Map.of("hits", List.of(), "facets", Map.of());

        Meta m = meta();
        if(m == null || m.getN() == 0)
            return Map.of("hits", List.of(), "facets", Map.of());

        Map<String, Double> scores = new HashMap<>();
        Map<String, Integer> dfCache = new HashMap<>();

        for(String t : terms) {
            List<DocTf> plist = postings(t);
            if(plist.isEmpty()) continue;
            int df = plist.size();
            dfCache.put(t, df);
            for(DocTf pair : plist){
                String did = pair.getDocId();
                int tf = pair.getTf();
                int dl = m.getDocLen().getOrDefault(did, 1);
                double sc = bm25(tf, df, m.getN(), dl, m.getAvgdl());
                scores.put(did, scores.getOrDefault(did, 0.0) + sc);
            }
        }

        List<Map.Entry<String, Double>> top = scores.entrySet().stream()
                .sorted((a, b2) -> Double.compare(b2.getValue(), a.getValue()))
                .limit(k* 4L).toList();
        List<Map<String, Object>> ranked = new ArrayList<>();
        for(var e : top) {
            String did = e.getKey();
            DocStore doc = docs().get(did);
            if(doc == null) continue;
            if(!passes(doc, author, topic, dateStart, dateEnd, source)) continue;
            ranked.add(Map.of(
                    "doc_id", did,
                    "score", round4(e.getValue()),
                    "title", doc.getTitle(),
                    "published", doc.getPublished(),
                    "authors", doc.getAuthors(),
                    "topics", doc.getTopics(),
                    "url", doc.getUrl(),
                    "snippet", SnippetUtil.makeSnippet(doc.getTitle(), doc.getAbstractText(), terms, 180)
            ));
            if(ranked.size() >= k) break;
        }

        Map<String, Object> facets = computeFacets(top.stream().map(Map.Entry::getKey).toList());
        return Map.of("hits", ranked, "facets", facets);
    }

    private boolean passes(DocStore d, String author, String topic, String start, String end, String source) {
        if (author != null && (d.getAuthors() == null || !d.getAuthors().contains(author))) return false;
        if (topic  != null && (d.getTopics()  == null || !d.getTopics().contains(topic))) return false;
        if (source != null && !source.equals(d.getSource())) return false;
        if ((start != null && !start.isBlank()) || (end != null && !end.isBlank())) {
            try {
                if (d.getPublished() != null && !d.getPublished().isBlank()) {
                    LocalDate dt = LocalDate.parse(d.getPublished());
                    if (start != null && !start.isBlank() && dt.isBefore(LocalDate.parse(start))) return false;
                    if (end   != null && !end.isBlank()   && dt.isAfter(LocalDate.parse(end))) return false;
                }
            } catch (Exception ignored) {}
        }
        return true;
    }

    private Map<String,Object> computeFacets(List<String> candidateIds) {
        Map<String, Integer> facAuth = new HashMap<>();
        Map<String, Integer> facTopic = new HashMap<>();
        Map<String, Integer> facYear = new HashMap<>();
        for (String did : candidateIds) {
            DocStore d = docs().get(did);
            if (d == null) continue;
            if (d.getAuthors() != null) d.getAuthors().forEach(a -> facAuth.put(a, facAuth.getOrDefault(a,0)+1));
            if (d.getTopics()  != null) d.getTopics().forEach(t -> facTopic.put(t, facTopic.getOrDefault(t,0)+1));
            String p = d.getPublished();
            if (p != null && p.length() >= 4) {
                String y = p.substring(0,4);
                facYear.put(y, facYear.getOrDefault(y,0)+1);
            }
        }
        return Map.of(
                "author", topN(facAuth, 10),
                "topic",  topN(facTopic, 10),
                "year",   topN(facYear, 10)
        );
    }

    private List<List<Serializable>> topN(Map<String,Integer> map, int n) {
        List<Map.Entry<String, Integer>> toSort = new ArrayList<>();
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            toSort.add(e);
        }
        toSort.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        List<List<Serializable>> list = new ArrayList<>();
        long limit = n;
        for (Map.Entry<String, Integer> e : toSort) {
            if (limit-- == 0) break;
            List<? extends Serializable> key = List.of(e.getKey(), e.getValue());
            list.add((List<Serializable>) key);
        }
        return list;
    }

    private double bm25(int tf, int df, int N, int dl, double avgdl) {
        double idf = Math.log(1.0 + (N - df + 0.5)/(df + 0.5));
        double denom = tf + k1 * (1 - b + b * dl / avgdl);
        return idf * (tf * (k1 + 1.0)) / denom;
    }

    private double round4(double v) { return Math.round(v * 10000.0) / 10000.0; }


}
