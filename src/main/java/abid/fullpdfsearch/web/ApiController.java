package abid.fullpdfsearch.web;

import abid.fullpdfsearch.model.Fields;
import abid.fullpdfsearch.model.SearchResult;
import abid.fullpdfsearch.service.PdfTextSearcher;
import abid.fullpdfsearch.service.WebsiteRenderer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    private static final String SEARCH_TEMPLATE = "templates/index.html";

    private final WebsiteRenderer websiteRenderer;
    private final PdfTextSearcher searcher;

    @Autowired
    public ApiController(WebsiteRenderer websiteRenderer, PdfTextSearcher searcher) {
        this.websiteRenderer = websiteRenderer;
        this.searcher = searcher;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "query", defaultValue = "") String query) throws IOException, ParseException {
        List<SearchResult> results = searcher.search(Fields.TEXT.getValue(), query);

        Map<String, Object> data = new HashMap<>();
        data.put("query", query);
        data.put("results", results);
        return websiteRenderer.render(SEARCH_TEMPLATE, data);
    }
}
