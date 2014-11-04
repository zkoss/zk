/** HtmlShadowElement.java.

	Purpose:
		
	Description:
		
	History:
		12:47:42 PM Oct 22, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zk.ui;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Generics;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.ShadowElementCtrl;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ShadowElementsCtrl;

/**
 * A skeleton of shadow element that represents as a <i>shadow</i> tree.
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public abstract class HtmlShadowElement extends AbstractComponent implements
		ShadowElement, ShadowElementCtrl {
	private static final Logger log = LoggerFactory.getLogger(HtmlShadowElement.class);
	private static final long serialVersionUID = 20141022145906L;
	private transient Component _firstInsertion;
	private transient Component _lastInsertion;
	private transient Component _nextInsertion;
	private transient Component _previousInsertion;
	private transient Map<String, Object> _props;

	
	protected Component _host;
	public HtmlShadowElement() {
		init();
	}
	private void init() {
		_props = new LinkedHashMap<String, Object>();
	}
	public Component getNextInsertion() {
		if (_nextInsertion == null) {
			Component result = _lastInsertion == null ? null : _lastInsertion.getNextSibling();
			if (result == null && getParent() != null) // ask for its parent
				return ((HtmlShadowElement)getParent()).getNextInsertion();
			return null;
		}
		return _nextInsertion;
	}
	public Component getPreviousInsertion() {
		if (_previousInsertion == null) {
				Component result = _firstInsertion == null ? null : _firstInsertion.getPreviousSibling();
			if (result == null && getParent() != null) // ask for its parent
				return ((HtmlShadowElement)getParent()).getPreviousInsertion();
		}
		return _previousInsertion;
	}
	public Component getFirstInsertion() {
		return _firstInsertion;
	}

	public Component getLastInsertion() {
		return _lastInsertion;
	}

	public void setShadowHost(Component host, Component insertBefore) {
		if (getParent() != null) {
			throw new UiException("As a shadow child cannot be a shadow root. [" + this + "]");
		}
		if (host == null) {
			throw new UiException("The shadow host cannot be null. [" + this + "], please use detach() method instead!.");
		}
		if (_host != null) {
			throw new UiException("The shadow element cannot change its host, if existed. [" + this + "]");
		}
		_host = host;
		                               
		if (insertBefore != null) { // TODO
			throw new IllegalAccessError("Not implemented yet");
		}
		_nextInsertion = insertBefore;
		if (insertBefore != null) {
			_previousInsertion = insertBefore.getPreviousSibling();
		} else {
			List<ShadowElement> shadowRoots = ((ComponentCtrl) host).getShadowRoots();
			ShadowElement lastShadowElement = shadowRoots.isEmpty() ? null : shadowRoots.get(shadowRoots.size() - 1);
			Component prev = (Component)lastShadowElement;
			HtmlShadowElement prevOwner = (HtmlShadowElement) lastShadowElement;
			Component lastChild = host.getLastChild();
			 while (prev != null) {
				if (prev instanceof HtmlShadowElement) {
					prevOwner  = (HtmlShadowElement) prev;
					prev = prevOwner.getPreviousInsertion();
				} else {
					break;
				}
			}
			
			if (prev == null) { // only shadow elements in front of it
				if (lastChild == null) // no any Component available 
					_previousInsertion = (Component) lastShadowElement;
				else
					_previousInsertion = lastChild;
			} else {
				if (prev == lastChild) { // the lastShadowElement is the last one
					_previousInsertion = (Component) lastShadowElement;
				} else {
					_previousInsertion = lastChild;
				}
			}
		}
			
		((ComponentCtrl) host).addShadowRoot(this);
	}
	
	public void setParent(Component parent) {
		if (_host != null && parent != null) {
			throw new UiException("As a shadow root cannot be a child of a shadow element.");
		}
		super.setParent(parent);
	}
	
	public void beforeParentChanged(Component parent) {
		if (parent != null) {
			if (!(parent instanceof ShadowElement))
				throw new UiException("Unsupported parent for shadow element: "
						+ parent);
			if (_host != null) {
				throw new UiException("Unsupported parent for shadow root: "
						+ this);
			} else { // detach
				_previousInsertion = null;
				_firstInsertion = null;
				_lastInsertion = null;
				_nextInsertion = null;
			}
		}
		super.beforeParentChanged(parent);
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof ShadowElement))
			throw new UiException("Unsupported child for shadow element: "
					+ child);
		HtmlShadowElement lastChild = (HtmlShadowElement) getLastChild();
		if (lastChild != null) {
			if (refChild == null) {
				if (lastChild._nextInsertion != null) {
					((HtmlShadowElement)child)._previousInsertion = lastChild._nextInsertion;
					if (((HtmlShadowElement)child)._nextInsertion == lastChild._nextInsertion)// avoid circle reference
						((HtmlShadowElement)child)._nextInsertion = null;
				}
			} else if (refChild != null) {
				throw new IllegalAccessError("not implemented yet");
			}
		}
		
		super.beforeChildAdded(child, refChild);
	}
	
	@Override
	public boolean insertBefore(Component newChild, Component refChild) {
		// TODO Auto-generated method stub
		return super.insertBefore(newChild, refChild);
	}
	public void onChildAdded(org.zkoss.zk.ui.Component child) {
		super.onChildAdded(child);
		HtmlShadowElement childSE = (HtmlShadowElement) child;
		stretchRange(childSE._firstInsertion, childSE._lastInsertion, null);
	}
	
	public void onChildRemoved(org.zkoss.zk.ui.Component child) {
		super.onChildRemoved(child);
		HtmlShadowElement childSE = (HtmlShadowElement) child;
		shrinkRange(childSE._firstInsertion, childSE._lastInsertion, null);
	}
	
	// unsupported Component methods
	public void invalidate() {
		throw new UnsupportedOperationException(
				"Unsupported for shadow element's invalidation, please use getShadowHost().invalidate() instead.");
	}
	
	private Map<Component, Integer> fillIndexMap(Component first, Component last, Map<Component, Integer> indexMap) {
		Component parent = first.getParent();
		if (parent == null)
			throw new UiException("The insertion point cannot be null: " + first);
		List<Component> children = parent.getChildren();
		if (indexMap == null) {
			indexMap = new HashMap<Component, Integer>(children.size()); // worse case
		} else { // reuse map
			Integer integer = indexMap.get(first);
			if (integer != null) {
				if (indexMap.containsKey(last))
					return indexMap; //nothing to fill up
			}
		}
		int i = 0;
		for (Iterator<Component> it = children.iterator(); it.hasNext(); i++) {
			Component next = it.next();
			if (indexMap.isEmpty()) {
				if (first == next) {
					indexMap.put(next, i);
				}
			} else {
				indexMap.put(next, i);
				if (next == last)
					break;
			}
		}
		return indexMap;
	}
	@SuppressWarnings("unused")
	private int[] getInsertionIndex(Component firstChild, Component lastChild, Map<Component, Integer> indexMap) {
		if (indexMap == null) {
			indexMap = fillIndexMap(firstChild, lastChild, indexMap);
			return new int[] {indexMap.get(firstChild), indexMap.get(lastChild)};
		} else {
			Integer start = indexMap.get(firstChild), end = indexMap.get(lastChild);
			if (start == null || end == null) // refill
				indexMap = indexMap = fillIndexMap(firstChild, lastChild, indexMap);
			start = indexMap.get(firstChild);
			end = indexMap.get(lastChild);
			return new int[] {start, end};
		}
	}
	
	protected void stretchRange(Component firstChild, Component lastChild, Map<Component, Integer> indexMap) {
		if (firstChild != null) { // has children
			boolean isEdge = false;
			if (_firstInsertion == null) { // init
				_firstInsertion = firstChild;
				_lastInsertion = lastChild;
				isEdge = true;
			} else {
				if (indexMap == null) // init map
					indexMap = fillIndexMap(firstChild, lastChild, indexMap);
				
				int[] childIndex = getInsertionIndex(firstChild, lastChild, indexMap);
				int[] selfIndex = getInsertionIndex(_firstInsertion, _lastInsertion, indexMap);
				
				if (childIndex[0] < selfIndex[0]) { // left edge changed
					isEdge = true;
					_firstInsertion = firstChild;
				}
				if (selfIndex[1] < childIndex[1]) { // right edge changed
					isEdge = true;
					_lastInsertion = lastChild;
				}
			}
			if (isEdge && getParent() != null) {
				((HtmlShadowElement)getParent()).stretchRange(firstChild, lastChild, indexMap);
			}
		}
	}
	protected void shrinkRange(Component firstChild, Component lastChild, Map<Component, Integer> indexMap) {
		if (firstChild != null) { // has children
			boolean isEdge = false;
			if (firstChild == _firstInsertion) { // cut edge
				if (lastChild == _lastInsertion) { // cut all
					_firstInsertion = _lastInsertion = null;
				} else {
					// shrink from the start
					_firstInsertion = lastChild.getNextSibling();
				}
				isEdge = true;
			} else if (lastChild == _lastInsertion) {
				isEdge = true;
				
				// shrink from the end
				_lastInsertion = _lastInsertion.getPreviousSibling();
			}
			if (isEdge && getParent() != null) {
				((HtmlShadowElement)getParent()).shrinkRange(firstChild, lastChild, indexMap);
			}
		}
	}
	
	
	//Cloneable//
	public Object clone() {
		final HtmlShadowElement clone = (HtmlShadowElement)super.clone();
		clone.init();
		clone._props.putAll(_props);
		
		clone._previousInsertion = _previousInsertion;
		clone._firstInsertion = _firstInsertion;
		clone._lastInsertion = _lastInsertion;
		clone._nextInsertion = _nextInsertion;
		return clone;
	}

	//-- DynamicPropertied --//
	public boolean hasDynamicProperty(String name) {
		return _props.containsKey(name);
	}
	public Object getDynamicProperty(String name) {
		return _props.get(name);
	}
	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		_props.put(name, value);
	}

	public Component getShadowHost() {
		return _host;
	}
	/**
	 * Creates the distributed children after apply dynamic properties
	 * {@link #setDynamicProperty}.
	 * <p>
	 * If a shadow element is created by ZK loader, this method is invoked
	 * automatically. Developers rarely need to invoke this method.
	 * <p>
	 * Default: it invokes {@link #compose} to compose the shadow element.
	 * <p>
	 * The method will invoke the following methods in order.
	 * <ol>
	 * <li>Check if {@link #isEffective()} to be true.</li>
	 * <li>If true, invokes {@link #compose} method to create the distributed
	 * children, otherwise, nothing happened.</li>
	 * </ol>
	 * <p>
	 * Instead of overriding this method, it is suggested to override
	 * {@link #compose}, since all other methods depend on {@link #compose}
	 * (rather than {@link #afterCompose}).
	 */
	public void afterCompose() {
		if (isEffective() && _firstInsertion == null) { // don't do it twice, if it has a child.
			Component host = getShadowHostIfAny();
			if (host != null) {
				Object shadowInfo = ShadowElementsCtrl.getCurrentInfo();
				try {
					ShadowElementsCtrl.setCurrentInfo(this);
					compose(host);
				} finally {
					ShadowElementsCtrl.setCurrentInfo(shadowInfo);
				}
			}
		}
	}
	
	public boolean mergeChild(Component child) {
		if (child.getParent() != this) {
			return true;
		}
		return false; // same parent, nothing to do
	}
	
	public List<ShadowElement> rebuildShadowTree() {
		List<HtmlShadowElement> children = getChildren();
		List<ShadowElement> allChildren = new LinkedList<ShadowElement>();
		for (HtmlShadowElement se : new ArrayList<HtmlShadowElement>(children)) {
			allChildren.addAll(se.rebuildShadowTree());
		}
		if (isDynamicValue()) {
			for (ShadowElement  sse : allChildren) {
				// appendChild((Component)sse); cannot use appendChild to merge,
				// it causes wrong insertion reference between components and
				// shadow elements
				mergeChild((HtmlShadowElement)sse);
			}
			return Generics.cast(Arrays.asList(this));
		} else {
			detach();
			return allChildren;
		}
	}

	/**
	 * Returns whether the shadow element is effective
	 */
	protected abstract boolean isEffective();

	/**
	 * Composes the shadow element. It is called by {@link #afterCompose} if the
	 * shadow host is not null. Otherwise, it will skip this method call.
	 * <p>
	 * The second invocation is ignored. If you want to recreate child
	 * components, use {@link #recreate()} instead.
	 * @param host the shadow host component, never null.
	 * @return a set of components that contain Component and ShadowElement created in order.
	 */
	protected abstract Component[] compose(Component host);

	public void beforeHostChildRemoved(Component child, int indexOfChild) {
		log.warn("beforeHostChildRemoved " + child + ", in this shadow "  + 
				ShadowElementsCtrl.getCurrentInfo());

		Object currentInfo = ShadowElementsCtrl.getCurrentInfo();
		if (currentInfo instanceof HtmlShadowElement) { // removed as my child in our control code
			if (currentInfo == this) {
				boolean isEdge = false;
				Component oldFirst = _firstInsertion;
				Component oldLast = _lastInsertion;
				if (child == _firstInsertion) {
					if (_firstInsertion == _lastInsertion) {
						_firstInsertion = _lastInsertion = null;
					} else {
						_firstInsertion = child.getNextSibling();
						oldLast = oldFirst; // only remove one by one
					}
					isEdge = true;
				} else if (child == _lastInsertion) {
					isEdge = true;
					_lastInsertion = child.getPreviousSibling();
					oldFirst = oldLast; // only remove one by one
				}
				if (isEdge && getParent() != null) {
					((HtmlShadowElement)getParent()).shrinkRange(oldFirst, oldLast, null);
				}
				return; // finish
			} else {
				((HtmlShadowElement) currentInfo).beforeHostChildRemoved(child, indexOfChild);
				return; // finish
			}
		} else { // out of our control, we have to do Binary search for this  to 
			if (_firstInsertion == null) return; // out of our range;
			
			List<Component> children = child.getParent().getChildren();
			int selfFirstIndex = children.indexOf(_firstInsertion);
			if (indexOfChild < selfFirstIndex) return; // out of our range;
			
			Map<Component, Integer> indexMap = fillIndexMap(_firstInsertion, _lastInsertion, null);
			int[] selfIndex = getInsertionIndex(_firstInsertion, _lastInsertion, fillIndexMap(_firstInsertion, _lastInsertion, null));
			if (selfIndex[1] < indexOfChild) return; // out of our range;

			HtmlShadowElement node = queryIntersectedShadowIfAny(indexOfChild, indexMap);
			if (node != null) {
				try {
					ShadowElementsCtrl.setCurrentInfo(node);
					((HtmlShadowElement) node).beforeHostChildRemoved(child, indexOfChild);
				} finally {
					ShadowElementsCtrl.setCurrentInfo(currentInfo); // reset
				}
			}
		}

	}
	
	// as binary search for a segment tree.
	private HtmlShadowElement queryIntersectedShadowIfAny(int queryIndex, Map<Component, Integer> indexMap) {
		Object binarySearchSubTree = binarySearchSubTree(this, queryIndex, indexMap);
		if (binarySearchSubTree instanceof HtmlShadowElement)
			return (HtmlShadowElement) binarySearchSubTree;
		return null; // not found;
	}

	// return -1, not found;
	private int checkMiddleIndex(int low, int high) {
		if (low > high)
			return -1;
		return (low + high) >>> 1;
	}
	private Object binarySearchSubTree(HtmlShadowElement subTree, int queryIndex, Map<Component, Integer> indexMap) {
		int startIndex, endIndex;
		if (subTree._firstInsertion == null) {
			return -1; // skip this;
		} else if ((startIndex = indexMap.get(subTree._firstInsertion)) > queryIndex) {
			return startIndex; // find from left
		} else if ((endIndex = indexMap.get(subTree._lastInsertion)) < queryIndex) {
			return endIndex; // find from right
		}
		
		final int nChild = subTree.nChild();
		
		if (nChild == 0)
			return subTree; // subTree is the intersection node.
		
		int low = 0;
		int high = nChild - 1;
		int midChild = checkMiddleIndex(low, high);
		int mid = midChild;
		int midIndex = (endIndex - startIndex) >>> 1;
		while (low <= high && mid >= 0) {
			System.out.println(subTree + ">>>" + mid);
			HtmlShadowElement target = (HtmlShadowElement)subTree.getChildren().get(mid);
			Object result = binarySearchSubTree(target, queryIndex, indexMap);
			if (result instanceof Integer) {
				int queryResult = ((Integer) result).intValue();
				if (queryResult < 0) {// not found, find next
					int oldMid = mid;
					mid--;
					if (oldMid <= low) {
						low = midChild + 1; // not found and do it from right again.
						mid = checkMiddleIndex(low, high);
						midChild = mid;
						if (mid == oldMid)
							break; // nothing do to.
					}
//					if (midIndex > queryIndex) {
//						mid--;
//						if (mid <= low) {
//							low = midChild + 1; // not found and do it from right again.
//							mid = checkMiddleIndex(low, high);
//							if (mid == oldMid)
//								break; // nothing do to.
//						}
//					} else {
//						mid++;
//						if (mid > high) {
//							if (mid == midChild)
//								break; // not found;
//							high = midChild - 1; // not found do it from left again.
//							mid = checkMiddleIndex(low, high);
//							if (mid == oldMid)
//								break; // nothing do to.
//						}
//					}
				} else if (queryResult > -1) { // find but not match
					if (low == mid && mid == high) {
						break; // not found to avoid dead loop
					}
					if (queryResult < queryIndex) {
						low = mid + 1; // find from right
					} else {
						high = mid - 1; // find from left
					}
					int newMid = checkMiddleIndex(low, high);
					if (newMid == mid)
						break; // nothing do to.
					midChild = mid = newMid; 
				}
			} else {
				return result; // node is found.
			}
		}
		
		return subTree; // subTree is the intersection node.
	}
	public void beforeHostParentChanged(Component parent) {
		log.warn("beforeHostParentChanged " + parent + ", in this shadow "  + 
				ShadowElementsCtrl.getCurrentInfo());
	}

	public void beforeHostChildAdded(Component child, Component insertBefore, int indexOfInsertBefore) {
		log.warn("beforeHostChildAdded " + child + ", " + insertBefore + ", in this shadow "  + 
				ShadowElementsCtrl.getCurrentInfo());
		Object currentInfo = ShadowElementsCtrl.getCurrentInfo();
		if (indexOfInsertBefore < 0) {
			if (currentInfo instanceof HtmlShadowElement) { // in our control
				HtmlShadowElement current = (HtmlShadowElement) currentInfo;
				Component lastChild = current.getLastChild();
				if (lastChild != null)
					((HtmlShadowElement)lastChild)._nextInsertion = child;
			} else { // out of our control
//				if (_next == null)
//					_next = child;
			}
		} else { // special case
			// TODO
			if (currentInfo instanceof HtmlShadowElement) { // in our control
				
			} else {
				
			}
		}
//		if (insertBefore != null) {
//			int index = insertBefore.getParent().getChildren().indexOf(insertBefore);
//			HtmlShadowElement node = queryIntersectedShadowIfAny(child.getParent().getChildren().indexOf(child));
//			if (node != null) {
//				// need to shift the index to right.
//				node.shiftRange(index);
//			}
//		}
	}

	public void afterHostChildAdded(Component child, int indexOfChild) {
		log.warn("afterHostChildAdded " + child + ", in this shadow "  + 
				ShadowElementsCtrl.getCurrentInfo());
		Object currentInfo = ShadowElementsCtrl.getCurrentInfo();
		if (currentInfo instanceof HtmlShadowElement) { // added as my child in our control code
			Map<Component, Integer> indexMap = null;
			if (currentInfo == this) {
				boolean isEdge = false;
				if (_firstInsertion == null) { // initial range
					_firstInsertion = _lastInsertion = child;
					isEdge = true;
				} else {
					int[] selfIndex = getInsertionIndex(_firstInsertion, _lastInsertion, fillIndexMap(_firstInsertion, _lastInsertion, indexMap));
					if (indexOfChild < selfIndex[0]) {
						_firstInsertion = child;
						isEdge = true;
					} else if (indexOfChild > selfIndex[1]) {
						_lastInsertion = child;
						isEdge = true;
					}
					
				}
				if (getParent() != null && isEdge) {
					((HtmlShadowElement)getParent()).stretchRange(_firstInsertion, _lastInsertion, indexMap);
				}
				return; // finish
			} else {
				((HtmlShadowElement) currentInfo).afterHostChildAdded(child, indexOfChild);
				return; // finish
			}
		} else { // out of our control, we have to do Binary search for this  to
			if (_firstInsertion == null) return; // out of our range;
			
			List<Component> children = child.getParent().getChildren();
			int insertIndex = children.indexOf(child);
			int selfFirstIndex = children.indexOf(_firstInsertion);
			if (insertIndex < selfFirstIndex) return; // out of our range;
			
			Map<Component, Integer> indexMap = fillIndexMap(_firstInsertion, _lastInsertion, null);
			int[] selfIndex = getInsertionIndex(_firstInsertion, _lastInsertion, fillIndexMap(_firstInsertion, _lastInsertion, null));
			if (selfIndex[1] < insertIndex) return; // out of our range;

			HtmlShadowElement node = queryIntersectedShadowIfAny(insertIndex, indexMap);
			if (node != null) {
				try {
					ShadowElementsCtrl.setCurrentInfo(node);
					((HtmlShadowElement) node).afterHostChildAdded(child, indexOfChild);
				} finally {
					ShadowElementsCtrl.setCurrentInfo(currentInfo); // reset
				}
			}
		}
	}
	
	public void afterHostChildRemoved(Component child, int indexOfChild) {
		log.warn("afterHostChildRemoved " + child + ", in this shadow "  + 
				ShadowElementsCtrl.getCurrentInfo());
	}

	/** Detaches all child components and then recreate them by use of
	 * {@link #compose}.
	 */
	public void recreate() {
		if (_firstInsertion != null) {
			for (Component next = _firstInsertion, end = _lastInsertion.getNextSibling(); next != end;) {
				Component tmp = next.getNextSibling();
				next.detach();
				next = tmp;
			}
		}
		afterCompose();
		// 1. remove all distributed children
		// 2. so the same as afterCompose
	}
	
	/**
	 * Return the shadow host from it or its ancestor, if any. 
	 * @return null or a host component
	 */
	protected Component getShadowHostIfAny() {
		Component parent = this;
		 while (parent.getParent() != null) {
			 parent = parent.getParent();
		 }
		 return ((ShadowElement)parent).getShadowHost();
	}
	/**
	 * Removes the relation points between shadow host and this shadow element.
	 */
	public void detach() {
		// super.detach();
		if (_host != null) {
			((ComponentCtrl) _host).removeShadowRoot(this);
		}
		_host = null;
		setParent(null);
	}
	
	private final int nDChild() {
		if (_firstInsertion != null) {
			int size = 1;
			Component next = _firstInsertion;
			while (next != _lastInsertion) {
				size++;
				next = next.getNextSibling();
			}
			return size;
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> List<T> getDistributedChildren() {
		final Component shadowHostIfAny = getShadowHostIfAny();
		
		return new AbstractList<T>() {
			@SuppressWarnings("unchecked")
			public ListIterator<T> listIterator(int index) {
				return (ListIterator<T>)new ChildIter((AbstractComponent)shadowHostIfAny, index);
			}

			public int size() {
				return nDChild();
			}

			public T get(int index) {
				try {
					return listIterator(index).next();
				} catch (NoSuchElementException exc) {
					throw new IndexOutOfBoundsException("Index: " + index);
				}
			}
			
		};
	}
	private class ChildIter implements ListIterator<Component>  {
		private AbstractComponent _p, _lastRet;
		private int _j;
		private int _modCntSnap;
		private AbstractComponent host;

		private ChildIter(AbstractComponent host, int index) {
			this.host = host;
			int nChild;
			if (index < 0 || index > (nChild = nDChild()))
				throw new IndexOutOfBoundsException("Index: "+index+", Size: "+  nDChild());
			
			if (index < (nChild >> 1)) {
				_p = (AbstractComponent) HtmlShadowElement.this._firstInsertion;
				for (_j = 0; _j < index; _j++)
					_p = _p._next;
			} else {
				_p = null; //means the end of the list
				for (_j = nChild; _j > index; _j--)
					_p = _p != null ? _p._prev: (AbstractComponent)HtmlShadowElement.this._lastInsertion;
			}
			_modCntSnap = host.modCntChd();
		}
		public boolean hasNext() {
			checkComodification();
			return _j < nDChild();
		}
		public Component next() {
			if (_j >= nDChild())
				throw new java.util.NoSuchElementException();
			checkComodification();
			
			_lastRet = _p;
			_p = _p._next;
			_j++;
			return _lastRet;
		}
		public boolean hasPrevious() {
			checkComodification();
			return _j > 0;
		}
		public Component previous() {
			if (_j <= 0)
				throw new java.util.NoSuchElementException();
			checkComodification();

			_lastRet = _p = _p != null ? _p._prev: (AbstractComponent) HtmlShadowElement.this._lastInsertion;
			_j--;
			return _lastRet;
		}
		private void checkComodification() {
			if (host.modCntChd() != _modCntSnap)
				throw new java.util.ConcurrentModificationException();
		}
		public int nextIndex() {
			return _j;
		}
		public int previousIndex() {
			return _j - 1;
		}
		public void add(Component newChild) {
			throw new UnsupportedOperationException("add Component");
		}
		public void remove() {
			if (_lastRet == null)
				throw new IllegalStateException();
			checkComodification();

			if (_p == _lastRet) _p = _lastRet._next; //previous was called
			else --_j; //next was called

			host.removeChild(_lastRet);
			
			_lastRet = null;
			++_modCntSnap;
		}
		public void set(Component o) {
			throw new UnsupportedOperationException("set Component");
				//Possible to implement this but confusing to developers
				//if o has the same parent (since we have to move)
		}
	}

	
	// refer to AnnotateBinderHelper.BIND_ANNO
	final static private String BIND_ANNO = "bind";
	// refer to AnnotateBinderHelper.BIND_ANNO
	final static private String LOAD_ANNO = "load";
	// refer to AnnotateBinderHelper.SAVE_ANNO
	final static private String SAVE_ANNO = "save";
	// refer to AnnotateBinderHelper.REFERENCE_ANNO
	final static private String REFERENCE_ANNO = "ref";

	/**
	 * Returns whether the property name contains with a dynamic value.
	 */
	protected boolean isDynamicValue(String propName) {
		final ComponentCtrl compCtrl = this;
		Collection<Annotation> annos = compCtrl.getAnnotations(propName);
		if (!annos.isEmpty()) {
			for (Annotation anno : annos) {
				final String annoName = anno.getName();
				if (annoName.equals(BIND_ANNO) || annoName.equals(LOAD_ANNO)
						|| annoName.equals(SAVE_ANNO)
						|| annoName.equals(REFERENCE_ANNO)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns how many properties that support a dynamic value. This method is
	 * used by {@link #isDynamicValue()} to check automatically.
	 */
	public abstract List<String> getDynamicProperties();

	private Boolean _dynamicValue;

	public boolean isDynamicValue() {
		return true;
//		if (_dynamicValue == null) {
//			List<String> props = getDynamicProperties();
//			if (props != null) {
//				for (String prop : props) {
//					if (isDynamicValue(prop)) {
//						_dynamicValue = true;
//						break;
//					}
//				}
//				if (_dynamicValue == null)
//					_dynamicValue = Boolean.FALSE;
//			}
//		}
//		return _dynamicValue.booleanValue();
	}

	//-- Object --//
	public String toString() {
		final String clsnm = getClass().getSimpleName();
		if (_host == null) {
			if (getParent() != null)
				return  getParent() + " -> <" + clsnm + "@" +  (getParent().getChildren().indexOf(this)) +">";
			else
				return '<' + clsnm + '>';
		}
		return "<" + clsnm + " (" + _host + ")>";
	}
}