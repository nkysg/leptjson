public class LeptContext {
    public static final char NONE = 0;
    private String json;
    int pos;
    public LeptContext(String json) {
        this.json = json;
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
        return NONE;
    }

    private char peek() {
        if (hasNext()){
            return json.charAt(pos);
        }
        return NONE;
    }

    private void lept_parse_whitespace() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }

    public LeptParseResult lept_parse(LeptValue value) {
        value.setLeptType(LeptType.LEPT_NULL);
        lept_parse_whitespace();
        LeptParseResult result = lept_parse_value(value);
        if (result == LeptParseResult.LEPT_PARSE_OK) {
            lept_parse_whitespace();
            if (hasNext()) {
                value.setLeptType(LeptType.LEPT_NULL);
                result = LeptParseResult.LEPT_PARSE_ROOT_NOT_SINGULAR;
            }
        }
        return result;
    }

    private LeptParseResult lept_parse_literal(LeptValue value, String expect, LeptType type) {

        for (char ch : expect.toCharArray()) {
            if (ch != next()) {
                return LeptParseResult.LEPT_PARSE_INVALID_VALUE;
            }
        }
        value.setLeptType(type);
        if (hasNext()) {
            return LeptParseResult.LEPT_PARSE_ROOT_NOT_SINGULAR;
        }
        return LeptParseResult.LEPT_PARSE_OK;
    }

    private LeptParseResult lept_parse_value(LeptValue value) {
        if (!hasNext()) {
            return LeptParseResult.LEPT_PARSE_EXPECT_VALUE;
        }
        char ch = peek();
        switch (ch) {
            case 'n':
                return lept_parse_literal(value, "null", LeptType.LEPT_NULL);
            case 't':
                return lept_parse_literal(value, "true", LeptType.LEPT_TRUE);
            case 'f':
                return lept_parse_literal(value, "false", LeptType.LEPT_FALSE);
            case '"':
                return lept_parse_string(value);
            case NONE:
                return LeptParseResult.LEPT_PARSE_EXPECT_VALUE;
            default:
                return lept_parse_number(value);
        }
    }

    private LeptParseResult lept_parse_number(LeptValue value) {
        char ch;
        int start_idx = pos;
        if (peek() == '-') {
            next();
        }
        if (peek() == '0') {
            next();
        } else {
            ch = peek();
            if (!(ch >= '1' && ch <= '9')) {
                return LeptParseResult.LEPT_PARSE_INVALID_VALUE;
            }
            for (; Character.isDigit(peek()); ) next();
        }

        if (peek() == '.') {
            next();
            if (!Character.isDigit((peek()))) {
                return LeptParseResult.LEPT_PARSE_INVALID_VALUE;
            }
            for (; Character.isDigit(peek());) next();
        }
        ch = peek();
        if (ch == 'e' || ch == 'E') {
            next();
            ch = peek();
            if (ch == '+' || ch == '-') {
                next();
            }
            if (!Character.isDigit(peek())) {
                return LeptParseResult.LEPT_PARSE_INVALID_VALUE;
            }
            for (; Character.isDigit(peek());) next();
        }
        double val = Double.parseDouble(json.substring(start_idx, pos));
        if (Double.isInfinite(val)) {
            return LeptParseResult.LEPT_PARSE_NUMBER_TOO_BIG;
        }
        value.setLeptType(LeptType.LEPT_NUMBER);
        value.setLeptDouble(json.substring(start_idx, pos));
        return LeptParseResult.LEPT_PARSE_OK;
    }

    private LeptParseResult lept_parse_string(LeptValue value) {
        return LeptParseResult.LEPT_PARSE_OK;
    }
}
