package com.derbis.tdt;

import com.derbis.BaseTranslationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class GS1EpcTagDataCustomTranslationTest extends BaseTranslationTest {

    @Autowired
    GS1EpcTagDataTranslationEngine engine;

    @Autowired
    TDTUtil util;

    private void testTranslationSequence(String epcIdentifier, String parameterList, String hex, int binaryDigitLimit,
                                         String tagEncoding, String pureIdentity, String outputFormat)
            throws TDTTranslationException {

        String result1 = engine.translate(epcIdentifier, parameterList, BINARY);
        assertThat(result1).isEqualTo(util.hexToBinary(hex, binaryDigitLimit));

        String result2 = engine.translate(epcIdentifier, parameterList, TAG_ENCODING);
        assertThat(result2).isEqualTo(tagEncoding);

        String result3 = engine.translate(result2, parameterList, PURE_IDENTITY);
        assertThat(result3).isEqualTo(pureIdentity);

        String result4 = engine.translate(result3, parameterList, outputFormat);
        assertThat(epcIdentifier).isEqualTo(result4);

        String result5 = engine.translate(result1, parameterList, outputFormat);
        assertThat(epcIdentifier).isEqualTo(result5);
    }

    private void testTranslationSequenceForBinaryToHex(String epcIdentifier, String parameterList, String hex,
                                                       String tagEncoding, String pureIdentity)
            throws TDTTranslationException {

        String result1 = engine.translate(epcIdentifier, parameterList, BINARY);
        assertThat(util.binaryToHex(result1)).isEqualTo(hex);

        String result2 = engine.translate(epcIdentifier, parameterList, TAG_ENCODING);
        assertThat(result2).isEqualTo(tagEncoding);

        String result3 = engine.translate(result2, parameterList, PURE_IDENTITY);
        assertThat(result3).isEqualTo(pureIdentity);

        String result4 = engine.translate(result3, parameterList, LEGACY);
        assertThat(epcIdentifier).isEqualTo(result4);

        String result5 = engine.translate(result1, parameterList, LEGACY);
        assertThat(epcIdentifier).isEqualTo(result5);
    }

    @Test
    public void testSSCC96() throws TDTTranslationException {
        testTranslationSequence("sscc=012345678901234560",
                "filter=0;gs1companyprefixlength=8;tagLength=96",
                "31105E30A7055F2CC0000000",
                0,
                "urn:epc:tag:sscc-96:0.12345678.090123456",
                "urn:epc:id:sscc:12345678.090123456",
                LEGACY);
    }

    @Test
    public void testSGLN96() throws TDTTranslationException {
        testTranslationSequence("gln=1234567890128;serial=1234567890",
                "filter=1;gs1companyprefixlength=8;tagLength=96",
                "32305E30A7466800499602D2",
                0,
                "urn:epc:tag:sgln-96:1.12345678.9012.1234567890",
                "urn:epc:id:sgln:12345678.9012.1234567890",
                LEGACY);
    }

    @Test
    public void testSGLN195() throws TDTTranslationException {
        testTranslationSequence("gln=1234567890128;serial=ABCDEF!&1=2",
                "filter=1;gs1companyprefixlength=8;tagLength=195",
                "39305E30A746690614389163214CC5EB20000000000000000000",
                195,
                "urn:epc:tag:sgln-195:1.12345678.9012.ABCDEF!%261=2",
                "urn:epc:id:sgln:12345678.9012.ABCDEF!%261=2",
                LEGACY);
    }

    @Test
    public void testGRAI96() throws TDTTranslationException {
        testTranslationSequence("grai=012345678901281",
                "filter=0;gs1companyprefixlength=9;tagLength=96",
                "330C75BCD150030000000001",
                0,
                "urn:epc:tag:grai-96:0.123456789.012.1",
                "urn:epc:id:grai:123456789.012.1",
                LEGACY);
    }

    @Test
    public void testGRAI170() throws TDTTranslationException {
        testTranslationSequence("grai=01234567890128ABcdE!GGH-;&*a%",
                "filter=7;gs1companyprefixlength=11;tagLength=170",
                "37E45BFB8386A0A0C2C7922A18F1E42D76995614A000",
                170,
                "urn:epc:tag:grai-170:7.12345678901.2.ABcdE!GGH-;%26*a%25",
                "urn:epc:id:grai:12345678901.2.ABcdE!GGH-;%26*a%25",
                LEGACY);
    }

    @Test
    public void testGIAI96() throws TDTTranslationException {
        testTranslationSequence("giai=12345671",
                "filter=1;gs1companyprefixlength=7;tagLength=96",
                "34344B5A1C00000000000001",
                0,
                "urn:epc:tag:giai-96:1.1234567.1",
                "urn:epc:id:giai:1234567.1",
                LEGACY);
    }

    @Test
    public void testGIAI202() throws TDTTranslationException {
        testTranslationSequence("giai=12345671AaBbKkZz!?%2225%%''%",
                "filter=1;gs1companyprefixlength=7;tagLength=202",
                "38344B5A1D8C1C30B14BD76BD217E9593264D52A54E9D2800000",
                202,
                "urn:epc:tag:giai-202:1.1234567.1AaBbKkZz!%3F%252225%25%25''%25",
                "urn:epc:id:giai:1234567.1AaBbKkZz!%3F%252225%25%25''%25",
                LEGACY);
    }

    @Test
    public void testGSRN96() throws TDTTranslationException {
        testTranslationSequence("gsrn=012345678901234560",
                "filter=0;gs1companyprefixlength=10;tagLength=96",
                "2D08075BCD1501E240000000",
                0,
                "urn:epc:tag:gsrn-96:0.0123456789.0123456",
                "urn:epc:id:gsrn:0123456789.0123456",
                LEGACY);
    }

    @Test
    public void testGDTI96() throws TDTTranslationException {
        testTranslationSequence("gdti=01234567890123445678",
                "filter=1;gs1companyprefixlength=10;tagLength=96",
                "2C28075BCD150200003493AE",
                0,
                "urn:epc:tag:gdti-96:1.0123456789.01.3445678",
                "urn:epc:id:gdti:0123456789.01.3445678",
                LEGACY);
    }

    @Test
    public void testGDTI113() throws TDTTranslationException {
        testTranslationSequenceForBinaryToHex("gdti=01234567890123445678",
                "filter=1;gs1companyprefixlength=10;tagLength=113",
                "3A28075BCD1502000000006695170000",
                "urn:epc:tag:gdti-113:1.0123456789.01.3445678",
                "urn:epc:id:gdti:0123456789.01.3445678");
    }

    @Test
    public void testGID96() throws TDTTranslationException {
        testTranslationSequence("generalmanager=1234;objectclass=1234;serial=1234",
                "tagLength=96",
                "3500004D20004D20000004D2",
                0,
                "urn:epc:tag:gid-96:1234.1234.1234",
                "urn:epc:id:gid:1234.1234.1234",
                LEGACY);
    }

    @Test
    public void testDOD96() throws TDTTranslationException {
        testTranslationSequence("cageordodaac=99ABH;serial=123",
                "filter=0;tagLength=96",
                "2F020393941424800000007B",
                0,
                "urn:epc:tag:usdod-96:0.99ABH.123",
                "urn:epc:id:usdod:99ABH.123",
                LEGACY);
    }

    @Test
    public void testADIvar() throws TDTTranslationException {
        testTranslationSequence("ADI CAG 99AH2/PNO 123/SEQ 145",
                "filter=1;tagLength=98",
                "3B060E79048CB1CB3031D3500000",
                98,
                "urn:epc:tag:adi-var:1.99AH2.123.145",
                "urn:epc:id:adi:99AH2.123.145",
                TEI);
    }

    @Test
    public void testSGTIN96() throws TDTTranslationException {
        testTranslationSequence("gtin=08710966610350;serial=1606",
                "filter=1;gs1companyprefixlength=7;tagLength=96",
                "303613ACD83B9AC000000646",
                0,
                "urn:epc:tag:sgtin-96:1.8710966.061035.1606",
                "urn:epc:id:sgtin:8710966.061035.1606",
                LEGACY);
    }

    @Test
    public void testSGTIN198() throws TDTTranslationException {
        testTranslationSequence("gtin=08710966610350;serial=%2522",
                "filter=1;gs1companyprefixlength=7;tagLength=198",
                "363613ACD83B9AD2B26AC9900000000000000000000000000000",
                198,
                "urn:epc:tag:sgtin-198:1.8710966.061035.%252522",
                "urn:epc:id:sgtin:8710966.061035.%252522",
                LEGACY);
    }
}
