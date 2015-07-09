/** FormProxyHandler.java.

	Purpose:
		
	Description:
		
	History:
		11:39:08 AM Jan 9, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyFactoryX;
import javassist.util.proxy.ProxyObject;
import javassist.util.proxy.SerializedProxyX;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormStatus;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.UiException;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

		private transient FormProxyObject self;
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

		// Fix an issue for javassist - https://issues.jboss.org/browse/JASSIST-247
		private synchronized void writeObject(java.io.ObjectOutputStream s)
				throws java.io.IOException {
			s.defaultWriteObject();
			Class clazz = self.getClass();

			MethodHandler methodHandler = null;
			if (self instanceof ProxyObject)
				methodHandler = ((ProxyObject)self).getHandler();
			else if (self instanceof Proxy)
				methodHandler = ProxyFactory.getHandler((Proxy) self);

			s.writeObject(new SerializedProxyX(clazz, ProxyFactoryX
					.getFilterSignature(clazz), methodHandler));
		}

		//Fix an issue for javassist - https://issues.jboss.org/browse/JASSIST-247
		@SuppressWarnings("rawtypes")
		private void readObject(java.io.ObjectInputStream s)
				throws java.io.IOException, ClassNotFoundException {
			s.defaultReadObject();
			Object read = s.readObject();
			if (read instanceof FormProxyObject)
				self = (FormProxyObject) read;
			else if (self instanceof SerializedProxyX) {
				try {
					self = (FormProxyObject) ((SerializedProxyX) self).readResolve();
				} catch (Exception e) {
					throw UiException.Aide.wrap(e);
				}
			}
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
				
				//F80: formProxyObject support notifyChange with Form.isDirty
				Object o =  super.invoke(self, method, proceed, args);
				if (o instanceof FormProxyObject && mname.startsWith("get")) {
					FormProxyObject fpo = (FormProxyObject)o;
					fpo.addFormProxyObjectListener(new FormProxyObjectListener() {
						public void onDataChange(Object o) {
							_binding.getBinder().notifyChange(o, ".");
						}

						public void onDirtyChange() {
							_binding.getBinder().notifyChange(_binding.getFormBean().getFormStatus(), ".");
						}
					});
				}
				return o;
			}
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		}
	}
}
