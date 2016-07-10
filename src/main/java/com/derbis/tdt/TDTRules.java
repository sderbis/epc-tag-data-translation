package com.derbis.tdt;

import com.derbis.model.ModeList;
import com.derbis.model.Rule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TDTRules {
    private static Logger LOG = LogManager.getLogger(TDTRules.class);

    @Autowired
    TDTUtil util;

    public void execute(List<Rule> rules, ModeList mode, Map<String, String> parameterMap)
            throws TDTTranslationException {
        if (CollectionUtils.isEmpty(rules)) {
            return;
        }
        for (Rule rule : rules) {
            if (rule.getType() != mode) {
                continue;
            }
            String[] functionSplit = StringUtils.split(rule.getFunction(), "(,)", 128);
            String functionName = functionSplit[0];
            String[] functionParams = ArrayUtils.remove(functionSplit, 0);
            int numParams = functionParams.length;

            String newFieldValue = null;

            switch (functionName) {
                case "SUBSTR":
                    if (numParams == 2 || numParams == 3) {
                        String inputString = parameterMap.get(functionParams[0]);
                        int offset = Integer.parseInt(util.getValue(functionParams[1], parameterMap));
                        if (numParams == 2) {
                            newFieldValue = StringUtils.substring(inputString, offset);
                        } else {
                            int length = Integer.parseInt(util.getValue(functionParams[2], parameterMap));
                            newFieldValue = StringUtils.substring(inputString, offset, offset + length);
                        }
                    }
                    break;
                case "CONCAT":
                    StringBuilder concatString = new StringBuilder("");
                    for (String functionParam : functionParams) {
                        concatString.append(util.getValue(functionParam, parameterMap));
                    }
                    newFieldValue = concatString.toString();
                    break;
                case "GS1CHECKSUM":
                    newFieldValue = util.computeGS1Checksum(util.getValue(functionParams[0], parameterMap));
                    break;
                default:
                    LOG.error("function {} not implemented", functionName);
                    throw new TDTTranslationException("function not implemented");
            }
            // Store the resulting key-value pairs in the associative array after checking that the value
            // falls entirely within the permitted characterSet (if specified)
            if (!util.validateCharacterSet(newFieldValue, rule.getCharacterSet())) {
                LOG.error("character set validation error, {} does not match {}",
                        newFieldValue, rule.getCharacterSet());
                throw new TDTTranslationException("character set mismatch");
            }
            // Check min and max
            if (!util.validateMinimum(newFieldValue, rule.getDecimalMinimum())) {
                LOG.error("{} lower than minimum {}", newFieldValue, rule.getDecimalMinimum());
                throw new TDTTranslationException("field below minimum");
            }
            if (!util.validateMaximum(newFieldValue, rule.getDecimalMaximum())) {
                LOG.error("{} larger than maximum {}", newFieldValue, rule.getDecimalMaximum());
                throw new TDTTranslationException("field above maximum");
            }

            LOG.info("function {} new field {} now has value {}", functionName, rule.getNewFieldName(), newFieldValue);
            parameterMap.put(rule.getNewFieldName(), newFieldValue);
        }
    }
}
