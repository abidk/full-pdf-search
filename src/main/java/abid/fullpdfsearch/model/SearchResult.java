package abid.fullpdfsearch.model;

public class SearchResult {
    private final String filename;
    private final String text;

    public SearchResult(String filename, String text) {
        this.filename = filename;
        this.text = text;
    }

    public String getFilename() {
        return filename;
    }

    public String getText() {
        return text;
    }
}
