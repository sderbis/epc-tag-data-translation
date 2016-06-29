package com.derbis.tdt;

import com.derbis.model.EpcTagDataTranslation;
import com.derbis.model.Scheme;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GS1EpcTagDataTranslation {
    private static final Logger LOG = LogManager.getLogger(GS1EpcTagDataTranslation.class);

    @Autowired
    XmlMapper xmlMapper;

    public List<EpcTagDataTranslation> loadXMLResources() {
        List<EpcTagDataTranslation> epcTagDataTranslations = new ArrayList<>();

        try {
            Resource[] resources = getResources();
            LOG.info("Found {} GS1 tag data translation xml files", resources.length);

            for (Resource resource : resources) {
                EpcTagDataTranslation epcTagDataTranslation =
                        xmlMapper.readValue(resource.getInputStream(), EpcTagDataTranslation.class);
                if (validateEpcTagDataTranslation(epcTagDataTranslation)) {
                    epcTagDataTranslations.add(epcTagDataTranslation);
                    LOG.info("loaded {} version {} date {} ",
                            epcTagDataTranslation.getScheme()
                                                 .get(0)
                                                 .getName(),
                            epcTagDataTranslation.getVersion(),
                            epcTagDataTranslation.getDate());
                } else {
                    LOG.error("invalid tag data translation xml file: {}", resource.getFilename());
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return epcTagDataTranslations;
    }

    private boolean validateEpcTagDataTranslation(EpcTagDataTranslation epcTagDataTranslation) {
        List<Scheme> schemes = epcTagDataTranslation.getScheme();
        return CollectionUtils.isNotEmpty(schemes);
    }

    public List<String> getSchemeXMLs() {
        List<String> schemas = new ArrayList<>();

        try {
            schemas = Arrays.asList(getResources())
                            .stream()
                            .map(Resource::getFilename)
                            .collect(Collectors
                                    .toList());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return schemas;
    }

    private Resource[] getResources() throws IOException {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:/gs1-tdt/**/*.xml");
    }
}
