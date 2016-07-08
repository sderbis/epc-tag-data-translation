package com.derbis.tdt;

import com.derbis.BaseSpringTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilTest extends BaseSpringTest {
    private static String ESCAPED = "%3Cangle bracket%3E%26and100%2F0=%3For100%25";
    private static String NORMAL = "<angle bracket>&and100/0=?or100%";

    @Autowired
    Util util;

    @Test
    public void testParseParameterList() throws Exception {
        Map<String, String> parameterMap = util.parseParameterList("filter=3;gs1companyprefixlength=7;tagLength=96");
        assertThat(parameterMap).isNotEmpty();
        assertThat(parameterMap.get("filter")).isEqualTo("3");
        assertThat(parameterMap.get("gs1companyprefixlength")).isEqualTo("7");
        assertThat(parameterMap.get("taglength")).isEqualTo("96");
    }

    @Test
    public void testEscape() throws Exception {
        assertThat(util.escape(NORMAL)).isEqualTo(ESCAPED);
    }

    @Test
    public void testUnescape() throws Exception {
        assertThat(util.unescape(ESCAPED)).isEqualTo(NORMAL);
    }

    @Test
    public void testMatch() {
        String epcIdentifier = "gtin=00037000302414;serial=10419703";
        String pattern = "gtin=([0-9]{14});serial=([!%-?A-Z_a-z0-9]{1,20})";
        Matcher matcher = Pattern.compile("^" + pattern + "$")
                                 .matcher(epcIdentifier);
        assertThat(matcher.matches()).isTrue();

        int numberOfMatches = matcher.groupCount();
        assertThat(numberOfMatches).isEqualTo(2);
        assertThat(matcher.group(1)).isEqualTo("00037000302414");
        assertThat(matcher.group(2)).isEqualTo("10419703");
    }

    @Test
    public void testValidateMinMax() {
        String minimum = "0";
        String maximum = "999999999999";

        assertThat(util.validateMinimum("1", minimum)).isTrue();
        assertThat(util.validateMinimum("-1", minimum)).isFalse();
        assertThat(util.validateMaximum("1", maximum)).isTrue();
        assertThat(util.validateMaximum("9999999999990", maximum)).isFalse();
    }

    @Test
    public void testSplitFunction() {
        String function = "SUBSTR(gtin,1,12)";
        String[] functionSplit = StringUtils.split(function, "(,)", 128);

        assertThat(functionSplit.length).isEqualTo(4);
        assertThat(functionSplit[0]).isEqualTo("SUBSTR");
        assertThat(functionSplit[1]).isEqualTo("gtin");
        assertThat(functionSplit[2]).isEqualTo("1");
        assertThat(functionSplit[3]).isEqualTo("12");
    }

    @Test
    public void testCollectionMatches() {
        String grammarString = "'00110000' filter '101' gs1companyprefix itemref serial";

        List<String> matches = util.collectionMatch(grammarString, util.GRAMMAR_REGEX);
        assertThat(matches).hasSize(6);
        assertThat(matches.get(0)).isEqualTo("'00110000'");
        assertThat(matches.get(1)).isEqualTo("filter");
        assertThat(matches.get(2)).isEqualTo("'101'");
        assertThat(matches.get(3)).isEqualTo("gs1companyprefix");
        assertThat(matches.get(4)).isEqualTo("itemref");
        assertThat(matches.get(5)).isEqualTo("serial");
    }

    @Test
    public void testHexToBinary() {
        assertThat(util.hexToBinary("A", 0)).isEqualTo("1010");
        assertThat(util.hexToBinary("a", 0)).isEqualTo("1010");
        assertThat(util.hexToBinary("31105E30A7055F2CC0000000", 0))
                .isEqualTo(
                        "001100010001000001011110001100001010011100000101010111110010110011000000000000000000000000000000");
        assertThat(util.hexToBinary("31105E30A7055F2CC0000000", 20))
                .isEqualTo("00110001000100000101");
    }

    @Test
    public void testBinaryToHex() {
        assertThat(util.binaryToHex("1010")).isEqualTo("A000");
        assertThat(util.binaryToHex(
                "001100010001000001011110001100001010011100000101010111110010110011000000000000000000000000000000"))
                .isEqualTo("31105E30A7055F2CC0000000");
    }

    @Test
    public void testBinaryToHex2() {
        assertThat(util.binaryToHex(
                "00111010001010000000011101011011110011010001010100000010000000000000000000000000000000000110011010010101000101110"))
                .isEqualTo("3A28075BCD1502000000006695170000");
    }
}