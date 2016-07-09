package com.derbis;

import com.derbis.tdt.GS1EpcTagDataTranslationEngine;
import com.derbis.tdt.TDTTranslationException;
import com.derbis.tdt.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class EpcTagDataTranslationDemo implements CommandLineRunner {
    private static final Logger LOG = LogManager.getLogger(EpcTagDataTranslationDemo.class);

    @Autowired
    private GS1EpcTagDataTranslationEngine engine;

    @Autowired
    private Util util;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(EpcTagDataTranslationDemo.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        String parameterList = "filter=3;gs1companyprefixlength=7;tagLength=96";
        List<String> epcIdentifiers = new ArrayList<>();

        epcIdentifiers.add("gtin=00037000302414;serial=10419703");
        epcIdentifiers.add("gln=0003700030247;serial=1041970");
        epcIdentifiers.add("grai=00037000302414274877906943");
        epcIdentifiers.add("giai=123456789012312345");
        epcIdentifiers.add("generalmanager=5;objectclass=17;serial=23");
        epcIdentifiers.add("cageordodaac=AB123;serial=3789156");

        epcIdentifiers.forEach(epcIdentifier -> {
            try {
                LOG.info("Translating {} to outputFormat BINARY", epcIdentifier);
                String result = util.binaryToHex(engine.translate(epcIdentifier, parameterList, "BINARY"));
                LOG.info("Result is: {}", result);

                LOG.info("Translating {} to outputFormat LEGACY", result);
                String result2 = engine.translate(util.hexToBinary(result, 0), parameterList, "LEGACY");
                LOG.info("Result is: {}", result2);
            } catch (TDTTranslationException e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }
}
