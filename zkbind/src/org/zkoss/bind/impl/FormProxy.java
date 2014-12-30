/** FormProxy.java.

	Purpose:
		
	Description:
		
	History:
		10:30:20 AM Dec 24, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javassist.util.proxy.ProxyObject;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormCtrl;
import org.zkoss.bind.FormStatus;
import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.lang.Objects;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.xel.ExpressionX;

/**
 * A form-like proxy object, this will proxy deeply for its descendants.
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public class FormProxy<T> implements Form, FormCtrl, FormProxyObject,
		Serializable {

	private static final long serialVersionUID = 20141224103722L;

	private final Set<String> _saveFieldNames; // field name for saving
	private final Map<String, Object> _fields; // field series -> value
	private final Set<String> _dirtyFieldNames; // field name that is dirty
	private static final int INIT_CAPACITY = 32;
	private T _owner;
	private Binder _binder;

	private final FormStatus _status;
	private FormBinding _binding;

	public FormProxy(Binder binder) {
		_fields = new LinkedHashMap<String, Object>(INIT_CAPACITY);
		_saveFieldNames = new LinkedHashSet<String>(INIT_CAPACITY);
		_dirtyFieldNames = new HashSet<String>(INIT_CAPACITY);
		_status = new FormStatusImpl();
		_binder = binder;
	}

	@SuppressWarnings("unchecked")
	public void setOwner(Object owner, FormBinding binding) {
		_owner = (T) owner;
		_binding = binding;
		resetCache();
	}

	private void resetCache() {
		_fields.clear();
	}

	public T getOwner() {
		return _owner;
	}

	private class FormStatusImpl implements FormStatus, Serializable {
		private static final long serialVersionUID = 1L;

		public boolean isDirty() {
			return FormProxy.this.isDirtyForm();
		}
	}

	public void setField(String field, Object value) {
		_fields.put(field, value);
		_dirtyFieldNames.add(field);
	}

	public void resetDirty() {
		_fields.clear();
		_dirtyFieldNames.clear();
	}

	public Object getField(String field) {
		Object result = _fields.get(field);
		if (result == null && !_fields.containsKey(field) && _owner != null) {
			final BindEvaluatorX eval = _binder.getEvaluatorX();
			ExpressionX expr = _binding.getFieldExpression(eval, field);
			final BindContext ctx = BindContextUtil.newBindContext(_binder,
					_binding, false, null, _binding.getComponent(), null);
			result = eval.getValue(ctx, _binding.getComponent(), expr);
		}
		;
		if (result != null) {
			if (!(result instanceof FormProxyObject)) {
				result = createProxy(result);
				_fields.put(field, result);
				return result;
			}
		}
		return result;
	}

	public Set<String> getSaveFieldNames() {
		return _saveFieldNames;
	}

	public Set<String> getFieldNames() {
		return _fields.keySet();
	}

	public boolean isDirtyForm() {
		if (!_dirtyFieldNames.isEmpty()) {
			return true;
		}
		for (Map.Entry<String, Object> me : _fields.entrySet()) {
			Object value = me.getValue();
			if (value instanceof FormProxyObject) {
				if (((FormProxyObject) value).isDirtyForm())
					return true;
			}
		}
		return false;
	}

	public void addSaveFieldName(String fieldName) {
		_saveFieldNames.add(fieldName);
	}

	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName())
				.append("@").append(Integer.toHexString(hashCode()))
				// .append(",id:").append(getId())
				.append(",fields:").append(getFieldNames()).toString();
	}

	public FormStatus getStatus() {
		return _status;
	}

	public static <T extends Object> T createProxy(T product) {
		return ProxyHelper.createProxyIfAny(product);
	}

	public Object getOriginObject() {
		return this;
	}

	public void resetFromOrigin() {
		_fields.clear(); // reset
		for (Iterator<String> it = new ArrayList<String>(_dirtyFieldNames)
				.iterator(); it.hasNext();) {
			String field = it.next();
			getField(field);
		}
		_dirtyFieldNames.clear();
	}

	public void submitToOrigin(BindContext ctx) {
		for (Iterator<String> it = _dirtyFieldNames.iterator(); it.hasNext();) {
			String field = it.next();
			if (_owner != null) {
				Object modified = _fields.get(field);
				if (_fields.containsKey(field)) {
					final BindEvaluatorX eval = _binder.getEvaluatorX();
					ExpressionX expr = _binding.getFieldExpression(eval, field);
					eval.setValue(ctx, _binding.getComponent(), expr, modified);
				}

			}
			it.remove();
		}
		for (Map.Entry<String, Object> me : _fields.entrySet()) {
			if (me.getValue() instanceof FormProxyObject) {
				((FormProxyObject) me.getValue()).submitToOrigin(ctx);
			}
		}
	}

	private boolean hasSaveField(String field) {
		for (String key : _saveFieldNames) {
			if (key.startsWith(field))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void replaceForm(FormCtrl newForm) {
		if (newForm != null) {
			for (String fn : getSaveFieldNames()) {
				newForm.addSaveFieldName(fn);
			}
			newForm.setOwner(_owner, _binding);
		}
		if (newForm instanceof FormProxy) {
			FormProxy<T> newProxy = ((FormProxy<T>) newForm);
			newProxy._binder = _binder;
		}
	}

	public boolean isDirty() {
		return isDirtyForm();
	}
}
