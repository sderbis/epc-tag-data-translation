//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.14 at 02:01:55 PM EDT 
//


package com.derbis.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LevelTypeList.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LevelTypeList"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BINARY"/&gt;
 *     &lt;enumeration value="TAG_ENCODING"/&gt;
 *     &lt;enumeration value="PURE_IDENTITY"/&gt;
 *     &lt;enumeration value="LEGACY"/&gt;
 *     &lt;enumeration value="LEGACY_ALT"/&gt;
 *     &lt;enumeration value="LEGACY_AI"/&gt;
 *     &lt;enumeration value="ELEMENT_STRING"/&gt;
 *     &lt;enumeration value="TEI"/&gt;
 *     &lt;enumeration value="ONS_HOSTNAME"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "LevelTypeList", namespace = "urn:epcglobal:tdt:xsd:1")
@XmlEnum
public enum LevelTypeList {

    BINARY,
    TAG_ENCODING,
    PURE_IDENTITY,
    LEGACY,
    LEGACY_ALT,
    LEGACY_AI,
    ELEMENT_STRING,
    TEI,
    ONS_HOSTNAME;

    public String value() {
        return name();
    }

    public static LevelTypeList fromValue(String v) {
        return valueOf(v);
    }

}
