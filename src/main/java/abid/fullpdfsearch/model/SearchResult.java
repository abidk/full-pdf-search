package abid.fullpdfsearch.model;

public class SearchResult {
    private final String filename;
    private final String text;
    private final String[] fragments;

    public SearchResult(String filename, String text, String[] fragments) {
        this.filename = filename;
        this.text = text;
        this.fragments = fragments;
    }

    public String getFilename() {
        return filename;
    }

    public String getText() {
        return text;
    }

    public String[] getFragments() {
        return fragments;
    }
}
