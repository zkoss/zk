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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.zk.ui.Component;
/**
 * Map base implementation of {@link ValidationMessages}
 * @author dennis
 * @since 6.0.0
 */
public class ValidationMessagesImpl implements ValidationMessages,Collection<Object>, Serializable {
	//this class implement collection to support empty expression in EL
	private static final long serialVersionUID = 1L;
	
	//
	private final Map<Component, Map<String, List<Message>>> _attrMessageMap; //<component, <attr,messages>>
	private final Map<Component, Map<String, List<Message>>> _keyMessageMap; //<component, <key,messages>>
	private final Map<String, List<Message>> _globalKeyMessageMap; //<component, <key,messages>>
	
	private final List<Message> _messages;//contains all the messages
	
	public ValidationMessagesImpl(){
		_messages = new LinkedList<Message>();
		_attrMessageMap = new LinkedHashMap<Component,Map<String,List<Message>>>();
		_keyMessageMap = new LinkedHashMap<Component,Map<String,List<Message>>>();
		_globalKeyMessageMap = new LinkedHashMap<String,List<Message>>();
	}
	
	//a message that related to a attr and key
	static class Message implements Serializable{
		private static final long serialVersionUID = 1L;
		final String attr;
		final String key;
		final String msg;
		public Message(String attr, String key, String msg) {
			this.attr = attr;
			this.key = key;
			this.msg = msg;
		}
	}
	
	//get messages of a attr of a special component
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
	
	//get attr messages of a special component
	private Map<String,List<Message>> getAttrMap(Component comp,boolean create){
		Map<String,List<Message>> msgs = _attrMessageMap.get(comp);
		if(msgs==null && create){
			msgs = new LinkedHashMap<String, List<Message>>();
			_attrMessageMap.put(comp, msgs);
		}
		return msgs;
	}
	
	//get messages of a key of a special component.
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
	
	//get key messages of a special component.
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
		Map<String,List<Message>> attrMap = getAttrMap(comp,false);
		if(attrMap != null){
			for(List<Message> remove:attrMap.values()){
				_messages.removeAll(remove);
			}
			_attrMessageMap.remove(comp);
		}

		//comp, <key,messages>
		Map<String,List<Message>> keyMap = getKeyMap(comp,false);
		if(keyMap!=null){
			for(Entry<String,List<Message>> entry:keyMap.entrySet()){
				List<Message> gmsgs = _globalKeyMessageMap.get(entry.getKey());
				if(gmsgs!=null && gmsgs.size()>0){
					gmsgs.removeAll(entry.getValue());
				}
				if(gmsgs!=null && gmsgs.size()==0){
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
			if(attrMap.size()==0){
				_attrMessageMap.remove(comp);
			}
		}
		if(remove!=null && remove.size()>0){
			_messages.removeAll(remove);
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
	public String[] getMessages() {
		if(_messages.size()==0){
			return null;
		}
		List<String> msgs = new ArrayList<String>(_messages.size());
		for(Message mm:_messages){
			msgs.add(mm.msg);
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
			_messages.add(msg);
			attrMsgs.add(msg);
			if(key!=null){
				if(keyMsgs==null){
					keyMsgs = getKeyMessages(comp, key, true);//key messages on a special component 
					globalMsgs = getGlobalKeyMessages(key,true);//
				}
				keyMsgs.add(msg);
				globalMsgs.add(msg);
			}
		}
	}
	
	//interface for collection

	@Override
	public int size() {
		return _messages.size();
	}

	@Override
	public boolean isEmpty() {
		return _messages.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return _messages.contains(o);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Iterator<Object> iterator() {
		return new ArrayList(_messages).iterator();
	}

	@Override
	public Object[] toArray() {
		return _messages.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return _messages.toArray(a);
	}

	@Override
	public boolean add(Object e) {
		throw new UnsupportedOperationException("read only");
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("read only");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return _messages.contains(c);
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		throw new UnsupportedOperationException("read only");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("read only");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("read only");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("read only");
	}

}
