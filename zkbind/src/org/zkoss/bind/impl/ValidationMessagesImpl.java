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
public class ValidationMessagesImpl implements ValidationMessages, Collection<Object>, Serializable {
	//this class implement collection to support empty expression in EL
	private static final long serialVersionUID = 1L;

	// null objects
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private static final Component[] EMPTY_COMPONENT_ARRAY = new Component[0];

	private final List<Message> _messages; //all the messages

	//messages for special comp and key
	private final Map<Component, List<Message>> _compMsgsMap; //component, <messages>
	private final Map<String, List<Message>> _keyMsgsMap; //key,<messages>

	public ValidationMessagesImpl() {
		_messages = new LinkedList<Message>();
		_compMsgsMap = new LinkedHashMap<Component, List<Message>>();
		_keyMsgsMap = new LinkedHashMap<String, List<Message>>();
	}

	//a message that related to a attr and key
	static class Message implements Serializable {
		private static final long serialVersionUID = 1L;
		final Component comp;
		final String attr;
		final String key;
		final String msg;
		final Object value;

		public Message(Component comp, String attr, String key, String msg, Object value) {
			this.comp = comp;
			this.attr = attr;
			this.key = key;
			this.msg = msg;
			this.value = value;
		}
	}

	public void clearMessages(Component comp) {
		List<Message> remove = _compMsgsMap.get(comp);
		if (remove == null || remove.size() == 0) {
			return;
		}
		_messages.removeAll(remove);
		for (List<Message> keyMsgs : _keyMsgsMap.values()) {
			keyMsgs.removeAll(remove);
		}
		_compMsgsMap.remove(comp);
	}

	public void clearMessages(Component comp, String attr) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if (compMsgs == null || compMsgs.size() == 0) {
			return;
		}
		List<Message> remove = new ArrayList<Message>();
		for (Message msg : compMsgs) {
			if (Objects.equals(msg.attr, attr)) {
				remove.add(msg);
			}
		}
		if (remove.size() == 0)
			return;

		_messages.removeAll(remove);
		for (List<Message> keyMsgs : _keyMsgsMap.values()) {
			keyMsgs.removeAll(remove);
		}
		compMsgs.removeAll(remove);
	}

	public void clearKeyMessages(Component comp, String key) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if (compMsgs == null || compMsgs.size() == 0) {
			return;
		}
		List<Message> remove = new ArrayList<Message>();
		for (Message msg : compMsgs) {
			if (Objects.equals(msg.key, key)) {
				remove.add(msg);
			}
		}
		if (remove.size() == 0)
			return;

		_messages.removeAll(remove);
		for (List<Message> keyMsgs : _keyMsgsMap.values()) {
			keyMsgs.removeAll(remove);
		}
		compMsgs.removeAll(remove);
	}

	public void clearKeyMessages(String key) {
		List<Message> keyMsgs = _keyMsgsMap.get(key);
		if (keyMsgs == null || keyMsgs.size() == 0) {
			return;
		}
		List<Message> remove = new ArrayList<Message>();
		for (Message msg : keyMsgs) {
			if (Objects.equals(msg.key, key)) {
				remove.add(msg);
			}
		}
		if (remove.size() == 0)
			return;

		_messages.removeAll(remove);
		for (List<Message> compMsgs : _compMsgsMap.values()) {
			compMsgs.removeAll(remove);
		}
		keyMsgs.removeAll(remove);
	}

	public void clearAllMessages() {
		_messages.clear();
		_compMsgsMap.clear();
		_keyMsgsMap.clear();
	}

	public String[] getMessages(Component comp, String attr) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if (compMsgs == null || compMsgs.size() == 0) {
			return EMPTY_STRING_ARRAY;
		}
		List<String> msgs = new ArrayList<String>();
		for (Message msg : compMsgs) {
			if (Objects.equals(msg.attr, attr))
				msgs.add(msg.msg);
		}
		return msgs.toArray(EMPTY_STRING_ARRAY);
	}

	public String[] getMessages(Component comp) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if (compMsgs == null || compMsgs.size() == 0) {
			return EMPTY_STRING_ARRAY;
		}
		return getMessages0(compMsgs, compMsgs.size());
	}

	public String[] getMessages() {
		int len = _messages.size();
		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}
		return getMessages0(_messages, len);
	}

	private static String[] getMessages0(List<Message> msgs, int len) {
		String[] messages = new String[len];
		for (int i = 0; i < len; i++) {
			messages[i] = msgs.get(i).msg;
		}
		return messages;
	}

	public String[] getKeyMessages(Component comp, String key) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if (compMsgs == null || compMsgs.size() == 0) {
			return EMPTY_STRING_ARRAY;
		}
		List<String> msgs = new ArrayList<String>();
		for (Message msg : compMsgs) {
			if (Objects.equals(msg.key, key))
				msgs.add(msg.msg);
		}
		return msgs.toArray(EMPTY_STRING_ARRAY);
	}

	public String[] getKeyMessages(String key) {
		List<Message> keyMsgs = _keyMsgsMap.get(key);
		if (keyMsgs == null || keyMsgs.size() == 0) {
			return EMPTY_STRING_ARRAY;
		}
		List<String> msgs = new ArrayList<String>();
		for (Message msg : keyMsgs) {
			msgs.add(msg.msg);
		}
		return msgs.toArray(EMPTY_STRING_ARRAY);
	}

	public void setMessages(Component comp, String attr, String key, String[] messages) {
		setMessages(comp, attr, key, messages, null);
	}
	
	public void setMessages(Component comp, String attr, String key, String[] messages, Object value) {
		clearMessages(comp, attr);
		addMessages(comp, attr, key, messages, value);
	}

	public void addMessages(Component comp, String attr, String key, String[] messages) {
		addMessages(comp, attr, key, messages, null);
	}

	public void addMessages(Component comp, String attr, String key, String[] messages, Object value) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if (compMsgs == null) {
			_compMsgsMap.put(comp, compMsgs = new ArrayList<Message>());
		}

		for (String s : messages) {
			Message msg = new Message(comp, attr, key, s, value);
			_messages.add(msg);
			compMsgs.add(msg);
			if (key != null) {
				List<Message> keyMsgs = _keyMsgsMap.get(key);
				if (keyMsgs == null) {
					_keyMsgsMap.put(key, keyMsgs = new ArrayList<Message>());
				}
				keyMsgs.add(msg);
			}
		}
	}

	private List<Message> getMessage(Component comp, String key) {
		List<Message> compMsgs = _compMsgsMap.get(comp);
		if (compMsgs == null || compMsgs.size() == 0) {
			return Collections.emptyList();
		}
		List<Message> msgs = new ArrayList<Message>();
		for (Message msg : compMsgs) {
			if (Objects.equals(msg.key, key))
				msgs.add(msg);
		}
		return msgs;
	}

	public Object getFieldValue(String key) {
		List<Message> keyMsgs = _keyMsgsMap.get(key);
		if (keyMsgs == null || keyMsgs.size() == 0) {
			return null;
		}
		return keyMsgs.get(0).value;
	}

	public Object getFieldValue(Component comp, String key) {
		List<Message> messages = getMessage(comp, key);
		return messages.isEmpty() ? null : messages.get(0).value;
	}

	public Object[] getFieldValues(String key) {
		List<Message> keyMsgs = _keyMsgsMap.get(key);
		if (keyMsgs == null || keyMsgs.size() == 0) {
			return EMPTY_OBJECT_ARRAY;
		}
		List<Object> msgs = new ArrayList<Object>();
		for (Message msg : keyMsgs)
			msgs.add(msg.value);
		return msgs.toArray(EMPTY_OBJECT_ARRAY);
	}

	public Object[] getFieldValues(Component comp, String key) {
		List<Message> messages = getMessage(comp, key);
		List<Object> msgs = new ArrayList<Object>();
		for (Message msg : messages)
			msgs.add(msg.value);
		return msgs.toArray(EMPTY_OBJECT_ARRAY);
	}

	public Component getAssociate(String key) {
		List<Message> keyMsgs = _keyMsgsMap.get(key);
		if (keyMsgs == null || keyMsgs.size() == 0) {
			return null;
		}
		return keyMsgs.get(0).comp;
	}

	public Component[] getAssociates(String key) {
		List<Message> keyMsgs = _keyMsgsMap.get(key);
		if (keyMsgs == null || keyMsgs.size() == 0) {
			return EMPTY_COMPONENT_ARRAY;
		}
		List<Component> msgs = new ArrayList<Component>();
		for (Message msg : keyMsgs)
			msgs.add(msg.comp);
		return msgs.toArray(EMPTY_COMPONENT_ARRAY);
	}

	//interface for collection

	public int size() {
		return _messages.size();
	}

	public boolean isEmpty() {
		return _messages.isEmpty();
	}

	public boolean contains(Object o) {
		return _messages.contains(o);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Iterator<Object> iterator() {
		return new ArrayList(_messages).iterator();
	}

	public Object[] toArray() {
		return _messages.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return _messages.toArray(a);
	}

	public boolean add(Object e) {
		throw new UnsupportedOperationException("read only");
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException("read only");
	}

	public boolean containsAll(Collection<?> c) {
		return _messages.contains(c);
	}

	public boolean addAll(Collection<? extends Object> c) {
		throw new UnsupportedOperationException("read only");
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("read only");
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("read only");
	}

	public void clear() {
		throw new UnsupportedOperationException("read only");
	}

}
