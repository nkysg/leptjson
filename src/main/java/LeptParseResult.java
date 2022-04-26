public enum LeptParseResult {
    LEPT_PARSE_OK(0),
    LEPT_PARSE_EXPECT_VALUE(1),
    LEPT_PARSE_INVALID_VALUE(2),
    LEPT_PARSE_ROOT_NOT_SINGULAR(3),
    LEPT_PARSE_NUMBER_TOO_BIG(4);

    private int code;

    LeptParseResult(int code) {
        this.code = code;
    }

    int getLeptParseResult() {
        return code;
    }
}
