/* FormImpl.java

		Purpose:
		
		Description:
		
		History:
				Wed Feb 19 15:45:29 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.xel.zel;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.bind.FormLegacy;
import org.zkoss.bind.impl.Path;
import org.zkoss.zel.ELContext;
import org.zkoss.zel.ELException;
import org.zkoss.zel.ELResolver;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zel.PropertyNotWritableException;

/**
 * ELResolver for {@link FormLegacy}.
 * @author Leon
 * @since 9.5.0
 */
public class FormELResolver extends ELResolver {
	
	public Object getValue(ELContext ctx, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		if (ctx == null) {
			throw new NullPointerException();
		}
		if (base instanceof FormLegacy) {
			//don't care the property, at there, get the path (the key of field in form), the path was built by PathResolver)
			final int nums = ((Integer) ctx.getContext(Integer.class)).intValue(); //get numOfKids, see #PathResolver
			final Path path = getPathList(ctx); //get path, see #PathResolver
			
			ctx.setPropertyResolved(true);
			if (nums == 0) { //last property
				final String fieldName = path.getAccessFieldName();
				return ((FormLegacy) base).getField(fieldName);
			} else {
				return base; //allow FORM resolving to continue!
			}
		}
		return null;
	}
	
	
	public Class<?> getType(ELContext ctx, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		if (ctx == null) {
			throw new NullPointerException();
		}
		
		if (base instanceof FormLegacy) {
			ctx.setPropertyResolved(true);
			final Path path = getPathList(ctx); //get path, see #PathResolver
			final String fieldName = path.getAccessFieldName();
			final Object result = ((FormLegacy) base).getField(fieldName);
			if (result != null) {
				return result.getClass();
			}
		}
		
		return null;
	}
	
	
	public void setValue(ELContext ctx, Object base, Object property, Object value)
			throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
		if (ctx == null) {
			throw new NullPointerException();
		}
		
		if (base instanceof FormLegacy) {
			final Path path = getPathList(ctx); //get path, see #PathResolver
			final String fieldName = path.getAccessFieldName();
			ctx.setPropertyResolved(true);
			((FormLegacy) base).setField(fieldName, value);
		}
	}
	
	private static Path getPathList(ELContext ctx) {
		return (Path) ctx.getContext(Path.class); //get path, see #PathResolver
	}
	
	
	public boolean isReadOnly(ELContext context, Object base, Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		return false;
	}
	
	
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		if (base instanceof FormLegacy) {
			Iterator<?> itr = ((FormLegacy) base).getFieldNames().iterator();
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