package com.derbis.tdt;

import com.derbis.BaseTranslationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class GS1EpcTagDataTimingTranslationTest extends BaseTranslationTest {

    StopWatch stopWatch;

    @Autowired
    GS1EpcTagDataTranslationEngine engine;

    @Before
    public void init() {
        stopWatch = new StopWatch();
    }

    @Test
    public void testInit() {
        stopWatch.start();
        engine.init();
        stopWatch.stop();
        assertThat(stopWatch.getTotalTimeMillis()).isLessThanOrEqualTo(400);
        System.out.println("engine started in " + stopWatch.getTotalTimeMillis() + " milliseconds");
    }

    @Test
    public void testTenThousandTranslations() {
        Random random = new Random();
        List<String> epcIdentifiers = new ArrayList<>();
        for (int i = 0; i < 10000; ++i) {
            epcIdentifiers.add("gtin=08710966610350;serial=" + random.nextInt(1000000));
        }
        stopWatch.start();
        epcIdentifiers.forEach(epcIdentifier -> {
            try {
                String resultBinary = engine.translate(epcIdentifier, PARAM_LIST, BINARY);
                String resultPureIdentity = engine.translate(resultBinary, PARAM_LIST, PURE_IDENTITY);
                System.out.println("results: " + resultBinary + " " + resultPureIdentity);
            } catch (TDTTranslationException e) {
                e.printStackTrace();
            }
        });
        stopWatch.stop();
        assertThat(stopWatch.getTotalTimeMillis()).isLessThanOrEqualTo(10000);
        System.out.println("10,000 translations completed in " + stopWatch.getTotalTimeMillis() + " milliseconds");
    }
}
