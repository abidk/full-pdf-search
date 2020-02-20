package abid.fullpdfsearch.model;

public enum Fields {

    FILE_NAME("fileName"),
    TEXT("text");

    private final String value;

    Fields(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
