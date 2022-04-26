public enum LeptType {
    LEPT_NULL(0),
    LEPT_FALSE(1),
    LEPT_TRUE(2),
    LEPT_NUMBER(3),
    LEPT_STRING(4),
    LEPT_ARRAY(5),
    LEPT_OBJECT(6);


    LeptType(int code) {
        this.code = code;
    }

    private int code;

    public int getLeptType() {
        return code;
    }
}
