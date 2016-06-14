//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.06.14 at 02:01:55 PM EDT 
//


package com.derbis.schema;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.derbis.schema package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EpcTagDataTranslation_QNAME = new QName("urn:epcglobal:tdt:xsd:1", "epcTagDataTranslation");
    private final static QName _GEPC64Table_QNAME = new QName("urn:epcglobal:tdt:xsd:1", "GEPC64Table");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.derbis.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EpcTagDataTranslation }
     * 
     */
    public EpcTagDataTranslation createEpcTagDataTranslation() {
        return new EpcTagDataTranslation();
    }

    /**
     * Create an instance of {@link GEPC64 }
     * 
     */
    public GEPC64 createGEPC64() {
        return new GEPC64();
    }

    /**
     * Create an instance of {@link Scheme }
     * 
     */
    public Scheme createScheme() {
        return new Scheme();
    }

    /**
     * Create an instance of {@link Level }
     * 
     */
    public Level createLevel() {
        return new Level();
    }

    /**
     * Create an instance of {@link Option }
     * 
     */
    public Option createOption() {
        return new Option();
    }

    /**
     * Create an instance of {@link Rule }
     * 
     */
    public Rule createRule() {
        return new Rule();
    }

    /**
     * Create an instance of {@link Field }
     * 
     */
    public Field createField() {
        return new Field();
    }

    /**
     * Create an instance of {@link GEPC64Entry }
     * 
     */
    public GEPC64Entry createGEPC64Entry() {
        return new GEPC64Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EpcTagDataTranslation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:tdt:xsd:1", name = "epcTagDataTranslation")
    public JAXBElement<EpcTagDataTranslation> createEpcTagDataTranslation(EpcTagDataTranslation value) {
        return new JAXBElement<EpcTagDataTranslation>(_EpcTagDataTranslation_QNAME, EpcTagDataTranslation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GEPC64 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:tdt:xsd:1", name = "GEPC64Table")
    public JAXBElement<GEPC64> createGEPC64Table(GEPC64 value) {
        return new JAXBElement<GEPC64>(_GEPC64Table_QNAME, GEPC64 .class, null, value);
    }

}
