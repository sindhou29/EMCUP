
package com.oracle.xmlns.adf.svc.types;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ObjAttrHints complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObjAttrHints"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="objHint" type="{http://xmlns.oracle.com/adf/svc/types/}CtrlHint" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="attrHints" type="{http://xmlns.oracle.com/adf/svc/types/}AttrCtrlHints" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjAttrHints", propOrder = {
    "objHint",
    "attrHints"
})
public class ObjAttrHints {

    protected List<CtrlHint> objHint;
    @XmlElement(required = true)
    protected List<AttrCtrlHints> attrHints;

    /**
     * Gets the value of the objHint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objHint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObjHint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CtrlHint }
     * 
     * 
     */
    public List<CtrlHint> getObjHint() {
        if (objHint == null) {
            objHint = new ArrayList<CtrlHint>();
        }
        return this.objHint;
    }

    /**
     * Gets the value of the attrHints property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attrHints property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttrHints().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttrCtrlHints }
     * 
     * 
     */
    public List<AttrCtrlHints> getAttrHints() {
        if (attrHints == null) {
            attrHints = new ArrayList<AttrCtrlHints>();
        }
        return this.attrHints;
    }

}
