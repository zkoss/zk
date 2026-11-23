/** SetProxy.java.

	Purpose:
		
	Description:
		
	History:
		2:13:30 PM Dec 29, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class provides a proxy implementation of the <tt>Set</tt> interface. 
 * @author jumperchen
 * @since 8.0.0
 */
public class SetProxy<E> extends AbstractCollectionProxy<E> implements Set<E> {
	private static final long serialVersionUID = 20141229141520L;

	public SetProxy(Collection<E> origin, Annotation[] callerAnnots) {
		super(origin, callerAnnots);
		resetFromOrigin();
	}

	@SuppressWarnings("unchecked")
	protected Collection<E> initCache() {
		return new LinkedHashSet<E>(((Collection<E>) getOriginObject()).size());
	}
}
