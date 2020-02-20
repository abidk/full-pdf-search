package abid.fullpdfsearch;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class LuceneConfiguration {

    @Bean
    public Analyzer standardAnalyzer() {
        return new StandardAnalyzer();
    }

    @Bean
    public Directory directory() {
        return new RAMDirectory();
    }

    @Bean
    public IndexSearcher indexSearcher(Directory directory) throws IOException {
        System.out.println(directory);
        return new IndexSearcher(DirectoryReader.open(directory));
    }
}
