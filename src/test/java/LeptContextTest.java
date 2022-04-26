import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LeptContextTest {

    @Test
    public void test_parse_null() {
        LeptContext ctx = new LeptContext("null");
        LeptValue value = new LeptValue();
        LeptParseResult result = ctx.lept_parse(value);
        assertEquals(result, LeptParseResult.LEPT_PARSE_OK);
        assertEquals(value.getLeptType(), LeptType.LEPT_NULL);
    }

    @Test
    public void test_parse_true() {
        LeptContext ctx = new LeptContext("true");
        LeptValue value = new LeptValue();
        value.setLeptType(LeptType.LEPT_FALSE);
        LeptParseResult result = ctx.lept_parse(value);
        assertEquals(result, LeptParseResult.LEPT_PARSE_OK);
        assertEquals(value.getLeptType(), LeptType.LEPT_TRUE);
    }

    @Test
    public void test_parse_false() {
        LeptContext ctx = new LeptContext("false");
        LeptValue value = new LeptValue();
        value.setLeptType(LeptType.LEPT_FALSE);
        LeptParseResult result = ctx.lept_parse(value);
        assertEquals(result, LeptParseResult.LEPT_PARSE_OK);
        assertEquals(value.getLeptType(), LeptType.LEPT_FALSE);
    }

    private void test_number(double expect, String json) {
        LeptContext ctx =new LeptContext(json);
        LeptValue value = new LeptValue();
        LeptParseResult result = ctx.lept_parse(value);
        assertEquals(result, LeptParseResult.LEPT_PARSE_OK);
        assertEquals(LeptType.LEPT_NUMBER, value.getLeptType());
        assertEquals(expect, value.getLeptNumber(),1e-6);
    }

    @Test
    public void test_parse_number() {
        test_number(0.0, "0");
        test_number(0.0, "-0");
        test_number(0.0, "-0.0");
        test_number(1.0, "1");
        test_number(-1.0, "-1");
        test_number(1.5, "1.5");
        test_number(-1.5, "-1.5");
        test_number(3.1416, "3.1416");
        test_number(1E10, "1E10");
        test_number(1e10, "1e10");
        test_number(1E+10, "1E+10");
        test_number(1E-10, "1E-10");
        test_number(-1E10, "-1E10");
        test_number(-1e10, "-1e10");
        test_number(-1E+10, "-1E+10");
        test_number(-1E-10, "-1E-10");
        test_number(1.234E+10, "1.234E+10");
        test_number(1.234E-10, "1.234E-10");
        test_number(0.0, "1e-10000"); /* must underflow */

        test_number(1.0000000000000002, "1.0000000000000002"); /* the smallest number > 1 */
        test_number( 4.9406564584124654e-324, "4.9406564584124654e-324"); /* minimum denormal */
        test_number(-4.9406564584124654e-324, "-4.9406564584124654e-324");
        test_number( 2.2250738585072009e-308, "2.2250738585072009e-308");  /* Max subnormal double */
        test_number(-2.2250738585072009e-308, "-2.2250738585072009e-308");
        test_number( 2.2250738585072014e-308, "2.2250738585072014e-308");  /* Min normal positive double */
        test_number(-2.2250738585072014e-308, "-2.2250738585072014e-308");
        test_number( 1.7976931348623157e+308, "1.7976931348623157e+308");  /* Max double */
        test_number(-1.7976931348623157e+308, "-1.7976931348623157e+308");

    }

    private void test_error(LeptParseResult error, String json) {
        LeptContext ctx = new LeptContext(json);
        LeptValue value = new LeptValue();
        value.setLeptType(LeptType.LEPT_FALSE);
        LeptParseResult result = ctx.lept_parse(value);
        assertEquals(error, result);
        assertEquals(value.getLeptType(), LeptType.LEPT_NULL);
    }

    @Test
    public void test_parse_expect_value() {
        test_error(LeptParseResult.LEPT_PARSE_EXPECT_VALUE, "");
        test_error(LeptParseResult.LEPT_PARSE_EXPECT_VALUE, " ");
    }

    @Test
    public void test_parse_invalid_value() {
        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, "nul");
        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, "?");

        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, "+0");
        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, "+1");
        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, ".123"); /* at least one digit before '.' */
        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, "1.");   /* at least one digit after '.' */
        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, "INF");
        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, "inf");
        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, "NAN");
        test_error(LeptParseResult.LEPT_PARSE_INVALID_VALUE, "nan");
    }

    @Test
    public void test_parse_root_not_singular() {
        test_error(LeptParseResult.LEPT_PARSE_ROOT_NOT_SINGULAR, "null x");

        test_error(LeptParseResult.LEPT_PARSE_ROOT_NOT_SINGULAR, "0123");
        test_error(LeptParseResult.LEPT_PARSE_ROOT_NOT_SINGULAR, "0x0");
        test_error(LeptParseResult.LEPT_PARSE_ROOT_NOT_SINGULAR, "0x123");
    }

    @Test
    public void test_parse_number_too_big() {
        test_error(LeptParseResult.LEPT_PARSE_NUMBER_TOO_BIG, "1e309");
        test_error(LeptParseResult.LEPT_PARSE_NUMBER_TOO_BIG, "-1e309");
    }
}
