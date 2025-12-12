/* B103_ZK_5683DataBean.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 11 16:37:12 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

public class B103_ZK_5683DataBean {

    private int id;
    private String label;
    private int index = 0;

    public B103_ZK_5683DataBean(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return index <= 0 ? label : label + index;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void incIndex() {
        index++;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        B103_ZK_5683DataBean other = (B103_ZK_5683DataBean) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
