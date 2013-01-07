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

import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
/**
 * Collection base implementation of {@link ValidationMessages}
 * @author dennis
 * @since 6.0.0
 */
public class ValidationMessagesImpl implements ValidationMessages,Collection<Object>, Serializable {
	//this class implement collection to support empty expression in EL
	private static final long serialVersionUID = 1L;
	
	
	private final List<Message> _messages;//all the messages
	
	//messages for special comp and key
	private final Map<Component, List<Message>> _compMsgsMap; //component, <messages>
	private final Map<String, List<Message>> _keyMsgsMap; //key,<messages>
	
	
	
	public ValidationMessagesImpl(){
		_messages = new LinkedList<Message>();
		_compMsgsMap = new LinkedHashMap<Component,List<Message>>();
		_keyMsgsMap = new LinkedHashMap<String,List<Message>>();
	}
	
	//a message that related to a attr and key
	static class Message implements Serializable{
		private static final long serialVersionUID = 1L;
		final Component comp;
		final String attr;
		final String key;
		final String msg;
		public Message(Component comp,String attr, String key, String msg) {
			this.comp = comp;
			this.attr = attr;
			this.key = key;
			this.msg = msg;
		}
	}
	
	@Override
	public void clearMessages(Component comp) {
		List<Message> remove = _compMsgsMap.get(comp);
		if(remove==null || remove.size()==0){
			return;
		}
		_messages.removeAll(remove);
		for(List<Message> keyMsgs:_keyMsgsMap.values()){
			keyMsgs.removeAll(remove);
		}
		_compMsgsMap.remove(comp);
	}
	
	@Override
	public void clearMessages(Component comp,String attr) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if(compMsgs==null || compMsgs.size()==0){
			return;
		}
		List<Message> remove = new ArrayList<Message>();
		for(Message msg:compMsgs){
			if(Objects.equals(msg.attr, attr)){
				remove.add(msg);
			}
		}
		if(remove.size()==0) return;
		
		_messages.removeAll(remove);
		for(List<Message> keyMsgs:_keyMsgsMap.values()){
			keyMsgs.removeAll(remove);
		}
		compMsgs.removeAll(remove);
	}
	
	
	@Override
	public void clearKeyMessages(Component comp,String key) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if(compMsgs==null || compMsgs.size()==0){
			return;
		}
		List<Message> remove = new ArrayList<Message>();
		for(Message msg:compMsgs){
			if(Objects.equals(msg.key, key)){
				remove.add(msg);
			}
		}
		if(remove.size()==0) return;
		
		_messages.removeAll(remove);
		for(List<Message> keyMsgs:_keyMsgsMap.values()){
			keyMsgs.removeAll(remove);
		}
		compMsgs.removeAll(remove);
	}
	
	@Override
	public void clearKeyMessages(String key) {
		List<Message> keyMsgs = _keyMsgsMap.get(key);
		if(keyMsgs==null || keyMsgs.size()==0){
			return;
		}
		List<Message> remove = new ArrayList<Message>();
		for(Message msg:keyMsgs){
			if(Objects.equals(msg.key, key)){
				remove.add(msg);
			}
		}
		if(remove.size()==0) return;
		
		_messages.removeAll(remove);
		for(List<Message> compMsgs:_compMsgsMap.values()){
			compMsgs.removeAll(remove);
		}
		keyMsgs.removeAll(remove);
	}
	
	public void clearAllMessages(){
		_messages.clear();
		_compMsgsMap.clear();
		_keyMsgsMap.clear();
	}

	@Override
	public String[] getMessages(Component comp,String attr) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if(compMsgs==null || compMsgs.size()==0){
			return null;
		}
		List<String> msgs = new ArrayList<String>();
		for(Message msg:compMsgs){
			if(Objects.equals(msg.attr, attr))
			msgs.add(msg.msg);
		}
		return msgs.size()==0?null:msgs.toArray(new String[msgs.size()]);
	}
	
	@Override
	public String[] getMessages(Component comp) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if(compMsgs==null || compMsgs.size()==0){
			return null;
		}
		List<String> msgs = new ArrayList<String>();
		for(Message msg:compMsgs){
			msgs.add(msg.msg);
		}
		return msgs.toArray(new String[msgs.size()]);
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
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if(compMsgs==null || compMsgs.size()==0){
			return null;
		}
		List<String> msgs = new ArrayList<String>();
		for(Message msg:compMsgs){
			if(Objects.equals(msg.key, key))
			msgs.add(msg.msg);
		}
		return msgs.size()==0?null:msgs.toArray(new String[msgs.size()]);
	}

	@Override
	public String[] getKeyMessages(String key) {
		List<Message> keyMsgs = _keyMsgsMap.get(key);
		if(keyMsgs==null || keyMsgs.size()==0){
			return null;
		}
		List<String> msgs = new ArrayList<String>();
		for(Message msg:keyMsgs){
			msgs.add(msg.msg);
		}
		return msgs.toArray(new String[msgs.size()]);
	}

	@Override
	public void setMessages(Component comp, String attr, String key,String[] messages) {
		clearMessages(comp, attr);
		addMessages(comp,attr,key,messages);
	}
	
	@Override
	public void addMessages(Component comp, String attr, String key, String[] messages) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if(compMsgs==null){
			_compMsgsMap.put(comp, compMsgs = new ArrayList<Message>());
		}
		
		for(String s:messages){
			Message msg = new Message(comp,attr,key,s);
			_messages.add(msg);
			compMsgs.add(msg);
			if(key!=null){
				List<Message> keyMsgs = _keyMsgsMap.get(key);
				if(keyMsgs==null){
					_keyMsgsMap.put(key, keyMsgs = new ArrayList<Message>());
				}
				keyMsgs.add(msg);
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
