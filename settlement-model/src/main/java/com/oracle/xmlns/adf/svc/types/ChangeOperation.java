
package com.oracle.xmlns.adf.svc.types;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChangeOperation.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChangeOperation"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Create"/&gt;
 *     &lt;enumeration value="Update"/&gt;
 *     &lt;enumeration value="Merge"/&gt;
 *     &lt;enumeration value="Delete"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ChangeOperation")
@XmlEnum
public enum ChangeOperation {

    @XmlEnumValue("Create")
    CREATE("Create"),
    @XmlEnumValue("Update")
    UPDATE("Update"),
    @XmlEnumValue("Merge")
    MERGE("Merge"),
    @XmlEnumValue("Delete")
    DELETE("Delete");
    private final String value;

    ChangeOperation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ChangeOperation fromValue(String v) {
        for (ChangeOperation c: ChangeOperation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
