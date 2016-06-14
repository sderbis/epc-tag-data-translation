package com.derbis.tdt;

import com.derbis.config.JacksonObjectMapper;
import com.derbis.model.EpcTagDataTranslation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {GS1EpcTagDataTranslation.class, JacksonObjectMapper.class})
public class GS1EpcTagDataTranslationTest {

    @Autowired
    GS1EpcTagDataTranslation gs1EpcTagDataTranslation;

    @Test
    public void testXML() throws Exception {
        List<EpcTagDataTranslation> epcTagDataTranslations = gs1EpcTagDataTranslation.loadXMLResources();

        assertThat(epcTagDataTranslations).isNotEmpty();
        assertThat(epcTagDataTranslations.stream()
                                         .allMatch(epcTagDataTranslation ->
                                                 epcTagDataTranslation.getScheme()
                                                                      .size() == 1)).isTrue();

        List<String> schemeXMLs = gs1EpcTagDataTranslation.getSchemeXMLs();
        epcTagDataTranslations.forEach(epcTagDataTranslation ->
                assertThat(epcTagDataTranslation.getScheme()
                                                .get(0)
                                                .getName() + ".xml").isIn(schemeXMLs));
    }
}