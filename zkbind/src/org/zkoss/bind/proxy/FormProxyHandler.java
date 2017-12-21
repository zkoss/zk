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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.util.proxy.MethodFilter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormStatus;
import org.zkoss.bind.annotation.Transient;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.lang.Strings;
import org.zkoss.util.Pair;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Callback;

/**
 * A form proxy handler
 * @author jumperchen
 */
public class FormProxyHandler<T> extends BeanProxyHandler<T> {
	private static final long serialVersionUID = 20150109113926L;
	protected static MethodFilter FORM_METHOD_FILTER = new MethodFilter() {
		public boolean isHandled(Method m) {
			if (m.isAnnotationPresent(Transient.class))
				return false;
			final String name = m.getName();
			if (name.startsWith("set"))
				return isSetMethodHandled(m);
			if (name.startsWith("get") || name.startsWith("is") || name.equals("hashCode"))
				return true;
			try {
				FormProxyObject.class.getMethod(name, m.getParameterTypes());
				return true;
			} catch (NoSuchMethodException e) {
				try {
					Form.class.getMethod(name, m.getParameterTypes());
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
		_defaultValues.put("getResetByteValue", (byte) 0);
		_defaultValues.put("getResetShortValue", (short) 0);
		_defaultValues.put("getResetIntValue", (int) 0);
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
		_node = new ProxyNodeImpl("", null);
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

	public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Exception {
		try {
			final String mname = method.getName();
			Class<?> declaringClass = method.getDeclaringClass();

			if (declaringClass.equals(FormFieldCleaner.class)) {
				return _defaultValues.get(mname);
			} else {
				if ("setFormOwner".equals(mname)) {
					//F80: formProxyObject support notifyChange with Form.isDirty
					addCallbacks();
					if (_binding != null) {
						BinderCtrl binder = (BinderCtrl) _binding.getBinder();
						Set<String> saveFormFieldNames = binder.removeSaveFormFieldNames((Form) self);

						_origin = ProxyHelper.getOriginObject((T) args[0]);
						_binding = (FormBinding) args[1];

						if (!saveFormFieldNames.isEmpty())
							binder.addSaveFormFieldName((Form) self, saveFormFieldNames);

					} else {
						_origin = ProxyHelper.getOriginObject((T) args[0]);
						_binding = (FormBinding) args[1];
					}
					return null;
				} else if ("cacheSavePropertyBinding".equals(mname)) {
					//ZK-3185: Enable form validation with reference and collection binding
					ProxyHelper.cacheSavePropertyBinding(_node, (String) args[0], (SavePropertyBinding) args[1]);
					return null;
				} else if ("getFormStatus".equals(mname)) {
					_status.setOwner((FormProxyObject) self);
					return _status;
				} else if ("collectCachedSavePropertyBinding".equals(mname)) {
					//ZK-3185: Enable form validation with reference and collection binding
					Set<Pair<String, SavePropertyBinding>> sBindings = new HashSet<Pair<String, SavePropertyBinding>>(_node.getCachedSavePropertyBinding());
					_node.getCachedSavePropertyBinding().clear();
					return sBindings;
				}

				return super.invoke(self, method, proceed, args);
			}
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		}
	}

	private void addCallbacks() {
		_node.setOnDataChangeCallback(new Callback<Object[]>() {
			public void call(Object[] data) {
				_binding.getBinder().notifyChange(data[0], (String) data[1]);
			}
		});
		_node.setOnDirtyChangeCallback(new Callback() {
			public void call(Object data) {
				_binding.getBinder().notifyChange(_binding.getFormBean().getFormStatus(), ".");
			}
		});
	}

	//F80: formProxyObject support notifyChange with Form.isDirty
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		addCallbacks();
	}
}
