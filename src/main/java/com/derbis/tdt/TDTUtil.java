package com.derbis.tdt;

import com.derbis.model.Field;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.derbis.model.CompactionMethodList.*;

@Component
public class TDTUtil {

    public String GRAMMAR_REGEX = "'.*?'|\\s*[\\w]+\\s*";

    public Map<String, String> parseParameterList(String parameterList) {
        HashMap<String, String> parameterMap = new HashMap<>();

        Splitter.on(";")
                .split(parameterList)
                .forEach(s -> {
                            Iterator<String> nameValue = Splitter.on("=")
                                                                 .trimResults()
                                                                 .omitEmptyStrings()
                                                                 .split(s)
                                                                 .iterator();
                            parameterMap.put(nameValue.next()
                                                      .toLowerCase(), nameValue.next()
                                                                               .toLowerCase());
                        }
                );
        return parameterMap;
    }

    // According to the GS1 General Specifications [GS1GS14.0] for use in alphanumeric serial numbers
    public String escape(String s) {
        return s.replace("%", "%25")
                .replace("\"", "%22")
                .replace("&", "%26")
                .replace("/", "%2F")
                .replace("<", "%3C")
                .replace(">", "%3E")
                .replace("?", "%3F");
    }

    public String unescape(String s) {
        return s.replace("%22", "\"")
                .replace("%25", "%")
                .replace("%26", "&")
                .replace("%2F", "/")
                .replace("%3C", "<")
                .replace("%3E", ">")
                .replace("%3F", "?");
    }

    public boolean validateCharacterSet(String input, String characterSet) {
        return characterSet == null || Pattern.compile(characterSet)
                                              .matcher(input)
                                              .matches();
    }

    public int getCompactionBits(Field inputField) {
        int compactionBits = 0;
        if (inputField.getCompaction()
                      .equals(BIT32.getValue())) {
            compactionBits = BIT32.getCompactionBits();
        } else if (inputField.getCompaction()
                             .equals(BIT16.getValue())) {
            compactionBits = BIT16.getCompactionBits();
        } else if (inputField.getCompaction()
                             .equals(BIT8.getValue())) {
            compactionBits = BIT8.getCompactionBits();
        } else if (inputField.getCompaction()
                             .equals(BIT7.getValue())) {
            compactionBits = BIT7.getCompactionBits();
        } else if (inputField.getCompaction()
                             .equals(BIT6.getValue())) {
            compactionBits = BIT6.getCompactionBits();
        } else if (inputField.getCompaction()
                             .equals(BIT5.getValue())) {
            compactionBits = BIT5.getCompactionBits();
        }
        return compactionBits;
    }

    public boolean validateMinimum(String number, String minimum) {
        if (StringUtils.isNotBlank(minimum)) {
            BigInteger numberToValidate = new BigInteger(number);
            BigInteger minimumValue = new BigInteger(minimum);
            return numberToValidate.compareTo(minimumValue) >= 0;
        } else {
            return true;
        }
    }

    public boolean validateMaximum(String number, String maximum) {
        if (StringUtils.isNotBlank(maximum)) {
            BigInteger numberToValidate = new BigInteger(number);
            BigInteger maximumValue = new BigInteger(maximum);
            return numberToValidate.compareTo(maximumValue) <= 0;
        } else {
            return true;
        }
    }

    public String getValue(String input, Map<String, String> parameterMap) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(input);
            return input;
        } catch (NumberFormatException e) {
            return parameterMap.get(input);
        }
    }

    public String computeGS1Checksum(String input) {
        // courtesy of http://codereview.stackexchange.com/questions/126685/calculate-gs1-sscc-upc-check-digit
        int sum = 0;
        for (int i = 0; i < input.length(); ++i) {
            int n = Integer.parseInt(StringUtils.substring(input, input.length() - 1 - i, input.length() - i));
            sum += i % 2 == 0 ? n * 3 : n;
        }
        int checksum = sum % 10 == 0 ? 0 : 10 - sum % 10;
        return Integer.toString(checksum);
    }

    public List<String> collectionMatch(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(StringUtils.trim(matcher.group()));
        }
        return matches;
    }

    public String hexToBinary(String hex, int binaryDigitLimit) {
        BigInteger bigInteger = new BigInteger(hex, 16);
        String binaryString = bigInteger.toString(2);
        int pad = binaryString.length() % 4;
        String binaryStringPadded = StringUtils.leftPad(binaryString, binaryString.length() + pad, "0");
        return binaryDigitLimit > 0 ?
                StringUtils.substring(binaryStringPadded, 0, binaryDigitLimit) : binaryStringPadded;
    }

    public String binaryToHex(String binary) {
        if (binary.length() % 16 != 0) {
            int pad = 16 - binary.length() % 16;
            binary = StringUtils.rightPad(binary, binary.length() + pad, "0");
        }
        BigInteger bigInteger = new BigInteger(binary, 2);
        return bigInteger.toString(16)
                         .toUpperCase();
    }

    public String longToBinary(String longString) {
        return StringUtils.isNumeric(longString) ? Long.toBinaryString(Long.parseLong(longString)) : "0";
    }
}
