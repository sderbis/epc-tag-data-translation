package com.derbis.tdt;

import com.derbis.BaseTranslationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GS1EpcTagDataStandardTranslationTest extends BaseTranslationTest {

    @Autowired
    GS1EpcTagDataTranslationEngine engine;

    @Test
    public void testCasePage13TDTStandard() throws TDTTranslationException {
        assertThat(engine.translate(PAGE_13_EPC, PARAM_LIST, BINARY)).isEqualTo(PAGE_13_EXPECT_1);
        assertThat(engine.translate(PAGE_13_EPC, PARAM_LIST, TAG_ENCODING)).isEqualTo(PAGE_13_EXPECT_2);
        assertThat(engine.translate(PAGE_13_EPC, PARAM_LIST, PURE_IDENTITY)).isEqualTo(PAGE_13_EXPECT_3);
        assertThat(engine.translate(PAGE_13_EPC, PARAM_LIST, LEGACY)).isEqualTo(PAGE_13_EPC);
    }

    @Test
    public void testCasePage26TDTStandard() throws TDTTranslationException {
        List<String> epcIdentifiers = Arrays.asList(PAGE_26_EPC_1, PAGE_26_EPC_2, PAGE_26_EPC_3, PAGE_26_EPC_4,
                PAGE_26_EPC_5, PAGE_26_EPC_6);

        for (String epcIdentifier : epcIdentifiers) {
            String result1 = engine.translate(epcIdentifier, PARAM_LIST, BINARY);
            String result2 = engine.translate(result1, PARAM_LIST, LEGACY);
            assertThat(epcIdentifier).isEqualTo(result2);
        }
    }
}
