package abid.fullpdfsearch.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PdfFiles {

    private static final String PDF_DIR = "./pdf/";

    public List<Path> getFiles() throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(PDF_DIR))) {
            return walk
                    .filter(path -> path.toFile().getName().endsWith(".pdf"))
                    .collect(Collectors.toList());
        }
    }

}
