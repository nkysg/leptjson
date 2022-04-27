import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

public class LeptContext {
    public static final char NONE = 0;
    private String json;
    int pos;
    LeptStack stack;
    Stack<LeptValue> stkv;
    public LeptContext(String json) {
        this.json = json;
        pos = 0;
        stack = new LeptStack();
        stkv = new Stack<>();
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
        return LeptParseResult.LEPT_PARSE_OK;
    }

    private LeptParseResult lept_parse_value(LeptValue value) {
        lept_parse_whitespace();
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

            case '[':
                return lept_parse_array(value);
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
        value.setLeptValue(LeptType.LEPT_NUMBER,json.substring(start_idx, pos));
        return LeptParseResult.LEPT_PARSE_OK;
    }

    private LeptParseResult lept_parse_string(LeptValue value) {
        char ch = next();
        assert (ch == '\"');
        int head = stack.index_top();
        int len = 0;
        for (;;) {
            ch = next();
            switch (ch) {
                case '\"':
                    len = stack.index_top() - head;
                    String str = String.valueOf(stack.pop_chars(len));
                    value.setLeptValue(LeptType.LEPT_STRING, str);
                    return LeptParseResult.LEPT_PARSE_OK;
                case '\\':
                    ch = next();
                    switch (ch) {
                        case '\"':
                            stack.push_char('\"');
                            break;
                        case '\\':
                            stack.push_char('\\');
                            break;
                        case '/':
                            stack.push_char('/');
                            break;
                        case 'b':
                            stack.push_char('\b');
                            break;
                        case 'f':
                            stack.push_char('\f');
                            break;
                        case 'n':
                            stack.push_char('\n');
                            break;
                        case 'r':
                            stack.push_char('\r');
                            break;
                        case 't':
                            stack.push_char('\t');
                            break;
                        default:
                            stack.set_index_top(head);
                            return LeptParseResult.LEPT_PARSE_INVALID_STRING_ESCAPE;
                    }
                    break;
                case NONE:
                    stack.set_index_top(head);
                    return LeptParseResult.LEPT_PARSE_MISS_QUOTATION_MARK;
                default:
                    if (ch < 0x20) {
                        stack.set_index_top(head);
                        return LeptParseResult.LEPT_PARSE_INVALID_STRING_CHAR;
                    }
                    stack.push_char(ch);
            }
        }
    }

    private LeptParseResult lept_parse_array(LeptValue value) {
        int size = 0;
        char ch = next();
        assert ch == '[';
        lept_parse_whitespace();
        if (peek() == ']') {
            next();
            value.setLeptType(LeptType.LEPT_ARRAY);
            return LeptParseResult.LEPT_PARSE_OK;
        }

        for (;;) {
            LeptValue e = new LeptValue();
            e.setLeptType(LeptType.LEPT_NULL);
            LeptParseResult ret = lept_parse_value(e);
            if (ret != LeptParseResult.LEPT_PARSE_OK) {
               // System.out.println("error" + json.substring(0, pos));
                return ret;
            }
            stkv.add(e);
            size++;
            lept_parse_whitespace();
            if (peek() == ',') {
                // System.out.println("," + json.substring(0, pos));
                next();
            } else if (peek() == ']') {
                next();
                value.setLeptType(LeptType.LEPT_ARRAY);
                ArrayList<LeptValue> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    list.add(stkv.pop());
                }
                Collections.reverse(list);
                value.setList(list);
                return LeptParseResult.LEPT_PARSE_OK;
            } else {
                // System.out.println("this" + json.substring(0, pos));
                return LeptParseResult.LEPT_PARSE_MISS_COMMA_OR_SQUARE_BRACKET;
            }
        }

    }
}
