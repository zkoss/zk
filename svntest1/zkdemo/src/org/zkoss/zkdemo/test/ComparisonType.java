package org.zkoss.zkdemo.test;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.criterion.MatchMode;

public class ComparisonType {
    private ComparisonType(String name) {
        this.name =name;
        values.add(this);
    }
    
    private final String name;
        
    public String toString() {
        return name;
    }

    public static Object[] values() {
        return values.toArray();
    }
    private static Collection values = new ArrayList();
    
    public static final ComparisonType EXACT = new ComparisonType("EXACT");
    public static final ComparisonType STARTS = new ComparisonType("STARTS");
    public static final ComparisonType CONTAINS = new ComparisonType("CONTAINS");
    
}
