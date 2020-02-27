package abid.fullpdfsearch.service;

public class SearchException extends RuntimeException {

    public SearchException(String message, Exception e) {
        super(message, e);
    }
}
