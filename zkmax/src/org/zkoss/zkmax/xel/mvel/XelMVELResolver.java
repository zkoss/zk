/* XelMVELResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Sep  2 23:23:12	 2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.mvel;

import java.util.Collections;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import org.mvel.CompileException;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.impl.BaseVariableResolverFactory;
import org.mvel.DataConversion;

/**
 * The MVEL variable resolver that is implemnted on top of XEL
 * variable resolver.
 *
 * @author tomyeh
 * @since 3.0.0
 */
/*package*/ class XelMVELResolver extends BaseVariableResolverFactory {
	private final org.zkoss.xel.VariableResolver _resolver;
	/** A map of (String,Object) that was added by createVariable.
	 * It is used because _resolver is readonly.
	 */
	private Map _values;

	/*package*/ XelMVELResolver(org.zkoss.xel.VariableResolver resolver) {
		_resolver = resolver;
	}

	//VariableResolverFactory//
	public VariableResolver getVariableResolver(String name) {
		if (_values != null && _values.containsKey(name)
		|| (_resolver != null && _resolver.resolveVariable(name) != null)) {
			return this.variableResolvers != null && this.variableResolvers.containsKey(name) ?
				(VariableResolver)this.variableResolvers.get(name):
				new VarResolver(name);
		}
		return this.nextFactory != null ?
			this.nextFactory.getVariableResolver(name): null;
	}
	public boolean isResolveable(String name) {
		return (this.variableResolvers != null && this.variableResolvers.containsKey(name))
			|| (_values != null && _values.containsKey(name))
			|| (_resolver != null && _resolver.resolveVariable(name) != null)
			|| (this.nextFactory != null && this.nextFactory.isResolveable(name));
	}
	public boolean isTarget(String name) {
		return this.variableResolvers.containsKey(name);
	}
	public Set getKnownVariables() {
		return this.nextFactory != null ?
			this.nextFactory.getKnownVariables(): Collections.EMPTY_SET;
			//No way to retrieve all variables from XEL variable resolver
	}

	public VariableResolver createVariable(String name, Object value) {
		VariableResolver vr = getVariableResolver(name);
		if (vr == null)
			vr = new VarResolver(name);

		vr.setValue(value);
		return vr;
	}
	public VariableResolver createVariable(String name, Object value, Class type) {
		VariableResolver vr = getVariableResolver(name);
		if (vr != null && vr.getType() != null)
			throw new CompileException("variable already defined within scope: " + vr.getType() + " " + name);

		addResolver(name, vr = new VarResolver(name, type));
		vr.setValue(value);
		return vr;
	}
	private void addResolver(String name, VariableResolver vr) {
		if (this.variableResolvers == null)
			this.variableResolvers = new HashMap();
		this.variableResolvers.put(name, vr);
	}

	//Supporting Class//
	private class VarResolver implements VariableResolver {
		private final String _name;
		private Class _type;

		private VarResolver(String name) {
			_name = name;
		}
		private VarResolver(String name, Class type) {
			_name = name;
			_type = type;
		}

		public String getName() {
			return _name;
		}
		public Class getType() {
			return _type;
		}
		public void setStaticType(Class type) {
			_type = type;
		}
		public int getFlags() {
			return 0;
		}
		public Object getValue() {
			if (_values != null) {
				final Object o = _values.get(_name);
				if (o != null || _values.containsKey(_name))
					return o;
			}
			return _resolver.resolveVariable(_name);
		}
		public void setValue(Object value) {
			if (_type != null && value != null && value.getClass() != _type) {
				if (!DataConversion.canConvert(_type, value.getClass()))
					throw new CompileException("cannot assign " + value.getClass().getName() + " to type: " + _type.getName());

				try {
					value = DataConversion.convert(value, _type);
				} catch (Exception e) {
					throw new CompileException("cannot convert value of " + value.getClass().getName() + " to: " + _type.getName());
				}
			}

			if (_values == null)
				_values = new HashMap();
			_values.put(_name, value);
		}
	}
}
