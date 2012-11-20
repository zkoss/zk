/* DynamicPropertyELResolver.java

	Purpose:
		
	Description:
		
	History:
		2012/11/20 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.xel.zel;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import org.zkoss.zel.BeanELResolver;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;
import org.zkoss.zk.ui.ext.DynamicPropertied;

/**
 * A resolver to resolve value base on BeanELResolver and DynamicPropertied.
 * It only handle evaluation when a base object's type is DynamicPropertied
 * @author dennis
 * @since 6.5.1
 */
public class DynamicPropertiedELResolver extends BeanELResolver {

	
	public DynamicPropertiedELResolver(){
		super(false);
	}
	
	@Override
	public Object getValue(ELContext context, Object base, Object property) throws NullPointerException,
			PropertyNotFoundException, ELException {
		if (context == null) {
			throw new NullPointerException();
		}
		Object val = null;
		if(base instanceof DynamicPropertied){
			//don't get value before check it types(DynamicPropertied), to prevent call super 
			//BeanELResolver too early before other resolvers
			try{
				val = super.getValue(context, base, property);
			}catch(PropertyNotFoundException x){
				if(((DynamicPropertied)base).hasDynamicProperty(property.toString())){
					context.setPropertyResolved(true);
					val = ((DynamicPropertied)base).getDynamicProperty(property.toString());
				}else{
					context.setPropertyResolved(false);//super always set resolved to true, reset it
				}
			}
		}
		return val;
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) throws NullPointerException,
			PropertyNotFoundException, ELException {
		Class<?> type = null;
		if(base instanceof DynamicPropertied){
			//don't get value before check it types, , to prevent call super 
			//BeanELResolver too early before other resolvers
			try{
				type = super.getType(context, base, property);
			}catch(PropertyNotFoundException x){
				context.setPropertyResolved(true);
				type = Object.class;
			}
		}
		
		return type;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) throws NullPointerException,
			PropertyNotFoundException, PropertyNotWritableException, ELException {
		if(base instanceof DynamicPropertied){
			//don't get value before check it types, , to prevent call super 
			//BeanELResolver too early before other resolvers
			try{
				super.setValue(context, base, property,value);
			}catch(PropertyNotFoundException x){
				context.setPropertyResolved(true);
				((DynamicPropertied)base).setDynamicProperty(property.toString(), value);
			}
		}
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) throws NullPointerException,
			PropertyNotFoundException, ELException {
		if(base instanceof DynamicPropertied){
			return super.isReadOnly(context, base, property);
		}
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
