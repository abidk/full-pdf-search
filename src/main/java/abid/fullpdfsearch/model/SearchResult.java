package abid.fullpdfsearch.model;

public class SearchResult {
    private final String filename;
    private final float score;
    private final String text;

    public SearchResult(String filename, float score, String text) {
        this.filename = filename;
        this.score = score;
        this.text = text;
    }

    public String getFilename() {
        return filename;
    }

    public float getScore() {
        return score;
    }

    public String getText() {
        return text;
    }
}
