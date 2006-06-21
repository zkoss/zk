/* UiVisualizer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 10:57:48     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.io.StringWriter;
import java.io.IOException;

import com.potix.lang.D;
import com.potix.lang.Objects;
import com.potix.util.logging.Log;

import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Component.Range;
import com.potix.zk.ui.Components;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.sys.Visualizer;
import com.potix.zk.ui.sys.DesktopCtrl;
import com.potix.zk.ui.sys.PageCtrl;
import com.potix.zk.au.*;

/**
 * An implementation of {@link Visualizer} that works with
 * {@link UiEngineImpl}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
/*package*/ class UiVisualizer implements Visualizer {
	private static final Log log = Log.lookup(UiVisualizer.class);

	/** The first exec info that causes a chain of executions (never null).
	 */
	private final UiVisualizer _1stec;

	/** The associated execution. */
	private final Execution _exec;
	/** A set of invalidated pages. */
	private Set _pgInvalid;
	/** A set of removed pages. */
	private Set _pgRemoved;
	/** A set of invalidated components  (Component, Range). */
	private Map _invalidated = new LinkedHashMap();
	/** A map of smart updates (Component comp, Map(String attr, String value)). */
	private Map _smartUpdated = new LinkedHashMap();
	/** A set of new attached components. */
	private Set _attached = new LinkedHashSet();
	/** A set of moved components (parent changed or page changed). */
	private Set _moved = new LinkedHashSet();
	/** A map of components whose UUID is changed (Component, UUID). */
	private Map _idChgd;
	/** A map of responses being added(Component/Page, Map(key, AuResponse)). */
	private Map _responses;
	/** A stack of components that are including new pages (and then
	 * become the owner of the new page, if any).
	 */
	private final List _owners;
	/** Used to time the response added thru {@link #addResponse}. */
	private int _resptime = 0;
	/** if not null, it means the current executing is aborting
	 * and the content is reason to aborting. Its interpretation depends
	 * on {@link com.potix.zk.ui.sys.UiEngine}.
	 */
	private AbortingReason _aborting;
	/** Whether the first execution (_1stec) is for async-update. */
	private final boolean _1stau;

	/**
	 * Creates a root execution (without parent).
	 * In other words, it must be the first execution in the current request.
	 *
	 * @param asyncUpdate whether this exec is for async-update
	 */
	public UiVisualizer(Execution exec, boolean asyncUpdate) {
		_exec = exec;
		_1stec = this;
		_1stau = asyncUpdate;
		_owners = new LinkedList();
	}
	/**
	 * Creates the following execution.
	 * The first execution must use {@link #UiVisualizer(Execution, boolean)}
	 */
	public UiVisualizer(UiVisualizer parent, Execution exec) {
		_exec = exec;
		_1stec = parent._1stec;
		_1stau = parent._1stau;
		_owners = null;
	}

	//-- Visualizer --//
	public final Execution getExecution() {
		return _exec;
	}
	public final boolean isEverAsyncUpdate() {
		return _1stau;
	}
	public final boolean addToFirstAsyncUpdate(List responses) {
		if (!_1stau) return false;

		if (D.ON && log.finerable()) log.finer("Add to 1st au: "+responses);
		for (Iterator it = responses.iterator(); it.hasNext();)
			_1stec.addResponse(null, (AuResponse)it.next());
		return true;
	}

	//-- update/redraw --//

	/** Invalidates the whole page.
	 */
	public void addInvalidate(Page page) {
		if (!_exec.isAsyncUpdate(page))
			return; //nothing to do

		if (_pgInvalid == null)
			_pgInvalid = new LinkedHashSet(3);
		_pgInvalid.add(page);
	}
	/** Adds an invalidated component. Once invalidated, all invocations
	 * to {@link #addSmartUpdate} are ignored in this execution.
	 *
	 * @param range either {@link Component#INNER} or {@link Component#OUTER}
	 */
	public void addInvalidate(Component comp, Range range) {
		if (!_exec.isAsyncUpdate(comp.getPage()))
			return; //nothing to do

		if (range == null) throw new NullPointerException();
		final Object old = _invalidated.put(comp, range);
		if (old == Component.OUTER && range == Component.INNER)
			_invalidated.put(comp, old); //restore
		if (range == Component.OUTER)
			_smartUpdated.remove(comp);
	}
	/** Smart updates a component's attribute.
	 * Meaningful only if {@link #addInvalidate(Component, Component.Range)} is not called in this
	 * execution
	 */
	public void addSmartUpdate(Component comp, String attr, String value) {
		if (!_exec.isAsyncUpdate(comp.getPage()) || _invalidated.containsKey(comp))
			return; //nothing to do

		Map respmap = (Map)_smartUpdated.get(comp);
		if (respmap == null)
			_smartUpdated.put(comp, respmap = new LinkedHashMap());
		respmap.remove(attr); //so zk_init will be the last to execute
		respmap.put(attr, value);
	}
	/** Called to update (redraw) a component, when a component is moved.
	 * If a component's page or parent is changed, this method need to be
	 * called only once for the top one.
	 *
	 * @param newAttached whether the component is added to a page
	 * first time.
	 */
	public void addMoved(Component comp, boolean newAttached) {
		if (newAttached && !_moved.contains(comp)) {
			_attached.add(comp);
		} else {
			_moved.add(comp);
			_attached.remove(comp);
		}
	}
	/** Called before changing the component's UUID.
	 *
	 * @param addOnlyMoved if true, it is added only if it was moved
	 * before (see {@link #addMoved}).
	 */
	public void addUuidChanged(Component comp, boolean addOnlyMoved) {
		if ((!addOnlyMoved || _moved.contains(comp))
		&& (_idChgd == null || !_idChgd.containsKey(comp))) {
			if (_idChgd == null) _idChgd = new LinkedHashMap();
			_idChgd.put(comp, comp.getUuid());
		}
	}

	/** Adds a response directly (which will be returned when
	 * {@link #getResponses} is called).
	 *
	 * <p>If the response is component-dependent, {@link AuResponse#getDepends}
	 * must return a component. And, if the component is removed, the response
	 * is removed, too.
	 *
	 * @param key could be anything. The second invocation of this method
	 * in the same execution with the same key will override the previous one.
	 */
	public void addResponse(String key, AuResponse response) {
		if (response == null)
			throw new NullPointerException();

		final TimedResponse respInfo = new TimedResponse(++_resptime, response);
		if (_responses == null)
			_responses = new HashMap();

		final Object depends = response.getDepends(); //Page or Component
		Map respmap = (Map)_responses.get(depends);
		if (respmap == null)
			_responses.put(depends, respmap = new LinkedHashMap());

		if (key != null) {
			respmap.put(key, respInfo);
		} else {
			List resps = (List)respmap.get(null);
			if (resps == null)
				respmap.put(null, resps = new LinkedList());
			resps.add(respInfo); //don't override
		}
	}

	/** Prepares {@link #_pgRemoved} to contain set of pages that will
	 * be removed.
	 */
	private void checkPageRemoved() {
		//1. scan once
		final Desktop desktop = _exec.getDesktop();
		Set pages = null;
		for (Iterator it = desktop.getPages().iterator(); it.hasNext();) {
			final Page page = (Page)it.next();
			final Component owner = ((PageCtrl)page).getOwner();
			if (owner != null) { //included
				final Page ownerPage = owner.getPage();
				if (ownerPage == null //detached
				|| (_pgInvalid != null && _pgInvalid.contains(ownerPage))
				|| isAncestor(_invalidated.keySet(), owner)
				|| isAncestor(_moved, owner)
				/*|| isAncestor(_attached, owner)*/) {
					addPageRemoved(page);
				} else {
					if (pages == null) pages = new HashSet();
					pages.add(page);
				}
			}
		}
		if (_pgRemoved == null || pages == null) return;
			//done if no page is removed or no more included page

		//2. if a page is ever removed, it might cause chain effect
		//so we have to loop until nothing changed
		boolean pgRemovedFound;
		do {
			pgRemovedFound = false;
			for (Iterator it = pages.iterator(); it.hasNext();) {
				final Page page = (Page)it.next();
				final Component owner = ((PageCtrl)page).getOwner();
				if (_pgRemoved.contains(owner.getPage())) { 
					it.remove();
					addPageRemoved(page);
					pgRemovedFound = true;
				}
			}
		} while (pgRemovedFound); //loop due to chain effect
	}
	private void addPageRemoved(Page page) {
		if (_pgRemoved == null) _pgRemoved = new HashSet();
		_pgRemoved.add(page);
		if (_pgInvalid != null) _pgInvalid.remove(page);
		if (D.ON && log.debugable()) log.debug("Page removed: "+page);
	}
	/** Clears components if it belongs to invalidated or removed page. */
	private void clearForInvalidPage(Collection coll) {
		for (Iterator it = coll.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			final Page page = comp.getPage();
			if (page != null
			&& ((_pgRemoved != null && _pgRemoved.contains(page))
			||  (_pgInvalid != null && _pgInvalid.contains(page))))
				it.remove();
		}
	}
	/** Returns whether any component in coll is an ancestor of comp. */
	private boolean isAncestor(Collection coll, Component comp) {
		for (Iterator it = coll.iterator(); it.hasNext();)
			if (Components.isAncestor((Component)it.next(), comp))
				return true;
		return false;
	}

	/** Returns a list of {@link AuResponse} according to what components
	 * are invalidated and attached.
	 */
	public List getResponses() throws IOException {
		if (D.ON && log.debugable())
			log.debug("ei: "+this+"\nInvalidated: "+_invalidated+"\nSmart Upd: "+_smartUpdated
				+"\nAttached: "+_attached+"\nMoved:"+_moved+"\nResponses:"+_responses+"\npgInvalid: "+_pgInvalid
				+"\nUuidChanged: "+_idChgd);

		//0. prepare removed pages and optimize for invalidate or removed pages
		checkPageRemoved(); //maintain _pgRemoved for pages being removed

		if (_pgInvalid != null && _pgInvalid.isEmpty()) _pgInvalid = null;
		if (_pgRemoved != null && _pgRemoved.isEmpty()) _pgRemoved = null;
		if (_pgInvalid != null || _pgRemoved != null) {
			clearForInvalidPage(_invalidated.keySet());
			clearForInvalidPage(_attached);
			clearForInvalidPage(_moved);
			clearForInvalidPage(_smartUpdated.keySet());
			if (_idChgd != null) clearForInvalidPage(_idChgd.keySet());
		}

		//0a. remove pages. Note: we don't need to generate rm, becausee they
		//are included pages.
		if (_pgRemoved != null) {
			final DesktopCtrl dtctl = (DesktopCtrl)_exec.getDesktop();
			for (final Iterator it = _pgRemoved.iterator(); it.hasNext();)
				dtctl.removePage((Page)it.next());
		}

		//0b. generate response for invalidated pages
		final List responses = new LinkedList();
		if (_pgInvalid != null) {
			for (final Iterator it = _pgInvalid.iterator(); it.hasNext();) {
				final Page page = (Page)it.next();
				responses.add(
					new AuReplaceInner(page, redraw(page, Component.INNER)));
			}
		}

		//1. Remove components who is moved and its UUID is changed
		if (_idChgd != null) {
			for (Iterator it = _idChgd.values().iterator(); it.hasNext();)
				responses.add(new AuRemove((String)it.next()));
			_idChgd = null; //just in case
		}

		//2. Removes dead components
		//2a. Retrieves components to remove from the client
		final Set removed = new LinkedHashSet();
		for (Iterator it = _moved.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			if (comp.getPage() == null) {
				it.remove();
				removed.add(comp);
			}
		}
		if (D.ON && log.finerable()) log.finer("Removed: "+removed);

		//The reason to remove first: some insertion might fail if the old
		//componetns are not removed yet
		//Also, we have to remove both parent and child because, at
		//the client, they might not be parent-child relationship
		for (Iterator it = removed.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			_invalidated.remove(comp);
			_smartUpdated.remove(comp);
			if (_responses != null) _responses.remove(comp);

			responses.add(new AuRemove(comp));
				//Note: it is too late to handle isTransparent here
				//because it is detached and we don't know it is ex-parent
		}
		removed.clear(); //just in case

		//3. remove moved components first (because setParent use it)
		//Note: it has to be done before removing redudant, because
		//its new parent might be invalidated
		for (Iterator it = _moved.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			//comp in _moved might be created by execNewPage or by update
			//We have to remove those caused by update since setParent
			//implies remove-and-add
			if (_exec.isAsyncUpdate(comp.getPage()))
				responses.add(new AuRemove(comp));
				//Note: it is too late to handle isTransparent here
				//because it is detached and we don't know it is ex-parent
		}

		//4. reduntant: invalidate is parent of attached; vice-versa
		//   reduntant: invalidate or create is parent of smartUpdate
		_attached.addAll(_moved); _moved = null;
			//_moved is a kind of attached except we remove them first
		removeRedundant(_invalidated.keySet());
		removeRedundant(_attached);
		removeRedundant(responses, _invalidated, _smartUpdated, _attached);
			//it also handle isTransparent!!

		if (log.finerable())
			log.finer("After removing redudant: invalidated: "+_invalidated
			+"\nAttached: "+_attached+"\nSmartUpd:"+_smartUpdated);

		//5. generate replace for invalidated
		for (Iterator it = _invalidated.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final Component comp = (Component)me.getKey();
			final Range range = (Range)me.getValue();
			final String content = redraw(comp, range);
			if (range == Component.INNER)
				responses.add(new AuReplaceInner(comp, content));
			else
				responses.add(new AuReplace(comp, content));
		}

		//6. add attached components (including setParent)
		//Due to cyclic references, we have to process all siblings
		//at the same time
		final List desktops = new LinkedList();
		final Component[] attached = (Component[])
			_attached.toArray(new Component[_attached.size()]);
		for (int j = 0; j < attached.length; ++j) {
			final Component comp = attached[j];
			//Note: attached comp might change from another page to
			//the one being created. In this case, no need to add
			if (comp != null && _exec.isAsyncUpdate(comp.getPage())) {
				assert D.OFF || !comp.isTransparent(): "not resolved?" +comp;

				final Component parent = getNonTransparentParent(comp);
				final Set newsibs = new LinkedHashSet();
				newsibs.add(comp);
				desktops.add(newsibs);

				for (int k = j + 1; k < attached.length; ++k) {
					final Component ck = attached[k];
					if (ck != null && getNonTransparentParent(ck) == parent) {
						newsibs.add(ck);
						attached[k] = null;
					}
				}
			}
		}
		for (Iterator it = desktops.iterator(); it.hasNext();) {
			final Set newsibs = (Set)it.next();
			addResponsesForCreatedPerSiblings(responses, newsibs);
		}

		//7. execute smart updates		
		for (Iterator it = _smartUpdated.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final Component comp = (Component)me.getKey();
			final Map attrs = (Map)me.getValue();
			for (Iterator it2 = attrs.entrySet().iterator(); it2.hasNext();) {
				final Map.Entry me2 = (Map.Entry)it2.next();
				final String attr = (String)me2.getKey();
				final String val = (String)me2.getValue();
				if (val != null)
					responses.add(new AuSetAttribute(comp, attr, val));
				else
					responses.add(new AuRemoveAttribute(comp, attr));
			}
		}

		//just in case
		_invalidated =  null;
		_smartUpdated = null;
		_attached = null;
		_pgInvalid = _pgRemoved = null;

		//8. Adds component-dependent responses
		addAddedResponses(responses); //process _responses

		if (D.ON && log.debugable()) log.debug("Return responses: "+responses);
		return responses;
	}
	/** Adds responses from _responses. */
	private void addAddedResponses(List responses) {
		if (_responses == null)
			return; //nothing to do

		final LinkedList respInfos = new LinkedList(); //a list of TimedResponse
		for (Iterator it = _responses.values().iterator(); it.hasNext();) {
			final Map resps = (Map)it.next();
			final List keyless = (List)resps.remove(null); //key == null
			respInfos.addAll(resps.values());
			if (keyless != null)
				respInfos.addAll(keyless);
		}

		final TimedResponse[] ris = (TimedResponse[])
			respInfos.toArray(new TimedResponse[respInfos.size()]);
		Arrays.sort(ris, new Comparator() {
			public int compare(Object a, Object b) {
				final int x = ((TimedResponse)a).time, y = ((TimedResponse)b).time;
				return x > y ? 1: x == y ? 0: -1;
			}
		});

		for (int j = 0; j < ris.length; ++j)
			responses.add(ris[j].response);
		_responses = null;
	}

	/** Adds responses for a set of siblings which is new attached (or
	 * parent is changed).
	 */
	private static
	void addResponsesForCreatedPerSiblings(List responses, Set newsibs)
	throws IOException {
		final Component parent;
		final Page page;
		{
			final Component comp = (Component)newsibs.iterator().next();
			parent = getNonTransparentParent(comp);
			page = comp.getPage();
		}
		final Collection sibs;
		if (parent != null) {
			sibs = resolveTransparent(parent.getChildren());
		} else {
			sibs = page.getRoots();
		}
		if (D.ON && log.finerable()) log.finer("All sibs: "+sibs+" newsibs: "+newsibs);

		/* Algorithm:
	1. Locate a sibling, say <a>, that already exists.
	2. Then, use AuInsertBefore for all sibling before <a>,
		and AuInsertAfter for all after anchor.
	3. If anchor is not found, use AuAppendChild for the first
		and INSERT_AFTER for the rest
		*/
		final List before = new LinkedList();
		Component anchor = null;
		for (Iterator it = sibs.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			if (anchor != null) {
				if (newsibs.remove(comp)) {
					responses.add(new AuInsertAfter(anchor, drawNew(comp)));
					if (newsibs.isEmpty())
						return; //done (all newsibs are processed)
					anchor = comp;
				} else {
					anchor = comp;
				}
			} else if (newsibs.remove(comp)) {
				before.add(comp);	
			} else {
				//Generate before in the reverse order and INSERT_BEFORE
				anchor = comp;
				for (ListIterator i2 = before.listIterator(before.size());
				i2.hasPrevious();) {
					final Component c = (Component)i2.previous();
					responses.add(new AuInsertBefore(anchor, drawNew(c)));
					anchor = c;
				}
				if (newsibs.isEmpty())
					return; //done (all newsibs are processed)
				anchor = comp;
			}
		}
		assert D.OFF || (anchor == null && newsibs.isEmpty()): "anchor="+anchor+" newsibs="+newsibs+" sibs="+sibs;


		//all siblings are changed (and none of them is processed)
		final Iterator it = before.iterator();
		anchor = (Component)it.next();
		responses.add(
			parent != null ?
			new AuAppendChild(parent, drawNew(anchor)):
			new AuAppendChild(page, drawNew(anchor)));

		while (it.hasNext()) {
			final Component comp = (Component)it.next();
			responses.add(new AuInsertAfter(anchor, drawNew(comp)));
			anchor = comp;
		}
	}
	/** Removes redundant components (i.e., an descendant of another).
	 */
	private static void removeRedundant(Set comps) {
		rudLoop:
		for (Iterator j = comps.iterator(); j.hasNext();) {
			final Component cj = (Component)j.next();
			for (Iterator k = comps.iterator(); k.hasNext();) {
				final Component ck = (Component)k.next();
				if (ck != cj && Components.isAncestor(ck, cj)) {
					j.remove();
					continue rudLoop;
				}
			}
		}
	}
	/** Removes redundant components (i.e., an descendant of another).
	 */
	private static
	void removeRedundant(List responses, Map invalidated,
	Map smartUpdated, Set attached) {
		invLoop:
		for (Iterator j = invalidated.keySet().iterator(); j.hasNext();) {
			final Component cj = (Component)j.next();

			for (Iterator k = attached.iterator(); k.hasNext();) {
				final Component ck = (Component)k.next();
				if (Components.isAncestor(ck, cj)) { //includes ck == cj
					j.remove();
					continue invLoop;
				} else if (Components.isAncestor(cj, ck)) {
					k.remove();
				}
			}
		}
		suLoop:
		for (Iterator j = smartUpdated.keySet().iterator(); j.hasNext();) {
			final Component cj = (Component)j.next();

			if (cj.isTransparent()) {
				j.remove();
				continue;
			}

			for (Iterator k = invalidated.entrySet().iterator(); k.hasNext();) {
				final Map.Entry me = (Map.Entry)k.next();
				final Component ck = (Component)me.getKey();

				//INNER doesn't removes smartUpdate
				if ((ck != cj || Component.OUTER == me.getValue())
				&& Components.isAncestor(ck, cj)) {
					j.remove();
					continue suLoop;
				}
			}
			for (Iterator k = attached.iterator(); k.hasNext();) {
				final Component ck = (Component)k.next();
				if (Components.isAncestor(ck, cj)) {
					j.remove();
					continue suLoop;
				}
			}
		}

		//resolves transprent components.
		List comps = null;
		for (Iterator it = invalidated.keySet().iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			if (comp.isTransparent()) {
				if (comps == null) comps = new LinkedList();
				comps.add(comp);
				it.remove();
				responses.add(new AuRemove(comp));
					//yes, we have to remove it because it might change from
					//non-transparent to transparent (and there is counterpart
					//in the client to remove)
			}
		}
		if (comps != null) {
			resolveTransparent(comps, invalidated);
			comps = null;
		}
		for (Iterator it = attached.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			if (comp.isTransparent()) {
				if (comps == null) comps = new LinkedList();
				comps.add(comp);
				it.remove();
				responses.add(new AuRemove(comp));
					//yes, we have to remove it because it might change from
					//non-transparent to transparent (and there is counterpart
					//in the client to remove)
			}
		}
		if (comps != null) resolveTransparent(comps, attached);
	}
	/** Adds comps to invalidated, and resolves comps by replacing transparent
	 * components with their non-transparent children.
	 */
	private static void resolveTransparent(List comps, Map invalidated) {
		if (comps.isEmpty()) return;
		for (Iterator it = comps.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			if (comp.isTransparent()) {
				resolveTransparent(comp.getChildren(), invalidated); //recursive
			} else {
				invalidated.put(comp, Component.OUTER);
			}
		} 
	}
	/** Copies comps to result, and resolves comps by replacing transparent
	 * components with their non-transparent children.
	 */
	private static void resolveTransparent(List comps, Collection result) {
		if (comps.isEmpty()) return;
		for (Iterator it = comps.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			if (comp.isTransparent()) {
				resolveTransparent(comp.getChildren(), result); //recursive
			} else {
				result.add(comp);
			}
		} 
	}
	/** Clones comps and resolves comps by replacing transparent
	 * components with their non-transparent children (right at the same place).
	 */
	private static List resolveTransparent(List comps) {
		if (comps.isEmpty()) return comps;

		final List cloned = new LinkedList(comps);
		for (ListIterator it = cloned.listIterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			if (comp.isTransparent()) {
				it.remove();
				for (Iterator it2 = resolveTransparent(comp.getChildren())
				.iterator(); it2.hasNext();)
					it.add(it2.next());
			}
		}
		return cloned; 
	}
	private static Component getNonTransparentParent(Component comp) {
		for (;;) {
			comp = comp.getParent();
			if (comp == null || !comp.isTransparent())
				return comp;
		}
	}

	/** Draws a new attached component into a string.
	 */
	private static String drawNew(Component comp)
	throws IOException {
		final StringWriter out = new StringWriter(1024*8);
		comp.redraw(out);

		final StringBuffer buf = out.getBuffer();
		final Component parent = comp.getParent();
		if (parent != null)
			parent.onDrawNewChild(comp, buf);
		return buf.toString();
	}
	/** Redraw the specified component into a string.
	 */
	private static String redraw(Component comp, Range range)
	throws IOException {
		final StringWriter out = new StringWriter(1024*8);
		comp.redraw(out);

		final StringBuffer buf = out.getBuffer();
		return range == Component.INNER ? retrieveInner(buf): buf.toString();
	}
	/** Redraws the whole page. */
	private static String redraw(Page page, Range range)
	throws IOException {
		final StringWriter out = new StringWriter(1024*8);
		((PageCtrl)page).redraw(null, out);

		final StringBuffer buf = out.getBuffer();
		return range == Component.INNER ? retrieveInner(buf): buf.toString();
	}
	/** Retrieves the inner content. */
	private static String retrieveInner(StringBuffer sb){
		final int len = sb.length();
		for (int j = 0; j < len; ++j)
			if (sb.charAt(j) == '>') {
				for (int k = len; --k > j;)
					if (sb.charAt(k) == '<')
						return sb.substring(j + 1, k);
			}

		if (D.ON) log.warning("Not containing any tag: "+sb);
		return sb.toString();
	}

	/** Called before a component redraws itself if the component might
	 * include another page.
	 */
	public void pushOwner(Component comp) {
		_1stec._owners.add(0, comp);
	}
	/** Called after a component redraws itself if it ever calls
	 * {@link #pushOwner}.
	 */
	public void popOwner() {
		_1stec._owners.remove(0);
	}
	/** Sets the owner of the specified page. 
	 * The owner is the top of the stack pushed by {@link #pushOwner}.
	 */
	public void setOwner(Page page) {
		if (_1stec._owners.isEmpty()) {
			log.warning("No owner available for "+page);
		} else {
			final Component owner = (Component)_1stec._owners.get(0);
			((PageCtrl)page).setOwner(owner);
			if (D.ON && log.finerable()) log.finer("Set owner of "+page+" to "+owner);
		}
	}

	/** Used to time a response that addAddedResponses could generate
	 * responses in the correct order.
	 */
	private static class TimedResponse {
		private final int time;
		private final AuResponse response;
		private TimedResponse(int time, AuResponse response) {
			this.time = time;
			this.response = response;
		}
		public String toString() {
			return time + ":" + response;
		}
	}


	/** Sets the reason to aborting.
	/** if not null, it means the current execution is aborting
	 * and the specified argument is the reason to aborting.
	 * Its interpretation depends on {@link com.potix.zk.ui.sys.UiEngine}.
	 *
	 * <p>Note: if setAbortingReason is ever set with non-null, you
	 * CANNOT set it back to null.
	 *
	 * <p>The aborting flag means no more processing, i.e., dropping pending
	 * requests, events, and rendering.
	 *
	 * <p>After call this method, you shall not keep processing the page
	 * because the rendering is dropped and the client is out-of-sync
	 * with the server.
	 *
	 * <p>This method doesn't really abort pending events and requests.
	 * It just set a flag, {@link #getAbortingReason}, and it is
	 * {@link com.potix.zk.ui.sys.UiEngine}'s job to detect this flag
	 * and handling it properly.
	 */
	public void setAbortingReason(AbortingReason reason) {
		if (_aborting != null && reason == null)
			throw new IllegalStateException("Aborting reason is set and you cannot clear it");
				//Reason: some event or request might be skipped
				//so clearing it might cause unexpected results
		_aborting = reason;
	}
	/** Returns the reason to aborting, or null if no aborting at all.
	 * 
	 * @see #setAbortingReason
	 */
	public AbortingReason getAbortingReason() {
		return _aborting;
	}
	/** Returns whether it is aborting.
	 * <p>The execution is aborting if {@link #getAbortingReason} returns
	 * not null and the returned reason's {@link AbortingReason#isAborting}
	 * is true.
	 */
	public boolean isAborting() {
		return _aborting != null && _aborting.isAborting();
	}
}
