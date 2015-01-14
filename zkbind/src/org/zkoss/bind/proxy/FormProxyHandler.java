/** FormProxyHandler.java.

	Purpose:
		
	Description:
		
	History:
		11:39:08 AM Jan 9, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormStatus;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.UiException;

import javassist.util.proxy.MethodFilter;

/**
 * A form proxy handler
 * @author jumperchen
 */
public class FormProxyHandler<T> extends BeanProxyHandler<T> {
	private static final long serialVersionUID = 20150109113926L;
	protected static MethodFilter FORM_METHOD_FILTER = new MethodFilter() {
		public boolean isHandled(Method m) {
			if (m.getName().startsWith("set") || m.getName().startsWith("get") ||
					m.getName().startsWith("is") || m.getName().equals("hashCode"))
				return true;
			try {
				FormProxyObject.class.getMethod(m.getName(), m.getParameterTypes());
				return true;
			} catch (NoSuchMethodException e) {
				try {
					Form.class.getMethod(m.getName(), m.getParameterTypes());
					return true;
				} catch (NoSuchMethodException ex) {
					return false;
				}
			}
		}

	};
	private static final Map<String, Object> _defaultValues = new HashMap<String, Object>(10);
	static {
		_defaultValues.put("getResetEmptyStringValue", Strings.EMPTY);
		_defaultValues.put("getResetNullValue", null);
		_defaultValues.put("getResetByteValue", (byte)0);
		_defaultValues.put("getResetShortValue", (short)0);
		_defaultValues.put("getResetIntValue", (int)0);
		_defaultValues.put("getResetLongValue", 0L);
		_defaultValues.put("getResetFloatValue", 0.0f);
		_defaultValues.put("getResetDoubleValue", 0.0d);
		_defaultValues.put("getResetBooleanValue", false);
		_defaultValues.put("getResetCharValue", '\u0000');
	}
	private FormBinding _binding;
	
	FormStatusImpl _status;
	public FormProxyHandler(T origin) {
		super(origin);
		_status = new FormStatusImpl();
	}
	private class FormStatusImpl implements FormStatus, Serializable {
		private static final long serialVersionUID = 1L;

		private FormProxyObject self;
		public FormStatusImpl() {
		}
		public void setOwner(FormProxyObject self) {
			this.self = self;
		}
		public boolean isDirty() {
			return self.isFormDirty();
		}
		public void reset() {
			self.resetFromOrigin();
		}
		public void submit(BindContext ctx) {
			self.submitToOrigin(ctx);
			
		}
		public Object getOrigin() {
			return self;
		}
	}

	public Object invoke(Object self, Method method, Method proceed,
			Object[] args) throws Exception {
		try {
			final String mname = method.getName();
			Class<?> declaringClass = method.getDeclaringClass();
			
			if (declaringClass.equals(FormFieldCleaner.class)) {
				return _defaultValues.get(mname);
			} else {
				if ("setFormOwner".equals(mname)) {
					if (_binding != null) {
						BinderCtrl binder = (BinderCtrl) _binding.getBinder();
						Set<String> saveFormFieldNames = binder.removeSaveFormFieldNames((Form)self);
	
						_origin = (T) args[0];
						_binding = (FormBinding) args[1];
						
						if (!saveFormFieldNames.isEmpty())
							binder.addSaveFormFieldName((Form)self, saveFormFieldNames);
					} else {
						_origin = (T) args[0];
						_binding = (FormBinding) args[1];
					}
					return null;
				} else if ("getFormStatus".equals(mname)) {
					_status.setOwner((FormProxyObject)self);
					return _status;
				}
				
				return super.invoke(self, method, proceed, args);
			}
		} catch (Exception e) {
			throw new UiException(e);
		}
	}
}
