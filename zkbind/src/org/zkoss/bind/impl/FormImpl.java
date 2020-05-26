/* FormImpl.java

		Purpose:
		
		Description:
		
		History:
				Wed Feb 19 15:45:29 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.FormLegacy;
import org.zkoss.bind.FormLegacyExt;
import org.zkoss.bind.FormStatus;
import org.zkoss.lang.Objects;

/**
 * For compatibility only.
 * @deprecated As of release 9.2.0, please use {@link org.zkoss.bind.Form}
 * @author Leon
 * @since 9.2.0
 */
@Deprecated
public class FormImpl implements FormLegacy, FormLegacyExt, Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	
	private final Set<String> _saveFieldNames; //field name for saving
	private final Set<String> _loadFieldNames; //field name for loading
	private final Map<String, Object> _fields; //field series -> value
	private final Map<String, Object> _initFields; //field series -> value
	private final Set<String> _dirtyFieldNames; //field name that is dirty
	private static final int INIT_CAPACITY = 32;
	
	private final FormStatus _status;
	
	public FormImpl() {
		_fields = new LinkedHashMap<String, Object>(INIT_CAPACITY);
		_initFields = new HashMap<String, Object>(INIT_CAPACITY);
		_saveFieldNames = new LinkedHashSet<String>(INIT_CAPACITY);
		_loadFieldNames = new LinkedHashSet<String>(INIT_CAPACITY);
		_dirtyFieldNames = new HashSet<String>(INIT_CAPACITY);
		_status = new FormStatusImpl();
	}
	
	@Override
	public FormStatus getFormStatus() {
		return getStatus();
	}
	
	private class FormStatusImpl implements FormStatus, Serializable {
		private static final long serialVersionUID = 1L;
		
		public boolean isDirty() {
			return FormImpl.this.isDirty();
		}
		
		@Override
		public void reset() {
			throw new UnsupportedOperationException("Not support!");
		}
		
		@Override
		public void submit(BindContext ctx) {
			throw new UnsupportedOperationException("Not support!");
		}
		
		@Override
		public Object getOrigin() {
			throw new UnsupportedOperationException("Not support!");
		}
	}
	
	public void setField(String field, Object value) {
		_fields.put(field, value);
		final Object init = _initFields.get(field);
		if (!Objects.equals(init, value)) { //different from original
			_dirtyFieldNames.add(field);
		} else {
			_dirtyFieldNames.remove(field);
		}
	}
	
	
	public void resetDirty() {
		_initFields.putAll(_fields);
		_dirtyFieldNames.clear();
	}
	
	public Object getField(String field) {
		return _fields.get(field);
	}
	
	public Set<String> getLoadFieldNames() {
		return _loadFieldNames;
	}
	
	public Set<String> getSaveFieldNames() {
		return _saveFieldNames;
	}
	
	public Set<String> getFieldNames() {
		return _fields.keySet();
	}
	
	public boolean isDirty() {
		return !_dirtyFieldNames.isEmpty();
	}
	
	@Override
	public FormStatus getStatus() {
		return _status;
	}
	
	public void addLoadFieldName(String fieldName) {
		_loadFieldNames.add(fieldName);
	}
	
	public void addSaveFieldName(String fieldName) {
		_saveFieldNames.add(fieldName);
	}
	
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
				.append(",fields:").append(getFieldNames()).toString();
	}
}