/* AbstractGroup.java


	Purpose:
	Description:
	History:
	2001/10/21 16:31:30, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.Collections;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.zkoss.util.CheckableTreeArray;
import org.zkoss.xml.FacadeNodeList;
import org.zkoss.idom.*;

/**
 * A semi-implemented item for group. A group is a item that has child items.
 *
 * <p>The default implementation of newChildren is for the sematic of
 * Element. A deriving class has to re-implement it, if it is not applicable.
 * Example, Document.
 *
 * @author tomyeh
 * @see Item
 */
public abstract class AbstractGroup extends AbstractItem implements Group {
	/** The list of the children. Never null.
	 */
	protected List _children;
	
	/** A helper map to enhance the searching speed with tag name.
	 * If any deriving class don't contain this helper map, they should
	 * apply the basic sequential search.
	 */
	private transient ElementMap _elemMap;

	/** Constructor.
	 */
	protected AbstractGroup() {
		_children = newChildren();
	}

	//-- deriving to override --//
	/** Creates a list to hold child vertices.
	 * Note: the list must be able to protect itself from adding
	 * unexpected child -- read-only, wrong type, undetached...
	 *
	 * <p>The default implementation obeys the sematic of Element,
	 * i.e., it doen't allow any child that cannot be a child of Element.
	 *
	 * <p>For performance issue, we introduced a map to improve the search 
	 * speed for Element node associated with a tag name.
	 */
	protected List newChildren() {
		return new ChildArray();
	}

	//-- Group --//
	public void clearModified(boolean includingDescendant) {
		if (includingDescendant) {
			for (final Iterator it = _children.iterator(); it.hasNext();)
				((Item)it.next()).clearModified(true);
		}
		super.clearModified(includingDescendant);
	}
	public Item clone(boolean preserveModified) {
		AbstractGroup group = (AbstractGroup)super.clone(preserveModified);

		group._children = group.newChildren();
		for (final Iterator it = _children.iterator(); it.hasNext();) {
			Item v = ((Item)it.next()).clone(preserveModified);
			boolean bClearModified = !preserveModified || !v.isModified();

			group._children.add(v); //v becomes modified (v.setParent is called)

			if (bClearModified)
				v.clearModified(false);
		}

		if (group._children instanceof ChildArray)
			((ChildArray)group._children).afterClone();

		group._modified = preserveModified && _modified;
		return group;
	}

	public final List getChildren() {
		return _children;
	}
	public final List detachChildren() {
		List list = new ArrayList(_children); //make a copy first

		for (Iterator it = _children.iterator(); it.hasNext();) {
			it.next();
			it.remove(); //and detach
		}

		return list;
	}
	public final boolean anyElement() {
		if (_elemMap != null)
			return _elemMap.any();

		for (Iterator it=_children.iterator();it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element)
				return true;
		}
		return false;
	}
	public final Set getElementNames() {
		if (_elemMap != null)
			return _elemMap.names();

		final Set set = new LinkedHashSet();
		for (final Iterator it = _children.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element)
				set.add(((Element)o).getName());
		}
		return set;
	}
	public final List getElements() {
		final List lst = new LinkedList();
		for (final Iterator it = _children.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element)
				lst.add(o);
		}
		return lst;
	}

	public final int getElementIndex
	(int indexFrom, String namespace, String name, int mode) {
		if (indexFrom < 0 || indexFrom >= _children.size())
			return -1;

		final Pattern ptn =
			(mode & FIND_BY_REGEX) != 0 ? Pattern.compile(name): null;

		final Iterator it = _children.listIterator(indexFrom);
		for (int j = indexFrom; it.hasNext(); ++j) {
			final Object o = it.next();
			if ((o instanceof Element)
			&& match((Element)o, namespace, name, ptn, mode))
				return j;
		}
		return -1;
	}
	public final int getElementIndex(int indexFrom, String tname) {
		return getElementIndex(indexFrom, null, tname, FIND_BY_TAGNAME);
	}

	public final Element getElement(String namespace, String name, int mode) {
		if (_elemMap != null && namespace == null && mode == FIND_BY_TAGNAME)
			return getElement(name); //use the speed version

		int j = getElementIndex(0, namespace, name, mode);
		if (j >= 0)
			return (Element)_children.get(j);

		if ((mode & FIND_RECURSIVE) != 0) {
			for (Iterator it = _children.iterator(); it.hasNext();) {
				Object o = it.next();
				if (o instanceof Group) {
					Element elem = ((Group)o).getElement(namespace, name, mode);
					if (elem != null)
						return elem;
				}
			}
		}
		return null;
	}
	public final Element getElement(String tname) {
		if (_elemMap != null)
			return _elemMap.get(tname);

		int j = getElementIndex(0, tname);
		return j >= 0 ? (Element)_children.get(j): null;
	}

	public final List getElements(String namespace, String name, int mode) {
		if (_elemMap != null && namespace == null && mode == FIND_BY_TAGNAME)
			return getElements(name); //use the speed version

		final Pattern ptn =
			(mode & FIND_BY_REGEX) != 0 ? Pattern.compile(name): null;

		final List list = new LinkedList();
		for (final Iterator it = _children.iterator(); it.hasNext();) {
			Object o = it.next();
			if ((o instanceof Element)
			&& match((Element)o, namespace, name, ptn, mode))
				list.add(o);
		}

		if ((mode & FIND_RECURSIVE) != 0) {
			for (final Iterator it = _children.iterator(); it.hasNext();) {
				Object o = it.next();
				if (o instanceof Group)
					list.addAll(((Group)o).getElements(namespace, name, mode));
			}
		}
		return list;
	}
	public final List getElements(String tname) {
		if (_elemMap != null)
			return _elemMap.getAll(tname);

		return getElements(null, tname, FIND_BY_TAGNAME);
	}

	public final String getElementValue
	(String namespace, String name, int mode, boolean trim) {
		Element child = getElement(namespace, name, mode);
		return child != null ? child.getText(trim): null;
	}
	public final String getElementValue(String tname, boolean trim) {
		final Element child = getElement(tname);
		return child != null ? child.getText(trim): null;
	}

	public final int coalesce(boolean recursive) {
		int count = 0;
		Item found = null;
		StringBuffer sb = new StringBuffer();
		for (final Iterator it = _children.iterator(); it.hasNext();) {
			Object o = it.next();
			Item newFound =
				(o instanceof Textual) && ((Textual)o).isCoalesceable() ?
				(Item)o: null;

			if (newFound != null && found != null
			&& found.getClass().equals(o.getClass())) {
				if (sb.length() == 0)
					sb.append(found.getText());
				sb.append(((Item)o).getText()); //coalesce text
				it.remove(); //remove this node
				++count; //# being coalesced and removed
			} else {
				if (sb.length() > 0) { //coalesced before?
					found.setText(sb.toString());
					sb.setLength(0);
				}
				found = newFound;
			}
		}
		if (sb.length() > 0)
			found.setText(sb.toString());
		sb = null; //no longer useful

		if (recursive) {
			for (final Iterator it = _children.iterator(); it.hasNext();) {
				final Object o = it.next();
				if (o instanceof Group)
					count += ((Group)o).coalesce(recursive);
			}
		}
		return count;
	}

	//-- Node --//
	public final NodeList getChildNodes() {
		return new FacadeNodeList(_children);
	}
	public final Node getFirstChild() {
		return _children.isEmpty() ? null: (Node)_children.get(0);
	}
	public final Node getLastChild() {
		int sz = _children.size();
		return sz == 0 ? null: (Node)_children.get(sz - 1);
	}
	public final boolean hasChildNodes() {
		return !_children.isEmpty();
	}

	//No need to call checkWritable here because _children is smart enough
	public final Node insertBefore(Node newChild, Node refChild) {
		if (refChild == null)
			return appendChild(newChild);

		int j = _children.indexOf(refChild);
		if (j < 0)
			throw new DOMException(DOMException.NOT_FOUND_ERR, getLocator());
		_children.add(j, newChild);
		return newChild;
	}
	public final Node replaceChild(Node newChild, Node oldChild) {
		int j = _children.indexOf(oldChild);
		if (j < 0)
			throw new DOMException(DOMException.NOT_FOUND_ERR, getLocator());
		return (Node)_children.set(j, newChild);
	}
	public final Node removeChild(Node oldChild) {
		int j = _children.indexOf(oldChild);
		if (j < 0)
			throw new DOMException(DOMException.NOT_FOUND_ERR, getLocator());
		return (Node)_children.remove(j);
	}
	public final Node appendChild(Node newChild) {
		_children.add(newChild);
		return newChild;
	}

	//-- Serializable --//
	//NOTE: they must be declared as private
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		if (_children instanceof ChildArray)
			((ChildArray)_children).afterUnmarshal();
	}

	//-- ElementMap
	/** Stores a 'cached' map of child elements to speed up the access.
	 */
	protected static class ElementMap {
		/** the map of (String elemName, List of Elements). */
		private final Map _map = new LinkedHashMap();
		
		protected ElementMap() {
		}
		
		/**
		 * Put an element into the map.
		 * If the "following" argument is assocaied the same name, we will
		 * add the element before the "following".
		 */
		public final void put(Element e, Element following) {
			final String name = e.getName();
			List valueList = (List)_map.get(name);
			if (valueList == null) {
				valueList = new LinkedList();
				_map.put(name, valueList);
			}

			if (following != null && name.equals(following.getName())) {
				//add into list before the following
				for (ListIterator it = valueList.listIterator(); it.hasNext();) {
					if (it.next() == following) { //no need to use equals
						it.previous();
						it.add(e);
						return;
					}
				}
			}
			
			valueList.add(e);	//add into list
		}
		/**
		 * Get the element with name. If you have many values associalted with
		 * the same key, it returned the head for you.
		 */
		public final Element get(String name) {
			final List vals = (List)_map.get(name);
			return vals != null && !vals.isEmpty() ? (Element)vals.get(0): null;
		}
		/**
		 * Get a readonly list of all elements with name.
		 */
		public final List getAll(String name) {
			final List vals = (List)_map.get(name);
			return vals != null ?
				Collections.unmodifiableList(vals): Collections.EMPTY_LIST;
		}
		/**
		 * Remove e from the map.
		 */
		public final void remove(Element e) {
			final List vals = (List)_map.get(e.getName());
			vals.remove(e);
			if (vals.isEmpty())
				_map.remove(e.getName());
		}
		/** Returns true if any element.
		 */
		public final boolean any() {
			return !_map.isEmpty();
		}
		/** Returns a readonly set of names of all elements.
		 */
		public final Set names() {
			return _map.keySet();
		}
		/** Returns the number of elements.
		 */
		public final int size() {
			int sz = 0;
			for (Iterator it = _map.values().iterator(); it.hasNext();) {
				sz += ((List)it.next()).size();
			}
			return sz;
		}
	}
	
	//-- ChildArray --//
	/** The array to hold children.
	 */
	protected class ChildArray extends CheckableTreeArray {
		protected ChildArray() {
			_elemMap = new ElementMap();
		}

		/** Called after unmarshalling back the AbstractGroup instance
		 * that owns this object.
		 */
		private void afterUnmarshal() {
			_elemMap = new ElementMap();

			for (Iterator it = this.iterator(); it.hasNext();) {
				final Object o = it.next();
				if (o instanceof Element)
					_elemMap.put((Element)o, null);
			}
		}
		/** Called after cloning the AbstractGroup instance that owns this object.
		 */
		private void afterClone() {
			afterUnmarshal();
		}

		//-- CheckableTreeArray --//
		protected void onAdd(Object newElement, Object followingElement) {
			checkAdd(newElement, followingElement, false);
		}
		protected void onSet(Object newElement, Object replaced) {
			assert(replaced != null);
			checkAdd(newElement, replaced, true);
		}
		private void checkAdd(Object newVal, Object other, boolean replace) {
			checkWritable();

			//allowed type?
			if (!(newVal instanceof Element) && !(newVal instanceof Text)
			&& !(newVal instanceof CData) && !(newVal instanceof Comment)
			&& !(newVal instanceof EntityReference) && !(newVal instanceof Binary)
			&& !(newVal instanceof ProcessingInstruction))
				throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Invalid type", getLocator());

			//to be safe, no auto-detach
			final Item newItem = (Item)newVal;
			if (newItem.getParent() != null) {
				throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
					"Item, "+newItem.toString()+", owned by "+newItem.getParent()+" "+newItem.getLocator()+"; detach or clone it", getLocator());
			}

			//test whether a graph will be created?
			if (newItem instanceof Group)
				for (Item p = AbstractGroup.this; p != null; p = p.getParent())
					if (p == newItem)
						throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Add to itself", getLocator());

			if (newItem instanceof Element) { //Element put into map, this must be done before replaced
				//try to find the first Element node on the array
				Element eOther;
				if ((other != null) && !(other instanceof Element)) {
					eOther = null;
					boolean bFirstElemFind = false;
					for (Iterator it = this.iterator(); it.hasNext();) {
						Object node = it.next();
						if (bFirstElemFind) {
							if (node instanceof Element) {
								eOther = (Element)node;
								break;
							}
						} else if (node == other) {
							bFirstElemFind = true;
						} 
					}
				} else {
					eOther = (Element)other;
				}
				_elemMap.put((Element)newItem, eOther);
			}
			
			if (replace)
				onRemove(other);
			newItem.setParent(AbstractGroup.this); //it will call this.setModified
		}
		protected void onRemove(Object item) {
			checkWritable();
			final Item removeItem = (Item)item;
			removeItem.setParent(null); //it will call this.setModified

			if (removeItem instanceof Element) //Element remove from map
				_elemMap.remove((Element)removeItem);
		}
	}
}
