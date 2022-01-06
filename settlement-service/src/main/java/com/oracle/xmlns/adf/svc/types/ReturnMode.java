
package com.oracle.xmlns.adf.svc.types;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReturnMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReturnMode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Full"/&gt;
 *     &lt;enumeration value="Key"/&gt;
 *     &lt;enumeration value="None"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ReturnMode")
@XmlEnum
public enum ReturnMode {

    @XmlEnumValue("Full")
    FULL("Full"),
    @XmlEnumValue("Key")
    KEY("Key"),
    @XmlEnumValue("None")
    NONE("None");
    private final String value;

    ReturnMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReturnMode fromValue(String v) {
        for (ReturnMode c: ReturnMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
