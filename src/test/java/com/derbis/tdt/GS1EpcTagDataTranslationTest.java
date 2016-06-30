package com.derbis.tdt;

import com.derbis.BaseSpringTest;
import com.derbis.model.EpcTagDataTranslation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GS1EpcTagDataTranslationTest extends BaseSpringTest {

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