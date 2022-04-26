import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LeptContextTest {
    @Test
    public void test_parse_null() {
        LeptContext ctx = new LeptContext("null");
        LeptValue lept_value = new LeptValue();
        LeptParseError parse_error = ctx.lept_parse(lept_value);
        assertEquals(parse_error, LeptParseError.LEPT_PARSE_OK);
        assertEquals(lept_value.getLeptType(), LeptType.LEPT_NULL);
    }

    @Test
    public void test_parse_expect_value() {
        LeptContext ctx = new LeptContext("");
        LeptValue lept_value = new LeptValue();
        lept_value.setLeptType(LeptType.LEPT_FALSE);
        LeptParseError parse_error = ctx.lept_parse(lept_value);
        assertEquals(parse_error,LeptParseError.LEPT_PARSE_EXPECT_VALUE);
        assertEquals(lept_value.getLeptType(), LeptType.LEPT_NULL);

        ctx = new LeptContext(" ");
        lept_value.setLeptType(LeptType.LEPT_FALSE);
        parse_error = ctx.lept_parse(lept_value);
        assertEquals(parse_error,LeptParseError.LEPT_PARSE_EXPECT_VALUE);
        assertEquals(lept_value.getLeptType(), LeptType.LEPT_NULL);
    }

    @Test
    public void test_parse_invalid_value() {
        LeptContext ctx = new LeptContext("nul");
        LeptValue lept_value = new LeptValue();
        lept_value.setLeptType(LeptType.LEPT_FALSE);
        LeptParseError parse_error = ctx.lept_parse(lept_value);
        assertEquals(parse_error,LeptParseError.LEPT_PARSE_INVALID_VALUE);
        assertEquals(lept_value.getLeptType(), LeptType.LEPT_NULL);

        ctx = new LeptContext("?");
        lept_value.setLeptType(LeptType.LEPT_FALSE);
        parse_error = ctx.lept_parse(lept_value);
        assertEquals(parse_error,LeptParseError.LEPT_PARSE_INVALID_VALUE);
        assertEquals(lept_value.getLeptType(), LeptType.LEPT_NULL);
    }

    @Test
    public void test_parse_root_not_singular() {
        LeptContext ctx = new LeptContext("null x");
        LeptValue lept_value = new LeptValue();
        lept_value.setLeptType(LeptType.LEPT_FALSE);
        LeptParseError parse_error = ctx.lept_parse(lept_value);
        assertEquals(parse_error,LeptParseError.LEPT_PARSE_ROOT_NOT_SINGULAR);
        assertEquals(lept_value.getLeptType(), LeptType.LEPT_NULL);
    }

}
