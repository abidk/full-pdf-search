package abid.fullpdfsearch;

import abid.fullpdfsearch.model.Fields;
import abid.fullpdfsearch.service.PdfTextExtractor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Startup {

    private static final String PDF_DIR = "./pdf/";
    private final Analyzer analyzer;
    private final Directory directory;
    private final PdfTextExtractor extractor;

    @Autowired
    public Startup(Analyzer analyzer, Directory directory, PdfTextExtractor extractor) {
        this.analyzer = analyzer;
        this.directory = directory;
        this.extractor = extractor;
    }

    @PostConstruct
    public void init() throws IOException {
        try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            for (Path path : retrievePdfFiles()) {
                System.out.println("Processing: " + path);
                final Document document = new Document();
                document.add(new TextField(Fields.FILE_NAME.getValue(), path.toFile().getName(), Field.Store.YES));
                document.add(new TextField(Fields.TEXT.getValue(), extractor.extract(path.toFile()), Field.Store.YES));
                writer.addDocument(document);
            }
        }
    }

    private List<Path> retrievePdfFiles() throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(PDF_DIR))) {
            return walk
                    .filter(path -> path.toFile().getName().endsWith(".pdf"))
                    .collect(Collectors.toList());
        }
    }
}
