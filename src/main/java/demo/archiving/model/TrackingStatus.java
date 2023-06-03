package demo.archiving.model;

public enum TrackingStatus {
    GENERATING(5),
    PENDING(0),
    INSERTED(1),
    DELETED(2),
    INSERT_ERROR(3),
    DELETE_ERROR(4);
    private final int value;
    private TrackingStatus(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static TrackingStatus fromInt(int value) {
        for (TrackingStatus status : TrackingStatus.values()) {
            if (status.value() == value) {
                return status;
            }
        }
        return null;
    }
}
