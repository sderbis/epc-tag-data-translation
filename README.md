## Synopsis

Tag Data Translation implemented according to the GS1 EPC Tag Data Translation 1.6 specification (http://www.gs1.org/epc/tag-data-translation-standard) for [RAIN RFID](http://rainrfid.org).

This is a Java port of the C# [TagDataTranslation project](https://github.com/dannyhaak/TagDataTranslation)

* Spring Boot Application
* Java 8
* Maven

### Build Status
[![Build Status](https://travis-ci.org/sderbis/epc-tag-data-translation.svg?branch=master)](https://travis-ci.org/sderbis/epc-tag-data-translation)

### Usage
This project leverages the Spring framework.  To invoke a translation, simply autowire the GS1EpcTagDataTranslationEngine
and TDTUtil components
<p>
```java
@Autowired
private GS1EpcTagDataTranslationEngine engine;
@Autowired
private TDTUtil util;
```
<p>
invoke the translate method and convert to hex
<p>
```java
String epcIdentifier = "gtin=00037000302414;serial=10419703";
String parameterList = "filter=3;gs1companyprefixlength=7;tagLength=96";
String result = engine.translate(epcIdentifier, parameterList, "BINARY");
String hex = util.binaryToHex(result);
```
