package com.derbis.rules;

import com.derbis.schema.EpcTagDataTranslation;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
public class GS1RulesParser {

    @Autowired
    XmlMapper xmlMapper;

    public List<EpcTagDataTranslation> parse() {
        List<EpcTagDataTranslation> epcTagDataTranslations = new ArrayList<>();

        try {
            Resource[] resources = getResources();

            for (Resource resource : resources) {
                epcTagDataTranslations.add(xmlMapper.readValue(resource.getInputStream(), EpcTagDataTranslation.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return epcTagDataTranslations;
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
            e.printStackTrace();
        }

        return schemas;
    }

    private Resource[] getResources() throws IOException {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:/gs1-tdt/**/*.xml");
    }
}
