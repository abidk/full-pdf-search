package abid.fullpdfsearch.web;

import abid.fullpdfsearch.model.SearchResult;
import abid.fullpdfsearch.service.PdfFiles;
import abid.fullpdfsearch.service.PdfReader;
import abid.fullpdfsearch.service.WebsiteRenderer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ApiController {

    public static final String SEARCH_TEMPLATE = "templates/index.html";

    private final WebsiteRenderer websiteRenderer;
    private final PdfFiles pdfFiles;
    private final PdfReader pdfReader;

    @Autowired
    public ApiController(WebsiteRenderer websiteRenderer, PdfFiles pdfFiles, PdfReader pdfReader) {
        this.websiteRenderer = websiteRenderer;
        this.pdfFiles = pdfFiles;
        this.pdfReader = pdfReader;
    }

    @GetMapping("/search")
    public String greeting(@RequestParam(value = "query", defaultValue = "") String query) throws IOException, ParseException {
        Directory memoryIndex = new RAMDirectory();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);

        final List<Path> paths = pdfFiles.getFiles();
        for(Path path : paths) {
            final String file = path.toFile().getName();
            System.out.println("Processing: " + file);
            Document document = new Document();
            document.add(new TextField("file", file, Field.Store.YES));
            document.add(new TextField("text", pdfReader.read(path.toFile()), Field.Store.YES));
            writer.addDocument(document);
        }

        writer.close();

        List<Document> documents = searchIndex(analyzer, memoryIndex, "text", query);

        List<SearchResult> results = documents.stream().map(document -> {
            String filename = document.get("file");
            String text = document.get("text");
            return new SearchResult(filename, text);
        }).collect(Collectors.toList());


        Map<String, Object> data = new HashMap<>();
        data.put("query", query);
        data.put("results", results);

        return websiteRenderer.render(SEARCH_TEMPLATE, data);
    }

    public List<Document> searchIndex(StandardAnalyzer analyzer, Directory memoryIndex, String inField, String queryString) throws ParseException, IOException {
        Query query = new QueryParser(inField, analyzer)
                .parse(queryString);

        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        return documents;
    }

}
