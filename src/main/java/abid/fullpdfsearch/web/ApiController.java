package abid.fullpdfsearch.web;

import abid.fullpdfsearch.model.SearchResult;
import abid.fullpdfsearch.service.PdfFiles;
import abid.fullpdfsearch.service.PdfTextExtractor;
import abid.fullpdfsearch.service.WebsiteRenderer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
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

@RestController
public class ApiController {

    private static final String SEARCH_TEMPLATE = "templates/index.html";
    private static final String INDEX_PARAM_FILE = "file";
    private static final String INDEX_PARAM_TEXT = "text";

    private final WebsiteRenderer websiteRenderer;
    private final PdfFiles pdfFiles;
    private final PdfTextExtractor extractor;

    @Autowired
    public ApiController(WebsiteRenderer websiteRenderer, PdfFiles pdfFiles, PdfTextExtractor extractor) {
        this.websiteRenderer = websiteRenderer;
        this.pdfFiles = pdfFiles;
        this.extractor = extractor;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "query", defaultValue = "") String query) throws IOException, ParseException {
        final StandardAnalyzer analyzer = new StandardAnalyzer();
        final Directory index = createIndex(analyzer);

        List<SearchResult> results = searchIndex(analyzer, index, INDEX_PARAM_TEXT, query);

        Map<String, Object> data = new HashMap<>();
        data.put("query", query);
        data.put("results", results);
        return websiteRenderer.render(SEARCH_TEMPLATE, data);
    }

    private Directory createIndex(StandardAnalyzer analyzer) throws IOException {
        final Directory memoryIndex = new RAMDirectory();
        try (IndexWriter writer = new IndexWriter(memoryIndex, new IndexWriterConfig(analyzer))) {
            for (Path path : pdfFiles.getFiles()) {
                System.out.println("Processing: " + path);
                final Document document = new Document();
                document.add(new TextField(INDEX_PARAM_FILE, path.toFile().getName(), Field.Store.YES));
                document.add(new TextField(INDEX_PARAM_TEXT, extractor.extract(path.toFile()), Field.Store.YES));
                writer.addDocument(document);
            }
        }
        return memoryIndex;
    }

    private List<SearchResult> searchIndex(StandardAnalyzer analyzer, Directory memoryIndex, String field, String queryString) throws ParseException, IOException {
        final IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(memoryIndex));
        final Query query = new QueryParser(field, analyzer).parse(queryString);
        final TopDocs topDocs = searcher.search(query, 10);

        List<SearchResult> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String filename = doc.get(INDEX_PARAM_FILE);
            String text = doc.get(INDEX_PARAM_TEXT);
            results.add(new SearchResult(filename, scoreDoc.score, text));
        }
        return results;
    }
}
