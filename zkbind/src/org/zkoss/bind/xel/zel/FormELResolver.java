/* FormELResolver.java

	Purpose:
		
	Description:
		
	History:
		Aug 10, 2011 4:56:21 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.bind.Form;
import org.zkoss.bind.impl.Path;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;

/**
 * ELResolver for {@link Form}.
 * @author henrichen
 * @since 6.0.0
 */
public class FormELResolver extends ELResolver {
    @Override
    public Object getValue(ELContext ctx, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if (ctx == null) {
            throw new NullPointerException();
        }
        if (base instanceof Form) {
        	//don't care the property, at there, get the path (the key of field in form), the path was built by PathResolver) 
        	final int nums = ((Integer) ctx.getContext(Integer.class)).intValue(); //get numOfKids, see #PathResolver
        	final Path path = getPathList(ctx); //get path, see #PathResolver
        	
            ctx.setPropertyResolved(true);
            if (nums == 0) { //last property
            	final String fieldName = path.getAccessFieldName();
            	return ((Form) base).getField(fieldName);
            } else {
            	return base; //allow FORM resolving to continue!
            }
        }
        return null;
    }
    
    @Override
    public Class<?> getType(ELContext ctx, Object base, Object property)
    throws NullPointerException, PropertyNotFoundException, ELException {
        if (ctx == null) {
            throw new NullPointerException();
        }

        if (base instanceof Form) {
            ctx.setPropertyResolved(true);
        	final Path path = getPathList(ctx); //get path, see #PathResolver
        	final String fieldName = path.getAccessFieldName();
            final Object result = ((Form) base).getField(fieldName);
            if (result != null) {
            	return result.getClass();
            }
        }
        
        return null;
    }

    @Override
    public void setValue(ELContext ctx, Object base, Object property, Object value) 
    throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
        if (ctx == null) {
            throw new NullPointerException();
        }

        if (base instanceof Form) {
        	final Path path = getPathList(ctx);//get path, see #PathResolver
        	final String fieldName = path.getAccessFieldName();
        	ctx.setPropertyResolved(true);
            ((Form) base).setField(fieldName, value);
        }
    }
    
	private static Path getPathList(ELContext ctx){
		return (Path)ctx.getContext(Path.class);//get path, see #PathResolver
	}
    
    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
    	return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base instanceof Form) {
            Iterator<?> itr = ((Form) base).getFieldNames().iterator();
            List<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>();
            Object key;
            FeatureDescriptor desc;
            while (itr.hasNext()) {
                key = itr.next();
                desc = new FeatureDescriptor();
                desc.setDisplayName(key.toString());
                desc.setExpert(false);
                desc.setHidden(false);
                desc.setName(key.toString());
                desc.setPreferred(true);
                desc.setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.FALSE);
                desc.setValue(TYPE, key.getClass());
                feats.add(desc);
            }
            return feats.iterator();
        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base != null) {
            return Object.class;
        }
        return null;
    }
}
