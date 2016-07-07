package com.derbis.tdt;

import com.derbis.BaseTranslationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class GS1EpcTagDataExceptionTranslationTest extends BaseTranslationTest {

    @Autowired
    GS1EpcTagDataTranslationEngine engine;

    @Test
    public void testCaseExceptionTDTFieldAboveMaximum() {
        assertThatThrownBy(() ->
                engine.translate("gtin=00037000302414;serial=274877906944", PARAM_LIST, PURE_IDENTITY))
                .hasMessage("field above maximum");
    }

    @Test
    public void testCaseExceptionTDTUndefinedField() {
        assertThatThrownBy(() ->
                engine.translate("gtin=00037000302414;serial=1", PARAM_LIST_NO_FILTER, BINARY))
                .hasMessage("undefined field");
    }

    @Test
    public void testCaseExceptionTDTSchemeNotFound() {
        assertThatThrownBy(() ->
                engine.translate("glin=00037000302414;serial=-1", PARAM_LIST, PURE_IDENTITY))
                .hasMessage("no matching scheme");
    }

    @Test
    public void testCaseExceptionTDTOptionNotFound() {
        assertThatThrownBy(() ->
                engine.translate("gtin=00037000302414;serial=-1", PARAM_LIST, PURE_IDENTITY))
                .hasMessage("no input option");
    }

    @Test
    public void testCaseExceptionTDTOptionNotFound2() {
        assertThatThrownBy(() ->
                engine.translate("gtin=00037000302414;serial=$$", PARAM_LIST, PURE_IDENTITY))
                .hasMessage("no input option");
    }
}
