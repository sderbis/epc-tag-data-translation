package com.derbis.tdt;

import com.derbis.model.*;
import com.google.common.base.Splitter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.derbis.model.LevelTypeList.*;
import static com.derbis.model.PadDirectionList.LEFT;
import static com.derbis.model.PadDirectionList.RIGHT;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class GS1EpcTagDataTranslationEngine {
    private static final Logger LOG = LogManager.getLogger(GS1EpcTagDataTranslationEngine.class);
    private static final String TAGLENGTH_PARAM = "taglength";

    @Autowired
    GS1EpcTagDataTranslation gs1EpcTagDataTranslation;

    @Autowired
    Rules rules;

    @Autowired
    Util util;

    private List<EpcTagDataTranslation> epcTagDataTranslations;

    @PostConstruct
    protected void init() {
        epcTagDataTranslations = gs1EpcTagDataTranslation.loadXMLResources();
    }

    public String translate(String epcIdentifier, String parameterList, String outputFormat)
            throws TDTTranslationException {
        LOG.debug("1. SETUP");
        LOG.info("translate({}, {}, {})", epcIdentifier, parameterList, outputFormat);
        // Read the input value and the supplied extra parameters.

        // Populate an associative array of key-value pairs with the supplied extra parameters.

        Map<String, String> parameterMap = util.parseParameterList(parameterList);
        // During the translation process, this associative array will be populated with additional
        // values of extracted fields or fields obtained through the application of rules of type
        // 'EXTRACT' or 'FORMAT'

        // Note the desired outbound level.
        LevelTypeList outputLevelType = LevelTypeList.fromValue(outputFormat);

        LOG.info("outbound level is {}", outputLevelType);
        LOG.debug("2. DETERMINE THE CODING SCHEME AND INBOUND REPRESENTATION LEVEL.");

        // To find the scheme and level that matches the input value, consider all schemes and the
        // prefixMatch attribute of each level element within each scheme.
        Map<Level, Scheme> inputLevelsSchemes = new HashMap<>();
        for (EpcTagDataTranslation epcTagDataTranslation : epcTagDataTranslations) {
            Scheme scheme = epcTagDataTranslation.getScheme()
                                                 .get(0);
            for (Level level : scheme.getLevel()) {
                // If the prefixMatch string matches the input value at the beginning, the scheme and
                // level should be considered as a candidate for the inbound representation.
                if (level.getPrefixMatch() == null) {
                    continue;
                }
                // If the scheme
                // element specifies a taglength attribute, then if the value of this attribute does not
                // match the value of the taglength key in the associative array, then this scheme and
                // level should no longer be considered as a candidate for the inbound representation.
                if (epcIdentifier.startsWith(level.getPrefixMatch())) {
                    if (scheme.getTagLength() != null) {
                        if (parameterMap.containsKey(TAGLENGTH_PARAM)) {
                            BigInteger parameterTaglength =
                                    BigInteger.valueOf(Long.parseLong(parameterMap.get(TAGLENGTH_PARAM)));
                            if (parameterTaglength.compareTo(scheme.getTagLength()) != 0) {
                                continue;
                            }
                        }
                    }
                    inputLevelsSchemes.put(level, scheme);
                    LOG.info("inbound level candidate is {}.{}", level.getType(), scheme.getName());
                }
            }
        }
        if (MapUtils.isEmpty(inputLevelsSchemes)) {
            LOG.error("No matching schemes found, check input {} for validness", epcIdentifier);
            throw new TDTTranslationException("no matching scheme");
        }

        LOG.debug("3. DETERMINE THE OPTION THAT MATCHES THE INPUT VALUE");

        // To find the option that matches the input value, consider any scheme+level candidates
        // from the previous step.
        Level inputLevel = null;
        Scheme inputScheme = null;
        Option inputOption = null;
        for (Map.Entry<Level, Scheme> entry : inputLevelsSchemes.entrySet()) {
            Level level = entry.getKey();
            Scheme scheme = entry.getValue();

            // For each of these schemes, if the optionKey attribute is
            // specified within the scheme element in terms of the name of a supplied parameter (e.g.
            // gs1companyprefixlength), check the associative array of supplied parameters to
            // see if a corresponding value is defined and if so, select the option element for which
            // the optionKey attribute of the option element has the corresponding value.
            //
            // e.g. if a candidate scheme has a scheme attribute
            // optionKey="gs1companyprefixlength" and the associative array of supplied
            // extra parameters has a key=value pair gs1companyprefixlength=7, then only the
            // option element having attribute optionKey="7" should be considered.
            for (Option option : level.getOption()) {
                // If the optionKey attribute is not specified within the scheme element or if the
                // corresponding value is not present in the associative array of supplied extra parameters,
                // then consider each option element within each scheme+level candidate and check
                // whether the pattern attribute of the option element matches the input value at the
                // start of the string.
                if (scheme.getOptionKey() != null) {
                    String value;

                    try {
                        //noinspection ResultOfMethodCallIgnored
                        Integer.parseInt(scheme.getOptionKey());
                        value = scheme.getOptionKey();
                    } catch (NumberFormatException e) {
                        value = parameterMap.get(scheme.getOptionKey());
                    }

                    if (!StringUtils.equals(option.getOptionKey(), value)) {
                        continue;
                    }
                }

                // When a match is found, this option should be considered further and the corresponding
                // value of the optionKey attribute of the option element should be noted for use in
                // step 6.
                LOG.info("found a match {}.{}.{}", level.getType(), scheme.getName(), option.getOptionKey());

                // unescape input if Pure Identity or Tag Encoding
                String epcIdentifierUnescaped;
                if (level.getType() == PURE_IDENTITY || level.getType() == TAG_ENCODING) {
                    epcIdentifierUnescaped = util.unescape(epcIdentifier);
                } else {
                    epcIdentifierUnescaped = epcIdentifier;
                }

                if (Pattern.compile("^" + option.getPattern() + "$")
                           .matcher(epcIdentifierUnescaped)
                           .matches()) {
                    LOG.info("Also matches the regex");
                    inputScheme = scheme;
                    inputLevel = level;
                    inputOption = option;
                    epcIdentifier = epcIdentifierUnescaped;
                    break;
                }
            }
        }

        if (inputOption == null) {
            LOG.error("No matching option found, check input {0} for validness", epcIdentifier);
            throw new TDTTranslationException("no input option");
        }

        // check if all parsing parameters are present
        if (StringUtils.isNotBlank(inputLevel.getRequiredParsingParameters())) {
            for (String s : Splitter.on(",")
                                    .split(inputLevel.getRequiredParsingParameters())) {
                if (!parameterMap.containsKey(s)) {
                    LOG.error("Undefined field {} (required by parsing parameters)", s);
                    throw new TDTTranslationException("undefined field");
                }
            }
        }

        LOG.info("Input is scheme {}, level {}, option {}",
                inputScheme.getName(), inputLevel.getType(), inputOption.getPattern());

        LOG.debug("4. PARSE THE INPUT VALUE TO EXTRACT VALUES FOR EACH FIELD WITHIN THE OPTION");

        // Having found a scheme, level and option matching the input value, consider the field
        // elements nested within the option element.

        // Matching of the input value against the regular expression provided in the pattern
        // attribute of the option element should result in a number of backreference strings being
        // extracted. These should be considered as the values for the field elements, where the
        // seq attribute of the field element indicates the sequence in which the fields are extracted
        // as backreferences, from the start of the input value, e.g. the value from the first
        // backreference should be considered as the value of the field element with seq="1",
        // the value of the second backreference is the value of the field element with seq="2".

        final Matcher matcher = Pattern.compile("^" + inputOption.getPattern() + "$")
                                       .matcher(epcIdentifier);
        if (!matcher.matches()) {
            LOG.error("Error in parsing input string {} according to option pattern {}",
                    epcIdentifier, inputOption.getPattern());
            throw new TDTTranslationException("epc identifier parse");
        }

        List<Field> fieldsSorted = inputOption.getField();
        Collections.sort(inputOption.getField(), (o1, o2) -> o1.getSeq()
                                                               .compareTo(o2.getSeq()));

        for (int i = 1; i <= matcher.groupCount(); ++i) {
            Field inputField = fieldsSorted.get(i - 1);
            String name = inputField.getName();
            String variableElement = matcher.group(i);

            // For each field element, if a characterSet attribute is specified, check that the
            // value of the field falls entirely within the specified character set.
            if (!util.validateCharacterSet(variableElement, inputField.getCharacterSet())) {
                LOG.error("Character set validation error; input {} does not match {}",
                        variableElement, inputField.getCharacterSet());
                throw new TDTTranslationException("field value does not match character set");
            }

            // For each field element, if the compaction attribute is null, treat the field as an
            // integer. If the type attribute of the input level was "BINARY", treat the string of 0 and
            // 1 characters matched by the regular expression backreference as a binary string and
            // convert it to a decimal integer.
            if (inputLevel.getType() == BINARY) {
                // If the inbound representation was binary, perform any necessary stripping, conversion of
                // binary to integer or string, padding, referring to the procedure described in the flowchart
                // Figure 9b.
                if (inputField.getCompaction() != null) {
                    //TODO: implement check for bitPadChar; somehow not used in TDS1.6.

                    // Convert sequence of bit into characters,
                    // considering that each byte may have been compacted,
                    // as indicated by the compaction attribute.
                    int compactionBits = util.getCompactionBits(inputField);
                    List<Byte> byteList = new ArrayList<>();
                    for (int j = 0; j < variableElement.length(); j += compactionBits) {
                        if (j + compactionBits <= variableElement.length()) {
                            String character = StringUtils.substring(variableElement, j, j + compactionBits);
                            // ISO/IEC 15962
                            if (compactionBits == 5) {
                                // During the decode process, each 5-bit segment of the compacted bit string has
                                // “010” added as a prefix to re- create the 8-bit value of the source data.
                                character = "010" + character;
                            } else if (compactionBits == 6) {
                                // During the decode process, each 6-bit segment of the compacted bit string is
                                // analysed.
                                // a. If the first bit is “1”, the bits “00” are added as a prefix before converting
                                // to values 20 to 3FHEX.
                                // b. If the first bit is “0”, the bits “01” are added as a prefix before converting
                                // to values 40 to 5FHEX.
                                if (StringUtils.startsWith(character, "1")) {
                                    character = "00" + character;
                                } else {
                                    character = "01" + character;
                                }
                            }
                            byteList.add(Byte.parseByte(character, 2));
                        }
                    }
                    Byte[] bytes = byteList.toArray(new Byte[byteList.size()]);
                    variableElement = StringUtils.trim(
                            StringUtils.toEncodedString(ArrayUtils.toPrimitive(bytes), UTF_8));
                } else {
                    variableElement = Long.toString(Long.parseLong(variableElement, 2));
                }
                // Corresponding string field in TAG-ENCODING level
                Field tagEncodingField = new Field();
                for (Level level : inputScheme.getLevel()) {
                    if (level.getType() == TAG_ENCODING) {
                        for (Option option : level.getOption()) {
                            if (option.getOptionKey()
                                      .equals(inputOption.getOptionKey())) {
                                tagEncodingField = option.getField()
                                                         .get(i - 1);
                                break;
                            }
                        }
                        break;
                    }
                }
                final boolean binaryInputFieldPadChar = StringUtils.isNotBlank(inputField.getPadChar());
                final boolean tagEncodingFieldPadChar = StringUtils.isNotBlank(tagEncodingField.getPadChar());

                if (binaryInputFieldPadChar && tagEncodingFieldPadChar) {
                    LOG.error("padChar defined in both BINARY and TAG_ENCODING");
                    throw new TDTTranslationException("invalid definition");
                }
                if (binaryInputFieldPadChar) {
                    if (inputField.getPadDir() == LEFT) {
                        variableElement = StringUtils.stripStart(variableElement, inputField.getPadChar());
                    } else {
                        variableElement = StringUtils.stripEnd(variableElement, inputField.getPadChar());
                    }
                }
                if (tagEncodingFieldPadChar) {
                    if (tagEncodingField.getPadDir() == LEFT) {
                        variableElement = StringUtils.leftPad(variableElement, tagEncodingField.getLength()
                                                                                               .intValue(),
                                tagEncodingField.getPadChar());
                    } else {
                        variableElement = StringUtils.rightPad(variableElement, tagEncodingField.getLength()
                                                                                                .intValue(),
                                tagEncodingField.getPadChar());
                    }
                }
            }
            // If the decimalMinimum attribute is specified, check that the value is not less than the
            // decimal minimum value specified.
            if (!util.validateMinimum(variableElement, inputField.getDecimalMinimum())) {
                LOG.error("{} lower than minimum {}", variableElement, inputField.getDecimalMinimum());
                throw new TDTTranslationException("field below minimum");
            }
            // If the decimalMaximum attribute is specified, check that the value is not greater than
            // the decimal maximum value specified.
            if (!util.validateMaximum(variableElement, inputField.getDecimalMaximum())) {
                LOG.error("{} larger than maximum {}", variableElement, inputField.getDecimalMaximum());
                throw new TDTTranslationException("field above maximum");
            }

            LOG.info("Found field {} with value {}", name, variableElement);
            parameterMap.put(name, variableElement);
        }

        LOG.debug("5. PERFORM ANY RULES OF TYPE EXTRACT WITHIN THE INBOUND OPTION " +
                "IN ORDER TO CALCULATE ADDITIONAL DERIVED FIELDS");

        // Now run the rules that have attribute type="EXTRACT" in sequence, to determine any
        // additional derived fields that must be calculated after parsing of the input value.
        List<Rule> extractRulesSorted = inputLevel.getRule();
        Collections.sort(inputLevel.getRule(), (o1, o2) -> o1.getSeq()
                                                             .compareTo(o2.getSeq()));
        rules.execute(extractRulesSorted, ModeList.EXTRACT, parameterMap);

        LOG.debug("6. FIND THE CORRESPONDING OPTION IN THE OUTBOUND REPRESENTATION");

        // To find the corresponding option in the outbound representation within the same scheme,
        // select the level element having the desired outbound representation and within that,
        // select the option element that has the same value of the optionKey attribute that was
        // noted at the end of step 3

        Level outputLevel = null;
        Option outputOption = null;

        for (Level level : inputScheme.getLevel()) {
            if (level.getType() == outputLevelType) {
                outputLevel = level;
                for (Option option : level.getOption()) {
                    if (option.getOptionKey()
                              .equals(inputOption.getOptionKey())) {
                        outputOption = option;
                    }
                }
            }
        }
        if (outputLevel == null || outputOption == null) {
            LOG.error("no matching output level and/or option found");
            throw new TDTTranslationException("output not known");
        }

        if (outputLevel.getRequiredFormattingParameters() != null) {
            for (String format : StringUtils.split(outputLevel.getRequiredFormattingParameters(), ",")) {
                if (!parameterMap.containsKey(format)) {
                    LOG.error("undefined field {} (required by formatting parameters)", format);
                    throw new TDTTranslationException("undefined field");
                }
            }
        }

        LOG.debug("7. PERFORM ANY RULES OF TYPE FORMAT WITHIN THE OUTBOUND REPRESENTATION" +
                " IN ORDER TO CALCULATE ADDITIONAL DERIVED FIELDS");

        // Run any rules with attribute type="FORMAT" in sequence, to determine any additional
        // derived fields that must be calculated in order to prepare the output format.
        //
        // Store the resulting key-value pairs in the associative array after checking that the value
        // falls entirely within the permitted characterSet (if specified) or within the permitted
        // numeric range (if decimalMinimum or decimalMaximum are specified) and
        // performing any necessary padding or stripping of characters.
        List<Rule> formatRulesSorted = outputLevel.getRule();
        Collections.sort(outputLevel.getRule(), (o1, o2) -> o1.getSeq()
                                                              .compareTo(o2.getSeq()));
        rules.execute(formatRulesSorted, ModeList.FORMAT, parameterMap);

        LOG.debug("8. USE THE GRAMMAR string AND SUBSTITUTIONS FROM THE ASSOCIATIVE ARRAY TO BUILD THE OUTPUT VALUE");

        // Consider the grammar string for that option as a sequence of fixed literal strings (the
        // characters between the single quotes) interspersed with a number of variable elements,
        // whose key names are indicated by alphanumeric strings without any enclosing single
        // quotation marks.

        LOG.debug("grammar string is {}", outputOption.getGrammar());

        StringBuilder outputString = new StringBuilder("");
        List<String> grammarTokens = util.collectionMatch(outputOption.getGrammar(), util.GRAMMAR_REGEX);
        for (String grammarToken : grammarTokens) {
            if (StringUtils.startsWith(grammarToken, "'")) {
                outputString.append(StringUtils.strip(grammarToken, "'"));
            } else {
                // Perform lookups of each key name in the associative array to substitute the value of each
                // variable element, substituting the corresponding value in place of the key name.
                String variableElement = null;

                if ((variableElement = parameterMap.get(grammarToken)) == null) {
                    LOG.error("Undefined field {} (required by output)", grammarToken);
                    throw new TDTTranslationException("undefined field");
                }
                // Note that if the outbound representation is binary, it is necessary to convert values from
                // decimal integer or string to binary, performing any necessary stripping or padding,
                // following the method described in the flowchart Figure 9a.
                if (outputLevel.getType() == BINARY) {
                    // According to flowchart Figure 9a of the standard

                    // Corresponding string field in TAG-ENCODING level
                    Field tagEncodingField = new Field();
                    for (Level level : inputScheme.getLevel()) {
                        if (level.getType() == TAG_ENCODING) {
                            for (Option option : level.getOption()) {
                                if (option.getOptionKey() == inputOption.getOptionKey()) {
                                    for (Field field : option.getField()) {
                                        if (grammarToken.equals(field.getName())) {
                                            tagEncodingField = field;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    // Corresponding string field in BINARY level
                    Field binaryField = new Field();
                    for (Field field : outputOption.getField()) {
                        if (grammarToken.equals(field.getName())) {
                            binaryField = field;
                            break;
                        }
                    }

                    final boolean binaryFieldPadChar = StringUtils.isNotBlank(binaryField.getPadChar());
                    final boolean tagEncodingFieldPadChar = StringUtils.isNotBlank(tagEncodingField.getPadChar());

                    if (binaryFieldPadChar && tagEncodingFieldPadChar) {
                        LOG.error("padChar defined in both BINARY and TAG_ENCODING");
                        throw new TDTTranslationException("invalid definition");
                    }
                    // TODO: 6/29/16  check this because in the c# version it is checking the pad dir of the opposite
                    // see line 512 in TDTengine.cs
                    if (tagEncodingFieldPadChar) {
                        if (tagEncodingField.getPadDir() == LEFT) {
                            variableElement = StringUtils.stripStart(variableElement, tagEncodingField.getPadChar());
                        } else {
                            variableElement = StringUtils.stripEnd(variableElement, tagEncodingField.getPadChar());
                        }
                    }
                    if (binaryFieldPadChar) {
                        if (binaryField.getPadDir() == LEFT) {
                            variableElement = StringUtils.leftPad(variableElement, binaryField.getLength()
                                                                                              .intValue(),
                                    binaryField.getPadChar());
                        } else {
                            variableElement = StringUtils.rightPad(variableElement, binaryField.getLength()
                                                                                               .intValue(),
                                    binaryField.getPadChar());
                        }
                    }
                    StringBuilder bits = new StringBuilder("");
                    // Check for compaction attribute in BINARY level
                    if (StringUtils.isNotBlank(binaryField.getCompaction())) {
                        int compactionBits = util.getCompactionBits(binaryField);
                        byte[] bytes = variableElement.getBytes();
                        for (byte b : bytes) {
                            String binary = Integer.toBinaryString(b);
                            if (binary.length() > compactionBits) {
                                binary = StringUtils.substring(binary, binary.length() - compactionBits);
                            } else {
                                binary = StringUtils.leftPad(binary, compactionBits, "0");
                            }
                            bits.append(binary);
                        }
                        variableElement = bits.toString();
                    } else {
                        variableElement = Integer.toBinaryString(Integer.parseInt(variableElement));
                    }
                    // Check for bit padding in BINARY level
                    if (LEFT == binaryField.getBitPadDir()) {
                        variableElement = StringUtils.leftPad(variableElement, binaryField.getBitLength()
                                                                                          .intValue(), "0");
                    } else if (RIGHT == binaryField.getBitPadDir()) {
                        variableElement = StringUtils.rightPad(variableElement, binaryField.getBitLength()
                                                                                           .intValue(), "0");
                    }
                }
                if (outputLevel.getType() == PURE_IDENTITY || outputLevel.getType() == TAG_ENCODING) {
                    variableElement = util.escape(variableElement);
                }

                // Concatenate the fixed literal strings and values of variable together in the sequence
                // indicated by the grammar string and consider this as the output value.
                outputString.append(variableElement);
            }
        }
        LOG.info("TDT output: {}", outputString);

        return outputString.toString();
    }
}
