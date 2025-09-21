package com.example.SearchEng.service;

import com.example.SearchEng.model.RawDocument;
import com.example.SearchEng.repo.RawDocumentRepository;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngestService {

    private final RawDocumentRepository rawRepo;

    public int ingestArxiv(String searchQuery, int maxResults, int batch, long sleepMillis) throws Exception {
        String base = "http://export.arxiv.org/api/query";
        int start = 0, total = 0;
        while (start < maxResults) {
            int size = Math.min(batch, maxResults - start);
            String url = base + "?search_query=" + java.net.URLEncoder.encode(searchQuery, "UTF-8")
                    + "&start=" + start + "&max_results=" + size
                    + "&sortBy=submittedDate&sortOrder=descending";
            List<RawDocument> buf = fetchBatch(url);
            if (buf.isEmpty()) break;
            rawRepo.saveAll(buf);
            total += buf.size();
            start += size;
            Thread.sleep(sleepMillis);
        }
        return total;
    }

    private List<RawDocument> fetchBatch(String url) throws Exception {
        var input = new SyndFeedInput();
        SyndFeed feed;
        try (var reader = new XmlReader(new URL(url))) {
            feed = input.build(reader);
        }
        List<RawDocument> out = new ArrayList<>();
        for (Object o : feed.getEntries()) {
            SyndEntry e = (SyndEntry) o;
            String id = e.getUri(); // e.g., http://arxiv.org/abs/2401.01234v1
            if (id == null) id = e.getLink();
            String docId = id.substring(id.lastIndexOf('/') + 1);
            String title = e.getTitle();
            String abs = (e.getDescription() != null) ? e.getDescription().getValue() : "";
            List<String> authors = new ArrayList<>();
            for (SyndPerson p : e.getAuthors()) authors.add(p.getName());
            List<String> topics = new ArrayList<>();
            for (SyndCategory c : e.getCategories()) topics.add(c.getName());
            String published = (e.getPublishedDate() != null) ?
                    e.getPublishedDate().toInstant().toString().substring(0,10) : "";

            out.add(RawDocument.builder()
                    .docId(docId)
                    .title(title)
                    .abstractText(abs)
                    .authors(authors)
                    .topics(topics)
                    .published(published)
                    .url(e.getLink())
                    .source("arxiv")
                    .build());
        }
        return out;
    }
}
