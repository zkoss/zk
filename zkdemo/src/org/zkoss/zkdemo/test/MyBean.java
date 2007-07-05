package org.zkoss.zkdemo.test;

import java.util.Arrays;
import java.util.Collection;

public class MyBean {
    private ComparisonType codeFilterType = ComparisonType.CONTAINS;
    private ComparisonType titleFilterType = ComparisonType.CONTAINS;
            
    public ComparisonType getCodeFilterType() {
        return codeFilterType;
    }

    public void setCodeFilterType(ComparisonType codeFilterType) {
        this.codeFilterType = codeFilterType;
    }

    public Collection getCodeFilterTypeValues() {
        return Arrays.asList(ComparisonType.values());
    }

    public ComparisonType getTitleFilterType() {
        return titleFilterType;
    }

    public void setTitleFilterType(ComparisonType titleFilterType) {
        this.titleFilterType = titleFilterType;
    }

    public Collection getTitleFilterTypeValues() {
        return Arrays.asList(ComparisonType.values());
    }
    
}
