/* ListModelELResolver.java

	Purpose:
		
	Description:
		
	History:
		Jan 12, 2012 3:18:34 PM, Created by henrichen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.Iterator;

import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;

/**
 * ELResolver for {@link ListModel}.
 * @author henrichen
 * @since 6.0.0
 */
public class ListModelELResolver extends ELResolver {
    
    public Object getValue(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base instanceof ListModel<?>) {
            ListModel<?> listmodel = (ListModel<?>) base;
            Integer idx = coerce(property);
            if (idx==null) { // property is not a legal number format
                return null; // unresolved null
            }
            context.setPropertyResolved(true);
            if (idx >= 0 && idx < listmodel.getSize()) {
            	return listmodel.getElementAt(idx);
            }
            //out of range, a resolved null
        }

        return null;
    }

    
    public Class<?> getType(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base instanceof ListModel<?>) {
            ListModel<?> listmodel = (ListModel<?>) base;
            Integer idx = coerce(property);
            if (idx == null) return null;
            if (idx < 0 || idx >= listmodel.getSize()) {
                throw new PropertyNotFoundException(
                        new ArrayIndexOutOfBoundsException(idx).getMessage());
            }
            context.setPropertyResolved(true);
            return Object.class;
        }

        return null;
    }

    
    public void setValue(ELContext context, Object base, Object property,
            Object value) throws NullPointerException,
            PropertyNotFoundException, PropertyNotWritableException,
            ELException {
        if (context == null) {
            throw new NullPointerException();
        }
        
        if (base instanceof ListModel<?>) {
            ListModel<?> listmodel = (ListModel<?>) base;
            Integer idx = coerce(property);
            if (idx==null) { // property is not a legal number format
                return ; // unresolved null
            }
            context.setPropertyResolved(true);
            
            //ZK-1960 save back to listmodel
            if (idx >= 0 && idx < listmodel.getSize()) {
            	if(base instanceof ListModelArray){
            		((ListModelArray)base).set(idx, value);
            	}else if(base instanceof ListModelList<?>){
            		((ListModelList)base).set(idx, value);
            	}else{
            		throw new PropertyNotWritableException("can't write property "+property+" to ListModel:"+base);
            	}
            }else{
            	//out of range, should ignore to compatible with old version(when we didn't implement save) or throw exception?
            }
        }
    }

    
    public boolean isReadOnly(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
    	return true;
    }

    
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base instanceof ListModel<?>) {
            FeatureDescriptor[] descs = new FeatureDescriptor[((ListModel<?>) base).getSize()];
            for (int i = 0; i < descs.length; i++) {
                descs[i] = new FeatureDescriptor();
                descs[i].setDisplayName("["+i+"]");
                descs[i].setExpert(false);
                descs[i].setHidden(false);
                descs[i].setName(""+i);
                descs[i].setPreferred(true);
                descs[i].setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.FALSE);
                descs[i].setValue(TYPE, Integer.class);
            }
            return Arrays.asList(descs).iterator();
        }
        return null;
    }

    
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof ListModel<?>) { // implies base != null
            return Integer.class;
        }
        return null;
    }

    private static final Integer coerce(Object property) {
    	//should only handle a property that is possible a number
        if (property instanceof Number) {
            return ((Number)property).intValue();
        }
        if (property instanceof Character) {
            return (int)((Character) property).charValue();
        }
        if (property instanceof Boolean) {
            return (((Boolean) property).booleanValue() ? 1 : 0);
        }
        if (property instanceof String) {
        	//follow EL spec.: String in number format for Array is number
        	try {
        		return Integer.parseInt((String) property);
        	} catch(NumberFormatException ex) {
        		//ignore
        	}
        }
        //just ignore other types (especially a string)
        return null;
    }
}
