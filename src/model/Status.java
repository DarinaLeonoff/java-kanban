package model;

public enum Status {
    NEW, IN_PROGRESS, DONE;

    public static Status toStatus(String s) {
        Status status = null;
        switch (s) {
            case "NEW":
                status = Status.NEW;
                break;
            case "IN_PROGRESS":
                status = Status.IN_PROGRESS;
                break;
            case "DONE":
                status = Status.DONE;
        }
        return status;
    }
}
