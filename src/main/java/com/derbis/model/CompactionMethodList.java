package com.derbis.model;

/**
 * This was created manually because Java does not support the values as enums
 * <p>
 * <pre></pre>
 * &lt;xsd:simpleType name="CompactionMethodList"&gt;
 * &lt;xsd:restriction base="xsd:string"&gt;
 * &lt;xsd:enumeration value="32-bit"/&gt;
 * &lt;xsd:enumeration value="16-bit"/&gt;
 * &lt;xsd:enumeration value="8-bit"/&gt;
 * &lt;xsd:enumeration value="7-bit"/&gt;
 * &lt;xsd:enumeration value="6-bit"/&gt;
 * &lt;xsd:enumeration value="5-bit"/&gt;
 * &lt;/xsd:restriction&gt;
 * &lt;/xsd:simpleType&gt;
 * </pre>;
 */
public enum CompactionMethodList {
    BIT32("32-bit", 32),
    BIT16("16-bit", 16),
    BIT8("8-bit", 8),
    BIT7("7-bit", 7),
    BIT6("6-bit", 6),
    BIT5("5-bit", 5);

    private String value;
    private int compactionBits;

    CompactionMethodList(String value, int compactionBits) {
        this.value = value;
        this.compactionBits = compactionBits;
    }

    public String getValue() {
        return value;
    }

    public int getCompactionBits() {
        return compactionBits;
    }
}
