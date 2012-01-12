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
 * Map base implementation of {@link ValidationMessages}
 * @author dennis
 * @since 6.0.0
 */
public class ValidationMessagesImpl implements ValidationMessages,Map<Component, Object>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Map<Component, Map<String, List<Message>>> _attrMessageMap; //<component, <attr,messages>>
	private final Map<Component, Map<String, List<Message>>> _keyMessageMap; //<component, <attr,messages>>
	private final Map<String, List<Message>> _globalKeyMessageMap; //<component, <attr,messages>>
	
	private final MultipleMessages _multiple;
	
	public ValidationMessagesImpl(){
		_attrMessageMap = new LinkedHashMap<Component,Map<String,List<Message>>>();
		_keyMessageMap = new LinkedHashMap<Component,Map<String,List<Message>>>();
		_globalKeyMessageMap = new LinkedHashMap<String,List<Message>>();
		_multiple = new MultipleMessages();
	}
	
	
	static class Message implements Serializable{
		private static final long serialVersionUID = 1L;
		final String attr;
		final String key;
		final String msg;
		public Message(String attr, String key, String msg) {
			super();
			this.attr = attr;
			this.key = key;
			this.msg = msg;
		}
	}
	
	private List<Message> getAttrMessages(Component comp,String attr,boolean create){
		Map<String,List<Message>> attrMap = getAttrMap(comp,create);
		if(attrMap==null){
			return null;
		}
		List<Message> msgs = attrMap.get(attr);
		if(msgs==null && create){
			msgs = new ArrayList<Message>(1);
			attrMap.put(attr, msgs);
		}
		return msgs;
	}
	
	private Map<String,List<Message>> getAttrMap(Component comp,boolean create){
		Map<String,List<Message>> msgs = _attrMessageMap.get(comp);
		if(msgs==null && create){
			msgs = new LinkedHashMap<String, List<Message>>();
			_attrMessageMap.put(comp, msgs);
		}
		return msgs;
	}
	
	
	private List<Message> getKeyMessages(Component comp,String key,boolean create){
		Map<String,List<Message>> keyMap = getKeyMap(comp,create);
		if(keyMap==null){
			return null;
		}
		List<Message> msgs = keyMap.get(key);
		if(msgs==null && create){
			msgs = new ArrayList<Message>(1);
			keyMap.put(key, msgs);
		}
		return msgs;
	}
	
	private Map<String,List<Message>> getKeyMap(Component comp,boolean create){
		Map<String,List<Message>> msgs = _keyMessageMap.get(comp);
		if(msgs==null && create){
			msgs = new LinkedHashMap<String, List<Message>>();
			_keyMessageMap.put(comp, msgs);
		}
		return msgs;
	}
	
	private List<Message> getGlobalKeyMessages(String key,boolean create){
		List<Message> msgs = _globalKeyMessageMap.get(key);
		if(msgs==null && create){
			msgs = new ArrayList<Message>(1);
			_globalKeyMessageMap.put(key, msgs);
		}
		return msgs;
	}
	
	@Override
	public void clearMessages(Component comp) {
		_attrMessageMap.remove(comp);
		
		//comp, <key,messages>
		Map<String,List<Message>> keyMap = getKeyMap(comp,false);
		if(keyMap!=null){
			for(Entry<String,List<Message>> entry:keyMap.entrySet()){
				List<Message> gmsgs = _globalKeyMessageMap.get(entry.getKey());
				if(gmsgs!=null && gmsgs.size()>0){
					gmsgs.removeAll(entry.getValue());
				}
				if(gmsgs.size()==0){
					_globalKeyMessageMap.remove(entry.getKey());
				}
			}
		}
		_keyMessageMap.remove(comp);
	}
	
	@Override
	public void clearMessages(Component comp,String attr) {
		Map<String,List<Message>> attrMap = getAttrMap(comp,false);
		List<Message> remove = null;
		if(attrMap != null){
			remove = attrMap.remove(attr);
		}
		if(remove!=null && remove.size()>0){
			for(Message m:remove){
				if(m.key==null) continue;
				List<Message> msgs = getKeyMessages(comp, m.key, false);
				if(msgs!=null){
					msgs.remove(m);
					if(msgs.size()==0){
						Map<String,List<Message>> keyMap = getKeyMap(comp,false);
						if(keyMap!=null){
							keyMap.remove(m.key);
						}
					}
				}
				msgs = _globalKeyMessageMap.get(m.key);
				if(msgs!=null){
					msgs.remove(m);
					if(msgs.size()==0){
						_globalKeyMessageMap.remove(m.key);
					}
				}
			}
		}
	}

	@Override
	public String[] getMessages(Component comp,String attr) {
		List<Message> msgs = getAttrMessages(comp, attr, false);
		if(msgs==null||msgs.size()==0) 
			return null;
		String[] smsgs = new String[msgs.size()];
		for(int i=0;i<smsgs.length;i++){
			smsgs[i] = msgs.get(i).msg;
		}
		return smsgs;
	}
	
	@Override
	public String[] getMessages(Component comp) {
		Map<String,List<Message>> m = getAttrMap(comp,false);
		if(m==null || m.size()==0){
			return null;
		}
		List<String> msgs = new ArrayList<String>();
		
		for(Entry<String, List<Message>> e:m.entrySet()){
			for(Message mm:e.getValue()){
				msgs.add(mm.msg);
			}
		}
		return msgs.size()==0?null:msgs.toArray(new String[msgs.size()]);
	}
	

	@Override
	public String[] getKeyMessages(Component comp, String key) {
		List<Message> msgs = getKeyMessages(comp, key, false);
		if(msgs==null||msgs.size()==0) 
			return null;
		String[] smsgs = new String[msgs.size()];
		for(int i=0;i<smsgs.length;i++){
			smsgs[i] = msgs.get(i).msg;
		}
		return smsgs;
	}

	@Override
	public String[] getKeyMessages(String key) {
		List<Message> msgs = _globalKeyMessageMap.get(key);
		if(msgs==null||msgs.size()==0) 
			return null;
		String[] smsgs = new String[msgs.size()];
		for(int i=0;i<smsgs.length;i++){
			smsgs[i] = msgs.get(i).msg;
		}
		return smsgs;
	}

	@Override
	public void setMessages(Component comp, String attr, String key,String[] messages) {
		clearMessages(comp, attr);
		addMessages(comp,attr,key,messages);
	}
	
	@Override
	public void addMessages(Component comp, String attr, String key, String[] messages) {
		List<Message> attrMsgs = getAttrMessages(comp, attr, true);
		List<Message> keyMsgs = null;
		List<Message> globalMsgs = null;
		for(String s:messages){
			Message msg = new Message(attr,key,s);
			attrMsgs.add(msg);
			if(key!=null){
				if(keyMsgs==null){
					keyMsgs = getKeyMessages(comp, key, true);
					globalMsgs = getGlobalKeyMessages(key,true);
				}
				keyMsgs.add(msg);
				globalMsgs.add(msg);
			}
		}
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
		}else if(key instanceof String){
			String[] msgs = getKeyMessages((String)key);
			if(msgs!=null && msgs.length>0){
				return msgs[0];
			}
		}
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

	@SuppressWarnings("unchecked")
	@Override
	public Set<Component> keySet() {
		return Collections.EMPTY_SET;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Object> values() {
		return Collections.EMPTY_SET;
	}

	@SuppressWarnings("unchecked")
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
			}else if(key instanceof String){
				String[] msgs = getKeyMessages((String)key);
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
		public void putAll(Map<? extends Object, ? extends Object> m) {
		}
		@Override
		public void clear() {
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Set<Object> keySet() {
			return Collections.EMPTY_SET;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Collection<Object> values() {
			return Collections.EMPTY_SET;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Set<java.util.Map.Entry<Object, Object>> entrySet() {
			return Collections.EMPTY_SET;
		}
		
	}

}
