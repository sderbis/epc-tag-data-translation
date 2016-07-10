package com.derbis;

import com.derbis.config.JacksonObjectMapper;
import com.derbis.tdt.GS1EpcTagDataTranslation;
import com.derbis.tdt.GS1EpcTagDataTranslationEngine;
import com.derbis.tdt.TDTRules;
import com.derbis.tdt.TDTUtil;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {
        GS1EpcTagDataTranslationEngine.class,
        GS1EpcTagDataTranslation.class,
        TDTRules.class,
        TDTUtil.class,
        JacksonObjectMapper.class})
public abstract class BaseSpringTest {
}
