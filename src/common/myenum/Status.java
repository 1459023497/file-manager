package common.myenum;

public enum Status {
    INSERT("insert"),
    UPDATE("update"),
    BROWSE("browse"),
    DELETE("delete");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
