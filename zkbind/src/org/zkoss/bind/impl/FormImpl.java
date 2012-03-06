/* FormImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 12:28:17 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.Form;
import org.zkoss.bind.FormExt;
import org.zkoss.bind.FormStatus;
import org.zkoss.lang.Objects;

/**
 * Implementation of the {@link Form}.
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public class FormImpl implements Form,FormExt,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	
	private final Map<String, Object> _attrs; //custom attributes
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
		_attrs = new HashMap<String, Object>();
	}
	
	private class FormStatusImpl implements FormStatus,Serializable{
		private static final long serialVersionUID = 1L;
		@Override
		public boolean isDirty() {
			return FormImpl.this.isDirty();
		}
	}
	
	@Override
	public Object getAttribute(String name) {
		return _attrs.get(name);
	}

	@Override
	public void setAttribute(String name,Object value){
		if(value==null){
			_attrs.remove(name);
		}else{
			_attrs.put(name, value);
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
	
	@Override
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
	
	public void addLoadFieldName(String fieldName) {
		_loadFieldNames.add(fieldName);
	}

	public void addSaveFieldName(String fieldName) {
		_saveFieldNames.add(fieldName);
	}
	
	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
//		.append(",id:").append(getId())
		.append(",fields:").append(getFieldNames()).toString();
	}

	@Override
	public FormStatus getStatus() {
		return _status;
	}
	
}
