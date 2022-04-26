public enum LeptParseError {
    LEPT_PARSE_OK(0),
    LEPT_PARSE_EXPECT_VALUE(1),
    LEPT_PARSE_INVALID_VALUE(2),
    LEPT_PARSE_ROOT_NOT_SINGULAR(3);

    private int code;

    LeptParseError(int code) {
        this.code = code;
    }

    int getLeptParseError() {
        return code;
    }
}
