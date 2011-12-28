/* ValidationMessagesImpl.java

	Purpose:
		
	Description:
		
	History:
		2011/12/26 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.zk.ui.Component;
/**
 * 
 * @author dennis
 *
 */
public class ValidationMessagesImpl implements ValidationMessages,Map<Component, Object>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Map<Component, Map<String, List<String>>> _messages; //<component, <attr,messages>>
	
	private final MultipleMessages _multiple;
	
	public ValidationMessagesImpl(){
		_messages = new LinkedHashMap<Component,Map<String,List<String>>>();
		_multiple = new MultipleMessages();
	}
	
	private List<String> getAttrMessages(Component comp,String attr,boolean create){
		Map<String,List<String>> attrMsgs = getCompMessages(comp,create);
		if(attrMsgs==null){
			return null;
		}
		List<String> msgs = attrMsgs.get(attr);
		if(msgs==null && create){
			msgs = new ArrayList<String>(1);
			attrMsgs.put(attr, msgs);
		}
		return msgs;
	}
	
	private Map<String,List<String>> getCompMessages(Component comp,boolean create){
		Map<String,List<String>> compMsgs = _messages.get(comp);
		if(compMsgs==null && create){
			compMsgs = new LinkedHashMap<String, List<String>>();
			_messages.put(comp, compMsgs);
		}
		return compMsgs;
	}
	
	@Override
	public void clearMessages(Component comp) {
		_messages.remove(comp);
	}
	
	@Override
	public void clearMessages(Component comp,String attr) {
		Map<String,List<String>> msgs = getCompMessages(comp,false);
		if(msgs != null){
			msgs.remove(attr);
		}
//		System.out.println(">>>>clearMessage>"+comp+","+attr);
	}

	@Override
	public String[] getMessages(Component comp,String attr) {
		List<String> msgs = getAttrMessages(comp, attr, false);
//		System.out.println(">>>>getMessage>"+comp+","+attr+":"+msgs);
		return msgs==null||msgs.size()==0?null:msgs.toArray(new String[msgs.size()]);
	}
	
	@Override
	public String[] getMessages(Component comp) {
		Map<String,List<String>> m = getCompMessages(comp,false);
		if(m==null || m.size()==0){
			return null;
		}
//		System.out.println(">>>>getMessage>"+comp);
		List<String> msgs = new ArrayList<String>();
		
		for(Entry<String, List<String>> e:m.entrySet()){
			msgs.addAll(e.getValue());
		}
		
		return msgs.size()==0?null:msgs.toArray(new String[msgs.size()]);
	}

	@Override
	public void setMessages(Component comp, String attr, String[] messages) {
		List<String> msgs = getAttrMessages(comp, attr, true);
		msgs.clear();
		for(String s:messages){
			msgs.add(s);
		}
//		System.out.println(">>>>setMessage>"+comp+","+attr+","+msg);
	}
	
	@Override
	public void addMessages(Component comp, String attr, String[] messages) {
		List<String> msgs = getAttrMessages(comp, attr, true);
		for(String s:messages){
			msgs.add(s);
		}
//		System.out.println(">>>>addMessages>"+comp+","+attr+","+msg);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public Object get(Object key) {
		if(key instanceof Component){
			String[] msgs = getMessages((Component)key);
			if(msgs!=null && msgs.length>0){
				return msgs[0];
			}
		}
		if(key instanceof String){
			if("multiple".equals(key)){
				return getMultiple();
			}
		}
		//TODO for other type of message key
		return null;
	}

	@Override
	public String put(Component key, Object value) {
		return null;
	}

	@Override
	public String remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends Component, ? extends Object> m) {
	}

	@Override
	public void clear() {
	}

	@Override
	public Set<Component> keySet() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Collection<Object> values() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<java.util.Map.Entry<Component, Object>> entrySet() {
		return Collections.EMPTY_SET;
	}
	
	public Map<Object,Object> getMultiple(){
		return _multiple;
	}
	
	public class MultipleMessages implements Map<Object,Object>,Serializable{
		private static final long serialVersionUID = 7853710733151556817L;
		@Override
		public int size() {
			return 0;
		}
		@Override
		public boolean isEmpty() {
			return false;
		}
		@Override
		public boolean containsKey(Object key) {
			return false;
		}
		@Override
		public boolean containsValue(Object value) {
			return false;
		}
		@Override
		public Object get(Object key) {
			if(key instanceof Component){
				String[] msgs = getMessages((Component)key);
				return msgs;
			}
			return null;
		}
		@Override
		public Object put(Object key, Object value) {
			return null;
		}
		@Override
		public String[] remove(Object key) {
			return null;
		}
		@Override
		public void putAll(Map m) {
		}
		@Override
		public void clear() {
		}
		@Override
		public Set keySet() {
			return Collections.EMPTY_SET;
		}
		@Override
		public Collection values() {
			return Collections.EMPTY_SET;
		}
		@Override
		public Set entrySet() {
			return Collections.EMPTY_SET;
		}
		
	}

}
