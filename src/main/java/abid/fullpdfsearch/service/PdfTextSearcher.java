package abid.fullpdfsearch.service;

import abid.fullpdfsearch.model.Fields;
import abid.fullpdfsearch.model.SearchResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfTextSearcher {

    private final Analyzer analyzer;
    private final IndexSearcher indexSearcher;

    @Autowired
    public PdfTextSearcher(Analyzer analyzer, IndexSearcher indexSearcher) {
        this.analyzer = analyzer;
        this.indexSearcher = indexSearcher;
    }

    public List<SearchResult> search(String field, String queryString) throws SearchException {
        try {
            final Query query = new QueryParser(field, analyzer).parse(queryString);
            final TopDocs topDocs = indexSearcher.search(query, 10);

            final SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");
            final Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter(20));

            List<SearchResult> results = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                String filename = doc.get(Fields.FILE_NAME.getValue());
                String text = doc.get(Fields.TEXT.getValue());

                TokenStream tokenStream = analyzer.tokenStream(Fields.TEXT.getValue(), new StringReader(text));
                String[] fragments = highlighter.getBestFragments(tokenStream, text, 100);

                results.add(new SearchResult(filename, text, fragments));
            }
            return results;
        } catch (ParseException | IOException | InvalidTokenOffsetsException e) {
            throw new SearchException("Failed to search", e);
        }
    }
}
