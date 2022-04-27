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

    private void test_string(String expect, String json) {
        LeptContext ctx = new LeptContext(json);
        LeptValue value = new LeptValue();
        value.setLeptType(LeptType.LEPT_NULL);
        LeptParseResult result = ctx.lept_parse(value);
        assertEquals(result, LeptParseResult.LEPT_PARSE_OK);
        assertEquals(LeptType.LEPT_STRING, value.getLeptType());
        assertEquals(expect, value.getStr());
    }

    @Test
    public void test_parse_string() {
        test_string("", "\"\"");
        test_string("Hello", "\"Hello\"");
        test_string("Hello\nWorld", "\"Hello\\nWorld\"");
        test_string("\" \\ / \b \f \n \r \t", "\"\\\" \\\\ \\/ \\b \\f \\n \\r \\t\"");
       // test_string("Hello\0World", "\"Hello\\u0000World\"");
       // test_string("\x24", "\"\\u0024\"");         /* Dollar sign U+0024 */
       // test_string("\xC2\xA2", "\"\\u00A2\"");     /* Cents sign U+00A2 */
       // test_string("\xE2\x82\xAC", "\"\\u20AC\""); /* Euro sign U+20AC */
       // test_string("\xF0\x9D\x84\x9E", "\"\\uD834\\uDD1E\"");  /* G clef sign U+1D11E */
        //test_string("\xF0\x9D\x84\x9E", "\"\\ud834\\udd1e\"");  /* G clef sign U+1D11E */
    }

    @Test
    public void test_parse_array() {
        LeptContext ctx = new LeptContext("[ ]");
        LeptValue value = new LeptValue();
        value.setLeptType(LeptType.LEPT_NULL);
        LeptParseResult result = ctx.lept_parse(value);
        assertEquals(LeptParseResult.LEPT_PARSE_OK, result);
        assertEquals(LeptType.LEPT_ARRAY, value.getLeptType());
        assertEquals(0, value.get_array_size());


        ctx = new LeptContext("[ null , false , true , 123 , \"abc\" ]");
        value = new LeptValue();
        value.setLeptType(LeptType.LEPT_NULL);
        result = ctx.lept_parse(value);
        assertEquals(LeptParseResult.LEPT_PARSE_OK, result);
        assertEquals(LeptType.LEPT_ARRAY, value.getLeptType());
        assertEquals(5, value.get_array_size());
        assertEquals(value.getIndexValue(0).getLeptType(), LeptType.LEPT_NULL);
        assertEquals(value.getIndexValue(1).getLeptType(), LeptType.LEPT_FALSE);
        assertEquals(value.getIndexValue(2).getLeptType(), LeptType.LEPT_TRUE);
        assertEquals(value.getIndexValue(3).getLeptType(), LeptType.LEPT_NUMBER);
        assertEquals(value.getIndexValue(3).getLeptNumber(), 123, 1e-6);
        assertEquals(value.getIndexValue(4).getLeptType(), LeptType.LEPT_STRING);
        assertEquals(value.getIndexValue(4).getStr(), "abc");

        ctx = new LeptContext("[ [ ] , [ 0 ] , [ 0 , 1 ] , [ 0 , 1 , 2 ] ]");
        value = new LeptValue();
        value.setLeptType(LeptType.LEPT_NULL);
        result = ctx.lept_parse(value);
        assertEquals(LeptParseResult.LEPT_PARSE_OK, result);
        assertEquals(LeptType.LEPT_ARRAY, value.getLeptType());
        assertEquals(4, value.get_array_size());

        for (int i = 0; i < 4; i++) {
            assertEquals(value.getIndexValue(i).getLeptType(), LeptType.LEPT_ARRAY);
            assertEquals(value.getIndexValue(i).get_array_size(), i);
            for (int j = 0; j < i; j++) {
                assertEquals(value.getIndexValue(i).getIndexValue(j).getLeptType(), LeptType.LEPT_NUMBER);
                assertEquals(value.getIndexValue(i).getIndexValue(j).getLeptNumber(), j, 1e-6);
            }
        }

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

    @Test
    public void test_parse_missing_quotation_mark() {
        test_error(LeptParseResult.LEPT_PARSE_MISS_QUOTATION_MARK, "\"");
        test_error(LeptParseResult.LEPT_PARSE_MISS_QUOTATION_MARK, "\"abc");
    }

    @Test
    public void test_parse_invalid_string_escape() {
        test_error(LeptParseResult.LEPT_PARSE_INVALID_STRING_ESCAPE, "\"\\v\"");
        test_error(LeptParseResult.LEPT_PARSE_INVALID_STRING_ESCAPE, "\"\\'\"");
        test_error(LeptParseResult.LEPT_PARSE_INVALID_STRING_ESCAPE, "\"\\0\"");
        test_error(LeptParseResult.LEPT_PARSE_INVALID_STRING_ESCAPE, "\"\\x12\"");
    }

    @Test
    public void test_parse_invalid_string_char() {
       // test_error(LeptParseResult.LEPT_PARSE_INVALID_STRING_CHAR, "\"\\x01\"");
       // test_error(LeptParseResult.LEPT_PARSE_INVALID_STRING_CHAR, "\"\x1F\"");
    }

    @Test
    public void test_access_null() {
        LeptValue value = new LeptValue();
        value.setLeptType(LeptType.LEPT_NULL);
        value.setLeptValue(LeptType.LEPT_STRING, "a");
        value.setLeptValue(LeptType.LEPT_NULL, "");
        assertEquals(LeptType.LEPT_NULL, value.getLeptType());
    }

    @Test
    public void test_access_boolean() {
        LeptValue value = new LeptValue();
        value.setLeptType(LeptType.LEPT_NULL);
        value.setLeptValue(LeptType.LEPT_STRING, "a");
        value.setLeptValue(LeptType.LEPT_TRUE, "1");
        assertEquals(LeptType.LEPT_TRUE, value.getLeptType());
        value.setLeptValue(LeptType.LEPT_FALSE, "0");
        assertEquals(LeptType.LEPT_FALSE, value.getLeptType());
    }

    @Test
    public void test_access_number() {
        LeptValue value = new LeptValue();
        value.setLeptType(LeptType.LEPT_NULL);
        value.setLeptValue(LeptType.LEPT_STRING, "a");
        value.setLeptValue(LeptType.LEPT_NUMBER, "1234.5");
        assertEquals(1234.5,value.getLeptNumber(), 1e-6);
    }

    @Test
    public void test_access_string() {

    }

}
