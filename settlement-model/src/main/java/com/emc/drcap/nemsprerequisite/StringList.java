
package com.emc.drcap.nemsprerequisite;

import java.util.ArrayList;
import java.util.List;


public class StringList  implements java.io.Serializable{

    protected List<String> element2;

  
    public List<String> getElement2() {
        if (element2 == null) {
            element2 = new ArrayList<String>();
        }
        return this.element2;
    }

    public void setElement2(List<String> element2) {
        this.element2 = element2;
    }
}
