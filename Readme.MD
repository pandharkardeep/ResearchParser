# SpringIR: Custom Information Retrieval Engine

A lightweight search engine built from scratch in Java Spring Boot, leveraging BM25 ranking and faceted search over documents stored in MongoDB. Unlike Elasticsearch or Solr, this engine implements core Information Retrieval (IR) concepts manually, making it ideal for learning and experimentation.

## 🚀 Features

- Document ingestion pipeline (e.g., from arXiv/feeds) into MongoDB
- Inverted index construction with term frequencies, document frequencies, and avgdl
- BM25 ranking for keyword search (with tunable k1 and b parameters)
- Field weighting (e.g., title weighted higher than abstract)
- Faceted search by author, year, and topic
- RESTful APIs for ingestion, index rebuild, and search queries
- Tested with JUnit + Testcontainers (MongoDB in Docker)
- Swagger/OpenAPI documentation for easy API exploration
- Google Looker Studio for monitoring and dashboarding

## 🛠️ Tech Stack

- Backend: Java 17, Spring Boot 3.5.6
- Database: MongoDB
- Build: Maven
- Testing: JUnit 5, Testcontainers
- Other: Lombok, Jsoup, ROME (for feed parsing)
