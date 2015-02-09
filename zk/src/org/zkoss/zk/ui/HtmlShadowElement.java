/** HtmlShadowElement.java.

	Purpose:
		
	Description:
		
	History:
		12:47:42 PM Oct 22, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zk.ui;

import java.util.AbstractList;
import java.util.AbstractSequentialList;
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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Generics;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.ShadowElementCtrl;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
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
	private Component _firstInsertion;
	private Component _lastInsertion;
	private Component _nextInsertion;
	private Component _previousInsertion;
	protected boolean _afterComposed = false;
	
	private Component _host;
	
	protected static String ON_REBUILD_SHADOW_TREE_LATER = 	"onRebuildShadowTreeLater";
	
	public Object resolveVariable(Component child, String name, boolean recurse) {
		if (_firstInsertion == null) // out of our range;
			return null;
		
		if (child == null || child.getParent() == null) {
			return getAttributeOrFellow(name, recurse);
		}
		
		List<Component> children = child.getParent().getChildren();
		int insertIndex = children.indexOf(child);
		int selfFirstIndex = children.indexOf(_firstInsertion);
		if (insertIndex < selfFirstIndex) return null; // out of our range;
		
		Map<Component, Integer> indexMap = fillUpIndexMap(_firstInsertion, _lastInsertion);
		int[] selfIndex = getInsertionIndex(_firstInsertion, _lastInsertion, indexMap);
		if (selfIndex[1] < insertIndex) return null; // out of our range;

		HtmlShadowElement node = queryIntersectedShadowIfAny(insertIndex, indexMap);
		if (node != null)
			return node.getShadowVariable(name, recurse);
		return null;
	}
	/**
	 * Returns the next component before this shadow, if any. (it will invoke recursively from its parent.)
	 */
	public Component getNextInsertionComponentIfAny() {
		if (_nextInsertion == null) {
			Component result = _lastInsertion == null ? null : _lastInsertion.getNextSibling();
			if (result == null && getParent() != null) {// ask for its parent
				return asShadow(getParent()).getNextInsertionComponentIfAny();
			}
			return result;
		} else if (_nextInsertion instanceof HtmlShadowElement) {
			HtmlShadowElement nextInsertion = asShadow(_nextInsertion);
			// ask for the firstInsertion first.
			if (nextInsertion._firstInsertion != null)
				return nextInsertion._firstInsertion;
			return nextInsertion.getNextInsertionComponentIfAny();
		}
		return _nextInsertion;
	}
	/**
	 * Returns the first component before this shadow, if any. (it will invoke recursively from its parent.)
	 */
	public Component getPreviousInsertionComponentIfAny() {
		if (_previousInsertion == null) {
			Component result = _firstInsertion == null ? null : _firstInsertion.getNextSibling();
			if (result == null && getParent() != null) // ask for its parent
				return asShadow(getParent()).getPreviousInsertionComponentIfAny();
			return result;
		} else if (_previousInsertion instanceof HtmlShadowElement) {
			HtmlShadowElement previousInsertion = asShadow(_previousInsertion);
			// ask for the lastInsertion first.
			if (previousInsertion._lastInsertion != null)
				return previousInsertion._lastInsertion;
			return previousInsertion.getPreviousInsertionComponentIfAny();
		}
		return _previousInsertion;
	}
	protected void onHostAttached(Component host) {
		Iterable<EventListener<? extends Event>> eventListeners = host.getEventListeners(ON_REBUILD_SHADOW_TREE_LATER);
		if (!eventListeners.iterator().hasNext()) {
			host.addEventListener(ON_REBUILD_SHADOW_TREE_LATER, new SerializableEventListener<Event>() {

				public void onEvent(Event event) throws Exception {
					Component target = event.getTarget();
					if (target instanceof ComponentCtrl && target.getDesktop() != null) {
						for (ShadowElement se : new ArrayList<ShadowElement>(((ComponentCtrl)target).getShadowRoots())) {
							if (se instanceof HtmlShadowElement) {
								((HtmlShadowElement) se).rebuildShadowTree();
							}
						}
					} else { // cleanup
						Iterable<EventListener<? extends Event>> eventListeners = target.getEventListeners(ON_REBUILD_SHADOW_TREE_LATER);
						for (EventListener<? extends Event> listener : eventListeners) {
							target.removeEventListener(ON_REBUILD_SHADOW_TREE_LATER, listener);
						}
					}
				}
				
			});
		}
	}
	protected void onHostDetached(Component host) {
		if (host instanceof ComponentCtrl) {
			if (((ComponentCtrl) host).getShadowRoots().isEmpty()) {
				Iterable<EventListener<? extends Event>> eventListeners = host.getEventListeners(ON_REBUILD_SHADOW_TREE_LATER);
				for (EventListener<? extends Event> listener : eventListeners) {
					host.removeEventListener(ON_REBUILD_SHADOW_TREE_LATER, listener);
				}
			}
		}
	}

	/**
	 * Returns the next insertion point, it may be a component, a shadow element, or null.
	 */
	public Component getNextInsertion() {
		return _nextInsertion;
	}

	/**
	 * Returns the previous insertion point, it may be a component, a shadow element, or null.
	 */
	public Component getPreviousInsertion() {
		return _previousInsertion;
	}

	/**
	 * Returns the first component of its insertion range.
	 */
	public Component getFirstInsertion() {
		return _firstInsertion;
	}

	/**
	 * Returns the last component of its insertion range.
	 */
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
		onHostAttached(host);
		
		_nextInsertion = insertBefore;
		if (insertBefore != null) {
			_previousInsertion = insertBefore.getPreviousSibling();
		} else {
			List<ShadowElement> shadowRoots = ((ComponentCtrl) host).getShadowRoots();
			ShadowElement lastShadowElement = shadowRoots.isEmpty() ? null : shadowRoots.get(shadowRoots.size() - 1);
			Component prev = (Component)lastShadowElement;
			HtmlShadowElement prevOwner = asShadow(lastShadowElement);
			Component lastChild = host.getLastChild();
			 
			if (prevOwner == null) {
				prev = lastChild;
			} else {
				switch (HtmlShadowElement.inRange(prevOwner, lastChild)) {
				case NEXT:
				case AFTER_NEXT:
				case UNKNOWN:
					prev = lastChild;
					break;
				default:
					// prev is the lastShadowElement 
				}
			}
			_previousInsertion = prev;
			if (prev == lastShadowElement && prev != null) {
				prevOwner._nextInsertion = this;
			}
		}
			
		((ComponentCtrl) host).addShadowRoot(this);
		host.getDesktop().getWebApp().getConfiguration().afterShadowAttached(this, host);
	}
	/**
	 * Removes the relation points between shadow host and this shadow element.
	 */
	public void detach() {
		Component prevhost = getShadowHostIfAny();
		if (_host != null) {
			ComponentCtrl host = (ComponentCtrl) _host;
			_host = null; // clear first to avoid endloop
			((ComponentCtrl) host).removeShadowRoot(this);
			onHostDetached((Component)host);
		}
		setParent0(null);
		if (prevhost != null && prevhost.getDesktop() != null) {
			prevhost.getDesktop().getWebApp().getConfiguration().afterShadowDetached(this, prevhost);
		}
	}
	
	
	public void setParent(Component parent) {
		Component host = getShadowHostIfAny();
		
		setParent0(parent);
		
		if (host == null)
			host = getShadowHostIfAny();
		
		if (host != null) {
			if (parent != null) {
				host.getDesktop().getWebApp().getConfiguration().afterShadowAttached(this, host);
			} else {
				host.getDesktop().getWebApp().getConfiguration().afterShadowDetached(this, host);
			}
		}
	}
	
	private void setParent0(Component parent) {
		if (_host != null && parent != null) {
			throw new UiException("As a shadow root cannot be a child of a shadow element.");
		}
		
		if (parent == null && _host == null) {
			// detach
			if (_firstInsertion != null) {
				setPrevInsertion(_firstInsertion, _previousInsertion); // resync
				setPrevInsertion(_nextInsertion, _lastInsertion); // resync
			} else {
				setPrevInsertion(_nextInsertion, _previousInsertion); // resync
			}
			_previousInsertion = null;
			_firstInsertion = null;
			_lastInsertion = null;
			_nextInsertion = null;
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
			}
		}
		super.beforeParentChanged(parent);
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof ShadowElement))
			throw new UiException("Unsupported child for shadow element: "
					+ child);
		if (refChild != null && !(refChild instanceof ShadowElement))
			throw new UiException("Unsupported refChild for shadow element: "
					+ refChild);
		HtmlShadowElement seChild = (HtmlShadowElement) child;
		HtmlShadowElement seRefChild = (HtmlShadowElement) refChild;
		
		HtmlShadowElement lastChild = asShadow(getLastChild());
		if (lastChild != null) {
			if (refChild == null) {
				if (lastChild._nextInsertion != null) {
					seChild._previousInsertion = lastChild._nextInsertion;
					if (seChild._nextInsertion == lastChild._nextInsertion)// avoid circle reference
						seChild._nextInsertion = null;
				} else {
					lastChild._nextInsertion = child;
					seChild._previousInsertion = lastChild;
				}
			} else if (seRefChild != null) {
				//throw new IllegalAccessError("not implemented yet");
//				if (isAncestor(asShadow(seRefChild.getParent()), seChild)) {
				
				// sync child's insertion
				Component previousInsertion = seChild.getPreviousInsertion();
				Component nextInsertion = seChild.getNextInsertion();
				setPrevInsertion(nextInsertion, previousInsertion);
				
				// sync refChild's insertion				
				previousInsertion = seRefChild.getPreviousInsertion();
				setPrevInsertion(seRefChild, seChild);
				setPrevInsertion(seChild, previousInsertion);
			}
		} else if (_lastInsertion != null) {
			if (refChild != null) {
				throw new IllegalStateException("Some logic wrong here.");
			} else {
				if (_lastInsertion instanceof HtmlShadowElement) {
					setPrevInsertion(seChild, _lastInsertion);
					if (seChild._nextInsertion == ((HtmlShadowElement)_lastInsertion)._nextInsertion)// avoid circle reference
						seChild._nextInsertion = null;
				} else {
					seChild._previousInsertion = _lastInsertion;
				}
			}
		}
		
		super.beforeChildAdded(child, refChild);
	}
	
	public void onChildAdded(org.zkoss.zk.ui.Component child) {
		super.onChildAdded(child);
		HtmlShadowElement childSE = asShadow(child);
		stretchRange(childSE._firstInsertion, childSE._lastInsertion);
	}
	
// no need to handle if the children range is included.
// For example,
// 		A => {B => {0,1}, 2}
//		So A's range is 0~2, if B is removed.
// 		
//	public void onChildRemoved(org.zkoss.zk.ui.Component child) {
//		super.onChildRemoved(child);
//		HtmlShadowElement childSE = asShadow(child);
//		shrinkRange(childSE._firstInsertion, childSE._lastInsertion);
//	}
	
	private Map<Component, Integer> getIndexMap() {
		Map<Component, Integer> distributedIndexInfo = (Map<Component, Integer>)ShadowElementsCtrl.getDistributedIndexInfo();
		if (distributedIndexInfo == null) {
			throw new IllegalStateException("Distributed index map cannot be null! [" + this + "]");
		}
		return distributedIndexInfo;
	}
	// unsupported Component methods
	public void invalidate() {
		throw new UnsupportedOperationException(
				"Unsupported for shadow element's invalidation, please use getShadowHost().invalidate() instead.");
	}
	
	private Map<Component, Integer> fillUpIndexMap(Component first, Component last) {
		if (first == null) // last will be null too 
			return getIndexMap();
		Component parent = first.getParent();
		if (parent == null)
			throw new UiException("The insertion point cannot be null: " + first);
		List<Component> children = parent.getChildren();
		Map<Component, Integer> indexMap = getIndexMap();
		// reuse map
		Integer integer = indexMap.get(first);
		if (integer != null) {
			if (indexMap.containsKey(last))
				return indexMap; //nothing to fill up
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
			indexMap = fillUpIndexMap(firstChild, lastChild);
			return new int[] {indexMap.get(firstChild), indexMap.get(lastChild)};
		} else {
			Integer start = indexMap.get(firstChild), end = indexMap.get(lastChild);
			if (start == null || end == null) // refill
				indexMap = fillUpIndexMap(firstChild, lastChild);
			start = indexMap.get(firstChild);
			end = indexMap.get(lastChild);
			return new int[] {start, end};
		}
	}
	
	protected void stretchRange(Component firstChild, Component lastChild) {
		if (firstChild != null) { // has children
			boolean isEdge = false;
			if (_firstInsertion == null) { // init
				_firstInsertion = firstChild;
				_lastInsertion = lastChild;
				isEdge = true;
			} else {
				Map<Component, Integer> indexMap = fillUpIndexMap(firstChild, lastChild);
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
				asShadow(getParent()).stretchRange(firstChild, lastChild);
			}
		}
	}
	protected void shrinkRange(Component firstChild, Component lastChild) {
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
				asShadow(getParent()).shrinkRange(firstChild, lastChild);
			}
		}
	}
	
	
	//Cloneable//
	public Object clone() {
		final HtmlShadowElement clone = (HtmlShadowElement)super.clone();
		
		clone._previousInsertion = _previousInsertion;
		clone._firstInsertion = _firstInsertion;
		clone._lastInsertion = _lastInsertion;
		clone._nextInsertion = _nextInsertion;
		return clone;
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
		if (!_afterComposed) { // don't do it twice, if it has a child.
			_afterComposed = true;
			if (isEffective() && _firstInsertion == null) {
				Component host = getShadowHostIfAny();
				if (host == null)
					throw new UiException("Host cannot be null [" + this + "]");
				Object shadowInfo = ShadowElementsCtrl.getCurrentInfo();
				try {
					ShadowElementsCtrl.setCurrentInfo(this);
					compose(host);
				} finally {
					ShadowElementsCtrl.setCurrentInfo(shadowInfo);
				}
				Execution exec = Executions.getCurrent();
				if (exec != null) {
					String key = "org.zkoss.zk.ui.HttmlShadowelement" + host.getUuid();
					if (!exec.hasAttribute(key)) {
						exec.setAttribute(key, Boolean.TRUE);
						
						// put it to the end of all events
						Events.postEvent(-250000, new Event(ON_REBUILD_SHADOW_TREE_LATER, host));
					}
				}
				
			}
		}
	}
	protected static void setPrevInsertion(Component target, Component prevInsertion) {
		if (target == prevInsertion)
			return; // do nothing
		
		if (target instanceof HtmlShadowElement) {
			asShadow(target)._previousInsertion = prevInsertion; 
		}
			
		if (prevInsertion instanceof HtmlShadowElement) {
			asShadow(prevInsertion)._nextInsertion = target;
		}
	}

	/**
	 * Merge the all sub-tree into the parent's insertions, unlike
	 * {@link #appendChild(Component)}
	 */
	protected void mergeSubTree() {
		List<HtmlShadowElement> children = getChildren();
		if (children == null || children.isEmpty())
			return ;// nothing to do.
		if (_parent != null) {
			for (HtmlShadowElement child : new ArrayList<HtmlShadowElement>(children)) {
				Component previous = child._previousInsertion;
				Component next = child._nextInsertion;
				_parent.insertBefore(child, this);
	
				// resync the insertion of the child, if it has some comopnent sibling.
				if (previous != null && !(previous instanceof HtmlShadowElement)) {
					Component newPrevious = child._previousInsertion;
					setPrevInsertion(previous, newPrevious);
					setPrevInsertion(child, previous);
				}
				if (next != null && !(next instanceof HtmlShadowElement)) {
					Component newNext = child._nextInsertion;
					setPrevInsertion(newNext, next);
					setPrevInsertion(next, child);
				}

				if (_firstInsertion == child._firstInsertion)
					_firstInsertion = null; // reset

				if (_lastInsertion == child._lastInsertion)
					_lastInsertion = null; // reset
			}
		} else { // merge to host
			for (HtmlShadowElement child : new ArrayList<HtmlShadowElement>(children)) {
				Component previous = _previousInsertion;
				
				child.mergeToHost(_host);
				
				if (previous != null) {
					Component newPrevious = child._previousInsertion;
					if (newPrevious == null) {
						setPrevInsertion(child, previous);
					} else {
						setPrevInsertion(newPrevious, previous);
					}
				}
				Component newNext = child._nextInsertion;
				if (newNext == null) {
					setPrevInsertion(this, child);
				} else {
					setPrevInsertion(this, newNext);
				}
				if (_firstInsertion == child._firstInsertion)
					_firstInsertion = null; // reset

				if (_lastInsertion == child._lastInsertion)
					_lastInsertion = null; // reset
			}
		}
	}

	/**
	 * Merge the host into the current shadow, unlike
	 * {@link #setShadowHost(Component, Component)}
	 * 
	 * @param child
	 */
	public boolean mergeToHost(Component host) {
		if (host == null)
			throw new UiException("The host cannot be null.");
		if (host == _host)
			return false; // nothing to do
		if (_parent == null)
			throw new UiException("The parent shadow cannot be null.");
		HtmlShadowElement oldParent = (HtmlShadowElement)_parent;
		
		if (host != _host) {
			HtmlShadowElement parent = (HtmlShadowElement) _parent;
			_parent = null;
			((ComponentCtrl) host).addShadowRootBefore(this, (ShadowElement) parent);
			_host = host;

			// remove children reference
			++ parent._chdinf.modCntChd;
			-- parent._chdinf.nChild;
			if (this._prev == null)
				parent._chdinf.first = this._next;
			if (this._next == null)
				parent._chdinf.last = this._prev;
			
			return true;
		}
		return false;
	}

	private void rebuildShadowTree() {
		Map<Component, Integer> oldCacheMap = getIndexCacheMap();
		final boolean destroyCacheMap = oldCacheMap == null;
		try {
			if (destroyCacheMap) // the first caller
				initIndexCacheMap();

			rebuildSubShadowTree();
		} finally {
			if (destroyCacheMap) // the first caller
				destroyIndexCacheMap();
		}
	}

	/**
	 * Rebuilds the shadow tree if the shadow element contains a dynamic value,
	 * it should be alive, otherwise, it will be detached.
	 * @throws ConcurrentModificationException if caller use the same collection,
	 * it may throw this exception when merging sub-tree.
	 */
	protected void rebuildSubShadowTree() {
		List<HtmlShadowElement> children = getChildren();
		for (HtmlShadowElement se : new ArrayList<HtmlShadowElement>(children)) {
			se.rebuildSubShadowTree();
		}
		if (!isDynamicValue()) {
			mergeSubTree();
			detach();
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
	 */
	protected abstract void compose(Component host);

	public void beforeHostChildRemoved(Component child, int indexOfChild) {
		if (log.isDebugEnabled()) {
			log.debug("beforeHostChildRemoved " + child + ", in this shadow "  + 
				ShadowElementsCtrl.getCurrentInfo());
		}

		Object currentInfo = ShadowElementsCtrl.getCurrentInfo();
		if (currentInfo instanceof HtmlShadowElement) { // removed as my child in our control code
			if (currentInfo == this) {
				// do it at beginning. 
				adjustInsertionForRemove(this, child);
				
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
					asShadow(getParent()).shrinkRange(oldFirst, oldLast);
				}
				return; // finish
			} else if (isAncestor(this, asShadow(currentInfo))) {// do only my descendent
				asShadow(currentInfo).beforeHostChildRemoved(child, indexOfChild);
				return; // finish
			}
		} else { // out of our control, we have to do Binary search for this  to 
			if (_firstInsertion == null) return; // out of our range;
			
			List<Component> children = child.getParent().getChildren();
			int selfFirstIndex = children.indexOf(_firstInsertion);
			if (indexOfChild < selfFirstIndex) return; // out of our range;
			
			Map<Component, Integer> indexMap = fillUpIndexMap(_firstInsertion, _lastInsertion);
			int[] selfIndex = getInsertionIndex(_firstInsertion, _lastInsertion, fillUpIndexMap(_firstInsertion, _lastInsertion));
			if (selfIndex[1] < indexOfChild) return; // out of our range;

			HtmlShadowElement node = queryIntersectedShadowIfAny(indexOfChild, indexMap);
			if (node != null) {
				try {
					ShadowElementsCtrl.setCurrentInfo(node);
					asShadow(node).beforeHostChildRemoved(child, indexOfChild);
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
			return asShadow(binarySearchSubTree);
		return null; // not found;
	}
	
	private class BinarySearchIterator {
		private HtmlShadowElement _subTree;
		private int _low, _high, _mid, _midChild, _queryIndex;
		public BinarySearchIterator(HtmlShadowElement subTree, int nChild, int queryIndex) {
			_subTree = subTree;
			_low = 0;
			_high = nChild - 1;
			_midChild = getMiddleIndex(_low, _high);
			_mid = _midChild;
			_queryIndex = queryIndex;
		}

		// return -1, not found;
		private int getMiddleIndex(int low, int high) {
			if (low > high)
				return -1;
			return (low + high) >>> 1;
		}

		public boolean hasNext() {
			return _low <= _high && _mid >= 0;
		}

		public HtmlShadowElement next() {
			return asShadow(_subTree.getChildren().get(_mid));
		}
		
		private void checkIndex() {
			int newMid = getMiddleIndex(_low, _high);
			if (_mid == newMid)
				_mid = -1; // nothing do to.
			else
				_midChild = _mid = newMid;
		}
		public void adjustCursor(Integer result) {
			final int queryResult = result.intValue();
			if (queryResult < 0) {// not found, find next
				if (_mid <= _low) {
					_low = _midChild + 1; // not found and do it from right again.
					checkIndex();
				} else {
					_mid--;
				}
			} else if (queryResult > -1) { // find but not match
				if (_low == _mid && _mid == _high) {
					_mid = -1; // not found to avoid dead loop
				} else {
					if (queryResult < _queryIndex) {
						_low = _mid + 1; // find from right
					} else {
						_high = _mid - 1; // find from left
					}
					checkIndex();
				}
			}
		}
		
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
		
		
//		int midIndex = (endIndex - startIndex) >>> 1;
		BinarySearchIterator bsit = new BinarySearchIterator(subTree, nChild, queryIndex);
		while (bsit.hasNext()) {
			Object result = binarySearchSubTree(bsit.next(), queryIndex, indexMap);
			if (result instanceof Integer) {
				bsit.adjustCursor((Integer)result);
			} else {
				return result; // node is found.
			}
		}
		
		return subTree; // subTree is the intersection node.
	}
	
	public void beforeHostParentChanged(Component parent) {
		if (log.isDebugEnabled()) {
			log.debug("beforeHostParentChanged " + parent + ", in this shadow "  + 
				ShadowElementsCtrl.getCurrentInfo());
		}
		if (parent == null) {
			((ComponentCtrl) _host).removeShadowRoot(this);
		} else if (_host.getParent() == null) {
			onHostAttached(_host);
		}
	}

	public void beforeHostChildAdded(Component child, Component insertBefore, int indexOfInsertBefore) {
		if (log.isDebugEnabled()) {
			log.warn("beforeHostChildAdded " + child + ", " + insertBefore + ", in this shadow "  + 
					ShadowElementsCtrl.getCurrentInfo());
		}
		
		Object currentInfo = ShadowElementsCtrl.getCurrentInfo();
		if (indexOfInsertBefore < 0) {
			if (currentInfo instanceof HtmlShadowElement) { // in our control
				HtmlShadowElement asShadow = asShadow(currentInfo);
				if (isAncestor(this, asShadow)) {// do only my descendent
					Component lastChild = asShadow.getLastChild();
					if (lastChild != null)
						asShadow(lastChild)._nextInsertion = child;
				} else if (asShadow.getShadowHostIfAny() != getShadowHostIfAny()) { // not my ancestor, it may create by template and another host
					if (_nextInsertion == null)
						_nextInsertion = child;
				}
			} else { // out of our control
				if (_nextInsertion == null)
					_nextInsertion = child;
			}
		} else { // special case
			Map<Component, Integer> indexMap = fillUpIndexMap(_firstInsertion, _lastInsertion);
			HtmlShadowElement node = queryIntersectedShadowIfAny(indexOfInsertBefore, indexMap);
			if (currentInfo instanceof HtmlShadowElement) { // in our control
				if (isAncestor(asShadow(currentInfo), node)) {
					adjustInsertionForInsertBefore(node, child, insertBefore);
				} else if (!((HtmlShadowElement) currentInfo).getChildren().isEmpty()) { // adjust from currentInfo's first.
					HtmlShadowElement currentShadow = asShadow(currentInfo);
					asShadow(currentShadow.getLastChild())._nextInsertion = child;
				}
			} else if (node != null) {
				// check if the insertion is before the shadow root range,
				// if true, do nothing.
				if (this.getParent() != null || insertBefore != _firstInsertion) {
					adjustInsertionForInsertBefore(node, child, insertBefore);
				} else { // in front of the shadow root.
					_previousInsertion = child; 
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	protected static <T extends HtmlShadowElement> T asShadow(Object o) {
		return (T) o; 
	}
	private boolean isAncestor(HtmlShadowElement parent, HtmlShadowElement child) {
		if (child == null) return false;
		if (parent == child) return true;
		return isAncestor(parent, asShadow(child.getParent()));
	}
	private boolean adjustInsertionForRemove(HtmlShadowElement se, Component removed) {
		Component old = null;
		Direction direction = inRange(se, removed);
		switch (direction) {
		case PREVIOUS:
			old = se._previousInsertion;
			if (old != null) {
				HtmlShadowElement previousSibling = asShadow(se.getPreviousSibling());
				if (previousSibling != null) { // se is not the first shadow element
					if (previousSibling._nextInsertion == old) {
						previousSibling._nextInsertion = se;
						se._previousInsertion = previousSibling;
					} else { // some children between two shadows
						se._previousInsertion = old.getPreviousSibling();
					}
				} else { // se is the first shadow element
					HtmlShadowElement parentSe = asShadow(se.getParent());
					
					// Update it when the following conditions:
					// 1. se is the root tree, update it directly.
					// 2. check if the old is not the first insertion of parent, that
					// means still has some children in front of the se. 
					if (parentSe == null || parentSe._firstInsertion != old) {
						se._previousInsertion = old.getPreviousSibling();
					} else {
						se._previousInsertion = null;
					}
				}
				return true;
			}
		case NEXT:
			old = se._nextInsertion;
			if (old != null) {
				HtmlShadowElement nextSibling = asShadow(se.getNextSibling());
				if (nextSibling != null) { // se is not the last shadow element
					if (nextSibling._previousInsertion == old) {
						nextSibling._previousInsertion = se;
						se._nextInsertion = nextSibling;
					} else { // some children between two shadows
						se._nextInsertion = old.getNextSibling();
					}
				} else { // se is the last shadow element
					HtmlShadowElement parentSe = asShadow(se.getParent());
					
					// Update it when the following conditions:
					// 1. se is the root tree, update it directly.
					// 2. check if the old is not the last insertion of parent, that
					// means still has some children at the end of the se. 
					if (parentSe == null || parentSe._lastInsertion != old) {
						se._nextInsertion = old.getPreviousSibling();
					} else {
						se._nextInsertion = null;
					}
				}
				return true;
			}
		case IN_RANGE: // check children
		case FIRST:
		case LAST:
			List<HtmlShadowElement> children = se.getChildren();
			if (!children.isEmpty()) {
				for (Iterator<HtmlShadowElement> sit = children.iterator(); sit.hasNext();) {
					if (adjustInsertionForRemove(sit.next(), removed))
						return true;
				}
			}
		default:
			return false;
		}
	}
	private boolean adjustInsertionForInsertBefore(HtmlShadowElement se, Component target, Component insertBefore) {
		Component old = null;
		Direction direction = inRange(se, insertBefore);
		switch (direction) {
		case PREVIOUS:
			old = se._previousInsertion;
			se._previousInsertion = target;
			if (old instanceof HtmlShadowElement) {
				asShadow(old)._nextInsertion = target;
			}
			return true;
		case NEXT:
			old = se._nextInsertion;
			se._nextInsertion = target;
			if (old instanceof HtmlShadowElement) {
				asShadow(old)._previousInsertion = target;
			}
			return true;
		case IN_RANGE: // check children
		case FIRST:
		case LAST:
			List<HtmlShadowElement> children = se.getChildren();
			if (children.isEmpty()) {
				if (direction == Direction.FIRST) {
					// update previous sibling
					old = se._previousInsertion;
					se._previousInsertion = target;
					if (old instanceof HtmlShadowElement) {
						asShadow(old)._nextInsertion = target;
					}
					return true;
					
				}
			} else {
				for (Iterator<HtmlShadowElement> sit = children.iterator(); sit.hasNext();) {
					if (adjustInsertionForInsertBefore(sit.next(), target, insertBefore))
						return true;
				}
			}
		default:
			return false;
		}
	}
	/**
	 * A help class for an insertion direction.
	 * @author jumperchen
	 */
	public enum Direction {
		/**
		 * It indicates the direction of the target is inserted before the previous insertion
		 */
		BEFORE_PREVIOUS,
		/**
		 * It indicates the direction of the target is the same as the previous insertion
		 */
		PREVIOUS,
		/**
		 * It indicates the direction of the target is the same as the first insertion
		 */
		FIRST,
		/**
		 * It indicates the direction of the target is inserted in its descendant insertion range
		 */
		IN_RANGE,
		/**
		 * It indicates the direction of the target is the same as the last insertion
		 */
		LAST,
		/**
		 * It indicates the direction of the target is the same as the next insertion
		 */
		NEXT,
		/**
		 * It indicates the direction of the target is inserted after the next insertion
		 */
		AFTER_NEXT,
		/**
		 * It cannot indicate the direction of the target where it should be inserted.
		 */
		UNKNOWN
	}
	
	private static int getIndex(HtmlShadowElement owner, Component insertion, Map<Component, Integer> cacheMap) {
		if (insertion == null)
			return -1;
		if (insertion.getParent() == null) {
			if (owner == null) {
				throw new IllegalStateException("The insertion cannot be orphan" + insertion);
			} else {
				if (insertion instanceof HtmlShadowElement && ((HtmlShadowElement) insertion).getShadowHost() != null) {
					return -1;
				} else {
					throw new IllegalStateException("The insertion [" + insertion +
							"] of the shadow [" + owner + "] cannot be orphan");
				}
			}
		}
		if (insertion instanceof ShadowElement)
			return -1; // cannot compare component with shadow
		Integer result = cacheMap.get(insertion);
		if (result != null)
			return result;
		int i = 0;
		int matched = -1;
		for (Iterator<Component> it = insertion.getParent().getChildren().iterator(); it.hasNext(); i++) {
			Component next = it.next();
			cacheMap.put(next, new Integer(i));
			if (next == insertion)
				matched = i;
		}
		return matched;
	}
	
	/**
	 * Returns the direction of the target component according to the given shadow element.
	 * @param se the shadow element
	 * @param target the target to check.
	 */
	public static Direction inRange(HtmlShadowElement se, Component target) {
		Map<Component, Integer> oldCacheMap = se.getIndexCacheMap();
		final boolean destroyCacheMap = oldCacheMap == null;
		try {
			// cache the index.
			if (destroyCacheMap)
				oldCacheMap = se.initIndexCacheMap();
			
			int targetIndex = getIndex(null, target, oldCacheMap);
			int prev = getIndex(se, se.getPreviousInsertion(), oldCacheMap);
			int first = getIndex(se, se.getFirstInsertion(), oldCacheMap);
			int last = getIndex(se, se.getLastInsertion(), oldCacheMap);
			int next = getIndex(se, se.getNextInsertion(), oldCacheMap);
			if (targetIndex == prev) {
				return Direction.PREVIOUS;
			} else if (targetIndex == first) {
				return Direction.FIRST;
			} else if (targetIndex == last) {
				return Direction.LAST;
			} else if (targetIndex == next) {
				return Direction.NEXT;
			} else if (targetIndex > first && targetIndex < last) {
				return Direction.IN_RANGE;
			} else if (prev > -1) {
				return targetIndex - prev > 0 ? Direction.AFTER_NEXT : Direction.BEFORE_PREVIOUS;
			} else if (first > -1) {
				return targetIndex - first > 0 ? Direction.AFTER_NEXT : Direction.BEFORE_PREVIOUS;	
			} else if (next > -1) {
				return targetIndex - next > 0 ? Direction.AFTER_NEXT : Direction.BEFORE_PREVIOUS;
			} else if (last > -1) {
				return targetIndex - last > 0 ? Direction.AFTER_NEXT : Direction.BEFORE_PREVIOUS;	
			} else {
				return Direction.UNKNOWN;
			}
		} finally {
			if (destroyCacheMap)
				se.destroyIndexCacheMap();
		}
	}
	public void afterHostChildAdded(Component child, int indexOfChild) {
		if (log.isDebugEnabled()) {
			log.debug("afterHostChildAdded " + child + ", in this shadow "  + 
					ShadowElementsCtrl.getCurrentInfo());
		}
		Object currentInfo = ShadowElementsCtrl.getCurrentInfo();
		if (currentInfo instanceof HtmlShadowElement) { // added as my child in our control code
			if (currentInfo == this) {
				boolean isEdge = false;
				if (_firstInsertion == null) { // initial range
					_firstInsertion = _lastInsertion = child;
					isEdge = true;
				} else if (_firstInsertion != child && _lastInsertion != child) {
					int[] selfIndex = getInsertionIndex(_firstInsertion, _lastInsertion, fillUpIndexMap(_firstInsertion, _lastInsertion));
					if (indexOfChild < selfIndex[0]) {
						_firstInsertion = child;
						isEdge = true;
					} else if (indexOfChild > selfIndex[1]) {
						_lastInsertion = child;
						isEdge = true;
					}
					
				}
				if (getParent() != null && isEdge) {
					asShadow(getParent()).stretchRange(_firstInsertion, _lastInsertion);
				}
				return; // finish
			} else if (isAncestor(this, asShadow(currentInfo))) {// do only my descendent
				asShadow(currentInfo).afterHostChildAdded(child, indexOfChild);
				return; // finish
			}
		} else { // out of our control, we have to do Binary search for this  to
			if (_firstInsertion == null) return; // out of our range;
			
			List<Component> children = child.getParent().getChildren();
			int insertIndex = children.indexOf(child);
			int selfFirstIndex = children.indexOf(_firstInsertion);
			if (insertIndex < selfFirstIndex) return; // out of our range;
			
			Map<Component, Integer> indexMap = fillUpIndexMap(_firstInsertion, _lastInsertion);
			int[] selfIndex = getInsertionIndex(_firstInsertion, _lastInsertion, indexMap);
			if (selfIndex[1] < insertIndex) return; // out of our range;

			HtmlShadowElement node = queryIntersectedShadowIfAny(insertIndex, indexMap);
			if (node != null) {
				try {
					ShadowElementsCtrl.setCurrentInfo(node);
					asShadow(node).afterHostChildAdded(child, indexOfChild);
				} finally {
					ShadowElementsCtrl.setCurrentInfo(currentInfo); // reset
				}
			}
		}
	}
	
	public void afterHostChildRemoved(Component child) {
		if (log.isDebugEnabled()) {
			log.debug("afterHostChildRemoved " + child + ", in this shadow "  + 
				ShadowElementsCtrl.getCurrentInfo());
		}
	}

	/** Detaches all child components and then recreate them by use of
	 * {@link #compose}.
	 */
	public void recreate() {
		if (_afterComposed) { // execute after composed
			if (!getChildren().isEmpty()) { // clean up all and then re-attached
				getChildren().clear();
			}
			
			if (_firstInsertion != null) {
				for (Component next = _firstInsertion, end = _lastInsertion.getNextSibling(); next != end;) {
					Component tmp = next.getNextSibling();
					next.detach();
					next = tmp;
				}
			}
			_afterComposed = false; // reset
			afterCompose();
		}
	}
	
	public Component getShadowHostIfAny() {
		Component parent = this;
		 while (parent.getParent() != null) {
			 parent = parent.getParent();
		 }
		 return ((ShadowElement)parent).getShadowHost();
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
		
		return new AbstractSequentialList<T>() {
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

	// refer to AnnotateBinderHelper.INIT_ANNO
	final static protected String INIT_ANNO = "init";
	// refer to AnnotateBinderHelper.BIND_ANNO
	final static protected String BIND_ANNO = "bind";
	// refer to AnnotateBinderHelper.BIND_ANNO
	final static protected String LOAD_ANNO = "load";
	// refer to AnnotateBinderHelper.SAVE_ANNO
	final static protected String SAVE_ANNO = "save";
	// refer to AnnotateBinderHelper.REFERENCE_ANNO
	final static protected String REFERENCE_ANNO = "ref";
	// refer to BinderImpl.BINDER
	final static protected String BINDER = "$BINDER$";

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
						|| annoName.equals(REFERENCE_ANNO)
						|| annoName.equals(INIT_ANNO)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Boolean _dynamicValue;

	public boolean isDynamicValue() {
		if (_dynamicValue == null) {
			final ComponentCtrl ctrl = this;
			List<String> props = ctrl.getAnnotatedProperties();
			if (props != null) {
				for (String prop : props) {
					if (isDynamicValue(prop)) {
						_dynamicValue = true;
						break;
					}
				}
				if (_dynamicValue == null)
					_dynamicValue = Boolean.FALSE;
			}
		}
		return _dynamicValue.booleanValue();
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
		ComponentCtrl host = (ComponentCtrl)_host;
		return "<" + clsnm + "@" + host.getShadowRoots().indexOf(this) +" (" + _host + ")>";
	}
	
	@Override
	protected void updateSubBindingAnnotationCount(int diff) {
		for (AbstractComponent node = this; node != null; ) {
			setSubBindingAnnotationCount(diff, node);
			AbstractComponent p = (AbstractComponent) node.getParent();
		 	if (p != null) {
		 		node = p;
		 	} else {
		 		node = (AbstractComponent) ((HtmlShadowElement)node).getShadowHost();
		 		if (node != null) 
		 			node.updateSubBindingAnnotationCount(diff);
		 		break;
		 	}
		}
	}
}