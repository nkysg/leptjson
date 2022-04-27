public enum LeptParseResult {
    LEPT_PARSE_OK(0),
    LEPT_PARSE_EXPECT_VALUE(1),
    LEPT_PARSE_INVALID_VALUE(2),
    LEPT_PARSE_ROOT_NOT_SINGULAR(3),
    LEPT_PARSE_NUMBER_TOO_BIG(4),
    LEPT_PARSE_MISS_QUOTATION_MARK(5),
    LEPT_PARSE_INVALID_STRING_ESCAPE(6),
    LEPT_PARSE_INVALID_STRING_CHAR(7),
    LEPT_PARSE_INVALID_UNICODE_HEX(8),
    LEPT_PARSE_INVALID_UNICODE_SURROGATE(9),
    LEPT_PARSE_MISS_COMMA_OR_SQUARE_BRACKET(10);

    private int code;

    LeptParseResult(int code) {
        this.code = code;
    }

    int getLeptParseResult() {
        return code;
    }
}
