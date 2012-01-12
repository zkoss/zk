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

/**
 * ELResolver for {@link ListModel}.
 * @author henrichen
 * @since 6.0.0
 */
public class ListModelELResolver extends ELResolver {
    @Override
    public Object getValue(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base instanceof ListModel<?>) {
            context.setPropertyResolved(true);
            ListModel<?> listmodel = (ListModel<?>) base;
            int idx = coerce(property);
            if (idx < 0 || idx >= listmodel.getSize()) {
                return null;
            }
            return listmodel.getElementAt(idx);
        }

        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base instanceof ListModel<?>) {
            context.setPropertyResolved(true);
            ListModel<?> listmodel = (ListModel<?>) base;
            int idx = coerce(property);
            if (idx < 0 || idx >= listmodel.getSize()) {
                throw new PropertyNotFoundException(
                        new ArrayIndexOutOfBoundsException(idx).getMessage());
            }
            return Object.class;
        }

        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property,
            Object value) throws NullPointerException,
            PropertyNotFoundException, PropertyNotWritableException,
            ELException {
        if (context == null) {
            throw new NullPointerException();
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
    	return true;
    }

    @Override
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

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof ListModel<?>) { // implies base != null
            return Integer.class;
        }
        return null;
    }

    private static final int coerce(Object property) {
        if (property instanceof Number) {
            return ((Number) property).intValue();
        }
        if (property instanceof Character) {
            return ((Character) property).charValue();
        }
        if (property instanceof Boolean) {
            return (((Boolean) property).booleanValue() ? 1 : 0);
        }
        if (property instanceof String) {
            return Integer.parseInt((String) property);
        }
        throw new IllegalArgumentException(property != null ?
                property.toString() : "null");
    }
}
