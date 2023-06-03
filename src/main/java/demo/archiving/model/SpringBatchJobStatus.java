package demo.archiving.model;

public enum SpringBatchJobStatus {
    UNKNOWN("UNKNOWN"),
    COMPLETED("COMPLETED"),
    NOOP("NOOP");
    private final String value;
    private SpringBatchJobStatus(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
