
package com.emc.drcap.nemsprerequisite;

import com.emc.drcap.nemsprerequisite.StringList;

import java.util.ArrayList;
import java.util.List;


public class StringListList  implements java.io.Serializable{

    protected List<StringList> element1;

   
    public List<StringList> getElement1() {
        if (element1 == null) {
            element1 = new ArrayList<StringList>();
        }
        return this.element1;
    }

    public void setElement1(List<StringList> element1) {
        this.element1 = element1;
    }
}
