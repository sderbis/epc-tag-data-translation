package com.derbis.tdt;

import com.derbis.config.JacksonObjectMapper;
import com.derbis.model.LevelTypeList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {
        GS1EpcTagDataTranslationEngine.class,
        GS1EpcTagDataTranslation.class,
        Rules.class,
        Util.class,
        JacksonObjectMapper.class})
public class GS1EpcTagDataTranslationEngineTest {

    @Autowired
    GS1EpcTagDataTranslationEngine gs1EpcTagDataTranslationEngine;

    @Test
    public void translate() throws Exception {
        gs1EpcTagDataTranslationEngine.translate(
                "gtin=00037000302414;serial=10419703", "filter=3;gs1companyprefixlength=7;tagLength=96", "BINARY");
    }

    @Test
    public void testLevelTypeList() {
        assertThat(LevelTypeList.fromValue("BINARY")
                                .name()).isEqualTo("BINARY");
        assertThatThrownBy(() -> LevelTypeList.fromValue("XXXXXX")).hasMessageContaining("No enum constant");
    }
}