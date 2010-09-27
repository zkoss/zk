/* DelegatingVariableResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 2 12:12:33 2007, Created by Dennis Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.seasar;

import org.seasar.framework.container.ComponentNotFoundRuntimeException;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolver;

/**
 * DelegatingVariableResolver, a seasar2 bean variable resolver.
 *
 * <p>It defines a variable called <code>_container</code> to represent
 * the instance of <code>org.seasar.framework.container.S2Container</code>.
 * The _container is get from <code>SingletonS2ContainerFactory.getContainer()</code>.
 *
 * <p>Usage:<br/>
 * 
 * in your zul file:<br/>
 * <code>&lt;?variable-resolver class="org.zkoss.zkplus.seasar.DelegatingVariableResolver"?&gt;</code>
 * <p>Applicable to Sesar Framework version 2.4 or later</p>
 * @author Dennis.Chen
 * @since 3.0.0
 */
public class DelegatingVariableResolver implements VariableResolver {
	protected S2Container _container;
	/**
	 * Get the seasar component by the specified name.
	 */		
	public Object resolveVariable(String name) {
		if(_container==null){
			_container = SingletonS2ContainerFactory.getContainer();
		}
		if(_container!=null){
			try{
				return _container.getComponent(name);
			}catch(ComponentNotFoundRuntimeException ex){
				//do nothing.
			}
		}
		return null;
	}
	
	public int hashCode() {
		return Objects.hashCode(getClass());
	}
	
	public boolean equals(Object obj) {
		return this == obj || (obj instanceof DelegatingVariableResolver && getClass() == obj.getClass());
	}
}
