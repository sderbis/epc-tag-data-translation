package com.derbis;

public class BaseTranslationTest extends BaseSpringTest {
    public static final String BINARY = "BINARY";
    public static final String TAG_ENCODING = "TAG_ENCODING";
    public static final String PURE_IDENTITY = "PURE_IDENTITY";
    public static final String LEGACY = "LEGACY";
    public static final String TEI = "TEI";

    public static final String PARAM_LIST = "filter=3;gs1companyprefixlength=7;tagLength=96";
    public static final String PARAM_LIST_NO_FILTER = "gs1companyprefixlength=7;tagLength=96";

    public static final String PAGE_13_EPC = "gtin=00037000302414;serial=1041970";
    public static final String PAGE_13_EXPECT_1 = "001100000111010000000010010000100010000000011101100010000100000000000000000011111110011000110010";
    public static final String PAGE_13_EXPECT_2 = "urn:epc:tag:sgtin-96:3.0037000.030241.1041970";
    public static final String PAGE_13_EXPECT_3 = "urn:epc:id:sgtin:0037000.030241.1041970";

    public static final String PAGE_26_EPC_1 = "gtin=00037000302414;serial=10419703";
    public static final String PAGE_26_EPC_2 = "gln=0003700030247;serial=1041970";
    public static final String PAGE_26_EPC_3 = "grai=00037000302414274877906943";
    public static final String PAGE_26_EPC_4 = "giai=00370003024149267890123";
    public static final String PAGE_26_EPC_5 = "generalmanager=5;objectclass=17;serial=23";
    public static final String PAGE_26_EPC_6 = "cageordodaac=AB123;serial=3789156";
}
