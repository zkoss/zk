/* TreeModelELResolver.java

	Purpose:
		
	Description:
		
	History:
		Jan 12, 2012 3:18:34 PM, Created by henrichen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Array;
import java.util.Iterator;

import org.zkoss.lang.Classes;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;
import org.zkoss.zul.TreeModel;

/**
 * ELResolver for {@link TreeModel}.
 * @author dennis
 * @since 6.0.0
 */
public class TreeModelELResolver extends ELResolver {
	
	private static final Class<?> INTEGER_ARRAY = new int[0].getClass();
	
    @Override
    public Object getValue(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base instanceof TreeModel<?>) {
        	TreeModel<?> treemodel = (TreeModel<?>) base;
            int[] path = coerce(property);
            if (path==null) {
                return null;
            }
            context.setPropertyResolved(true);
            return treemodel.getChild(path);
        }

        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base instanceof TreeModel<?>) {
        	int[] path = coerce(property);
            if (path == null) return null;
            context.setPropertyResolved(true);
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
//        if (base instanceof TreeModel<?>) {
//            FeatureDescriptor[] descs = new FeatureDescriptor[1];
//            descs[0] = new FeatureDescriptor();
//            descs[0].setDisplayName("["+0+"]");
//            descs[0].setExpert(false);
//            descs[0].setHidden(false);
//            descs[0].setName("0");
//            descs[0].setPreferred(true);
//            descs[0].setValue(RESOLVABLE_AT_DESIGN_TIME, Boolean.FALSE);
//            descs[0].setValue(TYPE, _pathClass.getClass());
//            return Arrays.asList(descs).iterator();
//        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof TreeModel<?>) { // implies base != null
            return INTEGER_ARRAY.getClass();
        }
        return null;
    }

    private static final int[] coerce(Object property) {
    	
    	if (INTEGER_ARRAY.isInstance(property)) {//quick casting for int[]
            return (int[])property;
        }
    	
    	final Class<?> clz = property.getClass(); 
    	if(clz.isArray()){
    		int s  = Array.getLength(property);
    		int[] path = new int[s];
    		for(int i=0;i<s;i++){
    			try{
    				path[i] = ((Integer)Classes.coerce(Integer.class, Array.get(property, i))).intValue();
    			}catch(Exception x){
    				throw new PropertyNotFoundException(x.getMessage(),x);
    			}
    		}
    		return path;
    	}
        
        //just ignore other types (especially a string)
        return null;
    }
}
