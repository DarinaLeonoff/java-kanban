package manager;

public enum TaskTypes {
    TASK, EPIC, SUBTASK;

    public static TaskTypes toType(String s) {
        TaskTypes type = null;
        switch (s) {
            case "TASK":
                type = TaskTypes.TASK;
                break;
            case "EPIC":
                type = TaskTypes.EPIC;
                break;
            case "SUBTASK":
                type = TaskTypes.SUBTASK;
        }
        return type;
    }
}
