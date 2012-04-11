/* ImplicitObjectELResolver.java

	Purpose:
		
	Description:
		
	History:
		Mary 29, 2012 3:18:34 PM, Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;
import org.zkoss.zel.impl.lang.EvaluationContext;

/**
 * ELResolver for implicit object that is not supported in zk el
 * @author dennis
 * @since 6.0.1
 */
public class ImplicitObjectELResolver extends ELResolver {
	
	public static final String IMPLICIT_OBJECTS = "$IMPLICIT_OBJECTS$"; //the implicit objects
	
    @SuppressWarnings("unchecked")
	@Override
    public Object getValue(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if (context == null) {
            throw new NullPointerException();
        }
        BindContext ctx;
        if(base==null && (ctx = getBindContext(context)) != null){
        	Map<String,Object> implicit = (Map<String,Object>)ctx.getAttribute(IMPLICIT_OBJECTS);
        	if(implicit!=null && implicit.containsKey(property)){
        		//have to check contains to know it is resolved or not.
        		context.setPropertyResolved(true);
        		return implicit.get(property);
        	}
        }
        return null;
    }
    
    

    private BindELContext getBindELContext(ELContext context) {
    	if(context instanceof EvaluationContext){
    		ELContext ctx = ((EvaluationContext)context).getELContext();
    		return ctx instanceof BindELContext?(BindELContext)ctx:null;
    	}
		return null;
	}

    private BindContext getBindContext(ELContext context) {
    	BindELContext ctx = getBindELContext(context);
		return ctx==null?null:ctx.getBindContext();
	}

	@Override
    public Class<?> getType(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
    	//get type is called by setValue,see AstValue#setValue, 
    	//since this is ready only resolver, we don't need to implement it.
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property,
            Object value) throws NullPointerException,
            PropertyNotFoundException, PropertyNotWritableException,
            ELException {
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
    	return true;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }
}
