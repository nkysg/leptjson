public class LeptContext {
    private String json;
    int pos;
    public LeptContext(String json) {
        this.json = json.trim();
        pos = 0;
    }

    private boolean hasNext() {
        if (pos < json.length()) {
            return true;
        }
        return false;
    }

    private char next() {
        if (hasNext()) {
            return json.charAt(pos++);
        }
        return (char)-1;
    }

    private char peek() {
        if (hasNext()){
            return json.charAt(pos);
        }
        return (char)-1;
    }

    public LeptParseError lept_parse(LeptValue lept_value) {
        lept_value.setLeptType(LeptType.LEPT_NULL);
        return lept_parse_value(lept_value);
    }

    private LeptParseError lept_parse_value(LeptValue lept_value) {
        if (!hasNext()) {
            return LeptParseError.LEPT_PARSE_EXPECT_VALUE;
        }
        char ch = next();
        switch (ch) {
            case 'n':
                return lept_parse_null(lept_value);
            case 't':
                return lept_parse_true(lept_value);
            case 'f':
                return lept_parse_false(lept_value);
            default:
                return LeptParseError.LEPT_PARSE_INVALID_VALUE;
        }
    }

    private LeptParseError lept_parse_null(LeptValue lept_value) {
        if (next() != 'u' || next() !='l' || next() != 'l') {
            return LeptParseError.LEPT_PARSE_INVALID_VALUE;
        }
        lept_value.setLeptType(LeptType.LEPT_NULL);
        if (hasNext()) {
            return LeptParseError.LEPT_PARSE_ROOT_NOT_SINGULAR;
        }
        return LeptParseError.LEPT_PARSE_OK;
    }

    private LeptParseError lept_parse_true(LeptValue lept_value) {
        return LeptParseError.LEPT_PARSE_OK;
    }

    private LeptParseError lept_parse_false(LeptValue lept_value) {
        return LeptParseError.LEPT_PARSE_OK;
    }
}
