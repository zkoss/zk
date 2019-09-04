/* UiVisualizer.java

	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 10:57:48     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.out.AuAppendChild;
import org.zkoss.zk.au.out.AuInsertAfter;
import org.zkoss.zk.au.out.AuInsertBefore;
import org.zkoss.zk.au.out.AuOuter;
import org.zkoss.zk.au.out.AuOuterPartial;
import org.zkoss.zk.au.out.AuRemove;
import org.zkoss.zk.au.out.AuSetAttribute;
import org.zkoss.zk.au.out.AuUuid;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.Includer;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.sys.AbortingReason;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.StubComponent;
import org.zkoss.zk.ui.sys.Visualizer;

/**
 * An implementation of {@link Visualizer} that works with
 * {@link UiEngineImpl}.
 *
 * @author tomyeh
 */
/*package*/ class UiVisualizer implements Visualizer {
	//	private static final Logger log = LoggerFactory.getLogger(UiVisualizer.class);

	/** The parent exec info. */
	private final UiVisualizer _parent;

	/** The associated execution. */
	private final Execution _exec;
	/** A set of invalidated pages. */
	private Set<Page> _pgInvalid;
	/** A set of removed pages. */
	private Set<Page> _pgRemoved;
	/** A map of invalidated components (Component) and subids (String). */
	private final Map<Component, String> _invalidated = new LinkedHashMap<Component, String>(32);
	/** A map of smart updates (Component comp, Map(String name, TimedValue(comp,name,value))). */
	private final Map<Component, Map<String, TimedValue>> _smartUpdated = new HashMap<Component, Map<String, TimedValue>>(
			64); //we use TimedValue for better sequence control
	/** A set of new attached components. */
	private final Set<Component> _attached = new LinkedHashSet<Component>(32);
	/** A set of moved components (parent changed or page changed). */
	private final Set<Component> _moved = new LinkedHashSet<Component>(32);
	/** A set of components whose client-update is disabled. */
	private Set<Component> _updDisabled;
	/** A map of detached components (detached only -- not moved thereafter).
	 * (comp, comp's parent).
	 */
	private final Map<Component, Component> _detached = new LinkedHashMap<Component, Component>(32);
	/** A map of UUID of detached or moved components.
	 * It is important since UUID might be re-used
	 */
	private final Map<Component, String> _uuids = new HashMap<Component, String>(32);
	/** A map of components whose UUID is changed (Component, UUID). */
	private Map<Component, String> _idChgd;
	/** A map of responses being added(Component/Page, ResponseInfo)).
	 */
	private Map<Object, ResponseInfo> _responses;
	/** the component that is including a new page (and then
	 * become the owner of the new page, if any).
	 */
	private Component _owner;
	/** Time stamp for smart update and responses (see {@link TimedValue}). */
	private int _timed;
	/** if not null, it means the current executing is aborting
	 * and the content is reason to aborting. Its interpretation depends
	 * on {@link org.zkoss.zk.ui.sys.UiEngine}.
	 */
	private AbortingReason _aborting;
	/** The counter used for smartUpdate(...append). */
	private int _cntMultSU;
	/** Whether the first execution is for async-update. */
	private final boolean _1stau;
	/** Whether it is in recovering. */
	private final boolean _recovering;
	/** Whether it is ending, i.e., no further update is allowed. */
	private boolean _ending;
	/** Whether it is disabled, i.e., ignore any updates to the client.
	 */
	private boolean _disabled;

	/**
	 * Creates a root execution (without parent).
	 * In other words, it must be the first execution in the current request.
	 *
	 * @param asyncUpdate whether this execution is for async-update
	 * @param recovering whether this execution is in recovering,
	 * i.e., caused by {@link org.zkoss.zk.ui.sys.FailoverManager#recover}.
	 */
	public UiVisualizer(Execution exec, boolean asyncUpdate, boolean recovering) {
		_exec = exec;
		_parent = null;
		_1stau = asyncUpdate;
		_recovering = recovering;
	}

	/**
	 * Creates the following execution.
	 * The first execution must use {@link #UiVisualizer(Execution, boolean, boolean)}
	 */
	public UiVisualizer(UiVisualizer parent, Execution exec) {
		_exec = exec;
		_parent = parent;
		_1stau = parent._1stau;
		_recovering = false;
	}

	//-- Visualizer --//
	public final Execution getExecution() {
		return _exec;
	}

	public final boolean isEverAsyncUpdate() {
		return _1stau;
	}

	public final boolean addToFirstAsyncUpdate(List<AuResponse> responses) {
		if (!_1stau)
			return false;

		//		if (log.finerable()) log.finer("Add to 1st au: "+responses);
		UiVisualizer root = getRoot();
		for (AuResponse response : responses)
			root.addResponse(null, response);
		return true;
	}

	private UiVisualizer getRoot() {
		for (UiVisualizer uv = this;;) {
			if (uv._parent == null)
				return uv;
			uv = uv._parent;
		}
	}

	public boolean isRecovering() {
		return _recovering;
	}

	public void disable() {
		_disabled = true;
	}

	//-- update/redraw --//
	/** Returns if this component needs to be redrawn.
	 * <p>Note:
	 * <ol>
	 * <li>It always returns true if the current execution is not an
	 * asynchronous update.</li>
	 * <li>If its parent is invalidated, this component will be redrawn
	 * too, but this method returns false since {@link #addInvalidate(Component)}
	 * was not called against this component.</li>
	 * </ol>
	 * @since 3.0.5
	 */
	public boolean isInvalidated(Component comp) {
		return !_exec.isAsyncUpdate(comp.getPage()) || _invalidated.containsKey(comp) || _attached.contains(comp)
				|| _moved.contains(comp) || _detached.containsKey(comp);
		//No need to check page, recovering... since it won't be
		//part of _invalidated if so.
	}

	/** Invalidates the whole page.
	 */
	public void addInvalidate(Page page) {
		if (_recovering || _disabled || page == null || page instanceof VolatilePage || !_exec.isAsyncUpdate(page))
			return; //nothing to do

		//relative fix for BUG ZK-1464
		if (_ending && _pgRemoved != null && _pgRemoved.contains(page))
			return;

		if (_pgInvalid == null)
			_pgInvalid = new LinkedHashSet<Page>(4);
		_pgInvalid.add(page);
	}

	/** Adds an invalidated component. Once invalidated, all invocations
	 * to {@link #addSmartUpdate} are ignored in this execution.
	 */
	public void addInvalidate(Component comp) {
		addInvalidate(comp, "");
	}

	/** Adds an invalidated component. Once invalidated, all invocations
	 * to {@link #addSmartUpdate} are ignored in this execution.
	 */
	public void addInvalidate(Component comp, String subId) {
		final Page page = comp.getPage();
		if (_recovering || _disabled || page == null || page instanceof VolatilePage || !_exec.isAsyncUpdate(page)
				|| isCUDisabled(comp))
			return; //nothing to do

		//relative fix BUG ZK-1464
		if (_ending && _pgRemoved != null && _pgRemoved.contains(page))
			return;

		if (_ending)
			throw new IllegalStateException("UI can't be modified in the rendering phase");

		checkDesktop(comp);

		if (_invalidated.put(comp, subId) == null)
			_smartUpdated.remove(comp);
	}

	/** Ensure the use of component is correct. */
	private void checkDesktop(Component comp) {
		final Desktop dt = comp.getDesktop();
		if (dt != null && dt != _exec.getDesktop())
			throw new IllegalStateException(
					"Access denied: component, " + comp + ", belongs to another desktop: " + dt);
	}

	/** Smart updates a component's attribute.
	 * Meaningful only if {@link #addInvalidate(Component)} is not called in this
	 * execution
	 * @param value the value.
	 * @since 5.0.2
	 */
	public final void addSmartUpdate(Component comp, String attr, Object value, boolean append) {
		addSmartUpdate0(comp, attr, value, append, 0);
	}

	/**
	 * Adds a smart update that will be executed at the given priority.
	 * The higher priority, the earlier the update is executed.
	 * If {@link #addSmartUpdate(Component, String, Object, boolean)}
	 * is invoked, the priority is assumed to 0.
	 * @since 6.0.0
	 */
	public void addSmartUpdate(Component comp, String attr, Object value, int priority) {
		addSmartUpdate0(comp, attr, value, false, priority);
	}

	private void addSmartUpdate0(Component comp, String attr, Object value, boolean append, int priority) {
		if (comp == null)
			throw new IllegalArgumentException();
		//main fix for BUG ZK-1464
		if (_ending && (comp.getPage() == null || (_pgRemoved != null && _pgRemoved.contains(comp.getPage())))) {
			return;
		}

		final Map<String, TimedValue> respmap = getAttrRespMap(comp);
		if (respmap != null)
			respmap.put(append ? attr + ":" + _cntMultSU++ : attr,
					new TimedValue(_timed++, comp, attr, value, priority));
	}

	/** Sets whether to disable the update of the client widget.
	 * By default, if a component is attached to a page, modifications that
	 * change the visual representation will be sent to the client to
	 * ensure the consistency.
	 *
	 * @return whether it has been disabled before this invocation, i.e.,
	 * the previous disable status
	 * @since 3.6.2
	 */
	public boolean disableClientUpdate(Component comp, boolean disable) {
		if (disable) {
			if (_updDisabled == null)
				_updDisabled = new HashSet<Component>(4);
			return !_updDisabled.add(comp);
		}

		final boolean ret = _updDisabled != null && _updDisabled.remove(comp);
		if (ret && _updDisabled.isEmpty())
			_updDisabled = null;
		return ret;
	}

	private final boolean isCUDisabled(Component comp) {
		if (_updDisabled != null) {
			//no need to check comp.getPage() since it was checked before calling
			for (; comp != null; comp = comp.getParent())
				if (_updDisabled.contains(comp))
					return true;
		}
		return false;
	}

	/** Returns the response map for the specified attribute, or null if
	 * nothing to do.
	 */
	private Map<String, TimedValue> getAttrRespMap(Component comp) {
		final Page page = comp.getPage();
		if (_recovering || _disabled || page == null || page instanceof VolatilePage || !_exec.isAsyncUpdate(page)
				|| _invalidated.containsKey(comp) || isCUDisabled(comp))
			return null; //nothing to do
		if (_ending)
			throw new IllegalStateException("UI can't be modified in the rendering phase");

		checkDesktop(comp);

		Map<String, TimedValue> respmap = _smartUpdated.get(comp);
		if (respmap == null)
			_smartUpdated.put(comp, respmap = new HashMap<String, TimedValue>());
		return respmap;
	}

	/** Called to update (redraw) a component, when a component is moved.
	 * If a component's page or parent is changed, this method need to be
	 * called only once for the top one.
	 *
	 * @param oldparent the parent before moved
	 * @param oldpg the page before moved
	 * @param newpg the page after moved
	 */
	public void addMoved(Component comp, Component oldparent, Page oldpg, Page newpg) {
		if (_recovering || _disabled || (newpg == null && oldpg == null)
				|| (newpg == null && (oldpg instanceof VolatilePage || !_exec.isAsyncUpdate(oldpg))) //detach from loading pg
				|| (oldpg == null && (newpg instanceof VolatilePage || !_exec.isAsyncUpdate(newpg))) //attach to loading pg
				|| isCUDisabled(comp) || (oldparent != null && isCUDisabled(oldparent)))
			return; //to avoid redundant AuRemove
		if (_ending)
			throw new IllegalStateException("UI can't be modified in the rendering phase");

		snapshotUuid(comp);

		if (oldpg == null && !_moved.contains(comp) && !_detached.containsKey(comp)) { //new attached
			_attached.add(comp);
			//note: we cannot examine _exec.isAsyncUpdate here because
			//comp.getPage might be ready when this method is called
		} else if (newpg == null && !_moved.contains(comp)) {
			if (!_attached.remove(comp))
				_detached.put(comp, oldparent); //new detached
			//ignore if attach and then detach
		} else {
			_moved.add(comp);
			_attached.remove(comp);
			_detached.remove(comp);
		}
	}

	/** Called before changing the component's UUID.
	 * @since 5.0.3
	 */
	public void addUuidChanged(Component comp) {
		//Algorithm of handling UUID change:
		//1. If it belongs a new page, nothing to do (since there is no client widget)
		//2. If not, it generates AuUuid of all modified UUID before generating
		//any other responses such that client's UUID will be corrected first

		if (_exec.isAsyncUpdate(comp.getPage()) //only if not belong to a new page
				&& (_idChgd == null || !_idChgd.containsKey(comp)) && !isCUDisabled(comp)) {
			if (_idChgd == null)
				_idChgd = new LinkedHashMap<Component, String>(23);
			_idChgd.put(comp, comp.getUuid());
		}
	}

	/** Adds a response directly by using {@link AuResponse#getOverrideKey}
	 * as the override key.
	 * In other words, it is the same as <code>addResponse(resposne.getOverrideKey(), response)</code>
	 * <p>If the response is component-dependent, {@link AuResponse#getDepends}
	 * must return a component. And, if the component is removed, the response
	 * is removed, too.
	 * @since 5.0.2
	 */
	public void addResponse(AuResponse response) {
		addResponse(response.getOverrideKey(), response);
	}

	/** Adds a response directly (which will be returned when
	 * {@link #getResponses} is called).
	 *
	 * <p>If the response is component-dependent, {@link AuResponse#getDepends}
	 * must return a component. And, if the component is removed, the response
	 * is removed, too.
	 *
	 * @param key could be anything. If null, the response is appended.
	 * If not null, the second invocation of this method
	 * in the same execution with the same key and the same depends ({@link AuResponse#getDepends})
	 * will override the previous one.
	 * @see #addResponse(AuResponse)
	 */
	public void addResponse(String key, AuResponse response) {
		addResponse(key, response, 0);
	}

	/** Adds a response directly with the given priority.
	 * The higher priority, the earlier the update is executed.
	 * The priority of {@link #addResponse(String, AuResponse)}
	 *  and {@link #addResponse(AuResponse)} is assumed to be 0.
	 * @since 6.0.1
	 */
	public void addResponse(String key, AuResponse response, int priority) {
		if (response == null)
			throw new IllegalArgumentException();

		//relative fix BUG ZK-1464
		if (_ending) {
			Object dps = response.getDepends();
			if (dps == null && _owner == null) // Bug ZK-1708: if the response is generated inside Includer, should not return
				return;
			if (dps instanceof Page && _pgRemoved != null && _pgRemoved.contains((Page) dps)) {
				return;
			}
			if (dps instanceof Component) {
				Component p = (Component) dps;
				if (p.getPage() == null || (_pgRemoved != null && _pgRemoved.contains(p.getPage()))) {
					return;
				}
			}
		}

		final Object depends = response.getDepends(); //Page or Component
		if (depends instanceof Component && isCUDisabled((Component) depends))
			return; //nothing to do

		if (_responses == null)
			_responses = new HashMap<Object, ResponseInfo>();

		ResponseInfo ri = _responses.get(depends);
		if (ri == null)
			_responses.put(depends, ri = new ResponseInfo());

		final TimedValue tval = new TimedValue(_timed++, response, priority);
		if (key != null) {
			ri.values.put(key, tval); //overwrite
		} else {
			ri.keyless.add(tval); //don't overwrite
		}
	}

	/** Process {@link Cropper} by removing cropped invalidates and so on.
	 */
	private Map<Component, Set<? extends Component>> doCrop() {
		final Map<Component, Set<? extends Component>> croppingInfos = new HashMap<Component, Set<? extends Component>>();
		crop(_attached, croppingInfos, false);
		crop(_smartUpdated.keySet(), croppingInfos, false);
		if (_responses != null)
			crop(_responses.keySet(), croppingInfos, true);
		crop(_invalidated.keySet(), croppingInfos, false);
		return croppingInfos;
	}

	/** Crop attached and moved.
	 */
	private void crop(Set coll, Map<Component, Set<? extends Component>> croppingInfos, boolean bResponse) {
		for (Iterator it = coll.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (!(o instanceof Component))
				continue;

			final Component comp = (Component) o;
			final Page page = comp.getPage();
			if (page == null || !_exec.isAsyncUpdate(page)) {
				if (!bResponse)
					it.remove(); //just in case
				continue;
			}

			for (Component p, c = comp; (p = c.getParent()) != null; c = p) {
				final Set<? extends Component> avail = getAvailableAtClient(p, croppingInfos);
				if (avail != null) {
					if (!avail.contains(c)) {
						it.remove();
						break;
					}
					p = ((Cropper) ((ComponentCtrl) p).getExtraCtrl()).getCropOwner();
					if (p == null)
						break;
				}
			}
		}
	}

	/** Returns the available children, or null if no cropping.
	 */
	private static Set<? extends Component> getAvailableAtClient(Component comp,
			Map<Component, Set<? extends Component>> croppingInfos) {
		final Object xc = ((ComponentCtrl) comp).getExtraCtrl();
		if (xc instanceof Cropper) {
			//we don't need to check isCropper first since its component's job
			//to ensure the consistency

			Set<? extends Component> crop = croppingInfos.get(comp);
			if (crop == EMPTY_CROP)
				return null;
			if (crop != null)
				return crop;

			crop = ((Cropper) xc).getAvailableAtClient();
			croppingInfos.put(comp, crop != null ? crop : EMPTY_CROP);
			return crop;
		}
		return null;
	}

	private static final Set<Component> EMPTY_CROP = new HashSet<Component>(1);

	/** Prepares {@link #_pgRemoved} to contain set of pages that will
	 * be removed.
	 */
	private void checkPageRemoved(Set<Component> removed) {
		//1. scan once
		final Desktop desktop = _exec.getDesktop();
		Set<Page> pages = null;
		for (Page page : desktop.getPages()) {
			final Component owner = ((PageCtrl) page).getOwner();
			if (owner != null) { //included
				final Page ownerPage = owner.getPage();
				if (ownerPage == null //detached
						|| (_pgInvalid != null && _pgInvalid.contains(ownerPage))
						|| isAncestor(_invalidated.keySet(), owner, true) || isAncestor(_attached, owner, true)
						|| isAncestor(removed, owner, true)) {
					addPageRemoved(page);
				} else {
					if (pages == null)
						pages = new LinkedHashSet<Page>();
					pages.add(page);
				}
			}
		}
		if (_pgRemoved == null || pages == null)
			return;
		//done if no page is removed or no more included page

		//2. if a page is ever removed, it might cause chain effect
		//so we have to loop until nothing changed
		boolean pgRemovedFound;
		do {
			pgRemovedFound = false;
			for (Iterator<Page> it = pages.iterator(); it.hasNext();) {
				final Page page = it.next();
				final Component owner = ((PageCtrl) page).getOwner();
				if (_pgRemoved.contains(owner.getPage())) {
					it.remove();
					addPageRemoved(page);
					pgRemovedFound = true;
				}
			}
		} while (pgRemovedFound); //loop due to chain effect
	}

	private void addPageRemoved(Page page) {
		if (page == null || page instanceof VolatilePage)
			return;

		if (_pgRemoved == null)
			_pgRemoved = new LinkedHashSet<Page>();
		_pgRemoved.add(page);
		if (_pgInvalid != null)
			_pgInvalid.remove(page);
		//		if (log.isDebugEnabled()) log.debug("Page removed: "+page);
	}

	/** Clears components if it belongs to invalidated or removed page. */
	private void clearInInvalidPage(Collection<Component> coll) {
		for (Iterator<Component> it = coll.iterator(); it.hasNext();) {
			final Component comp = it.next();
			final Page page = comp.getPage();
			if (page != null && ((_pgRemoved != null && _pgRemoved.contains(page))
					|| (_pgInvalid != null && _pgInvalid.contains(page))))
				it.remove();
		}
	}

	/** Returns whether any component in coll is an ancestor of comp.
	 * @param includingEquals whether to return true if a equals B
	 */
	private boolean isAncestor(Collection<Component> coll, Component comp, boolean includingEquals) {
		for (Iterator<Component> it = coll.iterator(); it.hasNext();) {
			final Component c = it.next();
			if ((includingEquals || c != comp) && Components.isAncestor(c, comp))
				return true;
		}
		return false;
	}

	/** Returns a list of {@link AuResponse} according to what components
	 * are invalidated and attached.
	 * @param renderedComps used to return the components that are rendered.
	 * It is ignored if null. If not null, it must be mutable and
	 * this method will add the topmost rendered components to this collection.
	 * @since 6.0.0
	 */
	public List<AuResponse> getResponses(Collection<Component> renderedComps) throws IOException {
		_ending = true; //no more modifying UI (invalidate/addSmartUpdate...)

		/*		if (log.finerable())
					log.finer("ei: "+this+"\nInvalidated: "+_invalidated+"\nSmart Upd: "+_smartUpdated
						+"\nAttached: "+_attached+"\nMoved:"+_moved+"\nResponses:"+_responses
						+"\npgInvalid: "+_pgInvalid	+"\nUuidChanged: "+_idChgd);
		*/
		final List<AuResponse> responses = new LinkedList<AuResponse>();

		//0. Correct the UUID at the client first
		if (_idChgd != null) {
			for (Map.Entry<Component, String> me : _idChgd.entrySet()) {
				final Component comp = me.getKey();
				if (!_attached.contains(comp))
					responses.add(new AuUuid(comp, me.getValue()));
			}
			_idChgd = null; //just in case
		}

		//1. process dead components, cropping and the removed page
		final Map<Component, Set<? extends Component>> croppingInfos;
		{
			//1a. handle _detached to remove unnecessary detach
			doDetached();
			//after call, _detached is merged to _moved

			//1b. handle _moved
			//The reason to remove first: some insertion might fail if the old
			//components are not removed yet
			//Also, we have to remove both parent and child because, at
			//the client, they might not be parent-child relationship
			Set<Component> removed = doMoved(responses);
			//after called, _moved is cleared (add to _attached if necessary)
			//And, AuRemove is generated (we have to generate AuRemove first,
			//since UUID might be reused)

			//1c. remove redundant
			removeRedundant();

			//1d. process Cropper
			croppingInfos = doCrop();

			//1d. prepare removed pages and optimize for invalidate or removed pages
			checkPageRemoved(removed); //maintain _pgRemoved for pages being removed
		}

		//2. Process removed and invalid pages
		//2a. clean up _invalidated and others belonging to invalid pages
		if (_pgInvalid != null && _pgInvalid.isEmpty())
			_pgInvalid = null;
		if (_pgRemoved != null && _pgRemoved.isEmpty())
			_pgRemoved = null;
		if (_pgInvalid != null || _pgRemoved != null) {
			clearInInvalidPage(_invalidated.keySet());
			clearInInvalidPage(_attached);
			clearInInvalidPage(_smartUpdated.keySet());
		}

		//2b. remove pages. Note: we don't need to generate rm, because they
		//are included pages.
		if (_pgRemoved != null) {
			final DesktopCtrl dtctl = (DesktopCtrl) _exec.getDesktop();
			for (Page page : _pgRemoved)
				dtctl.removePage(page);
		}

		//3. generate response for invalidated pages
		if (_pgInvalid != null) {
			for (Page page : _pgInvalid) {
				if (renderedComps != null)
					renderedComps.addAll(page.getRoots());
				responses.add(new AuOuter(page, redraw(page)));
			}
		}

		/*		if (log.finerable())
					log.finer("After removing redundant: invalidated: "+_invalidated
					+"\nAttached: "+_attached+"\nSmartUpd:"+_smartUpdated);
		*/
		//4. process special interfaces

		//5. generate replace for invalidated
		for (Map.Entry<Component, String> entry : _invalidated.entrySet()) {
			final Component comp = entry.getKey();
			final String subId = entry.getValue();
			if (renderedComps != null)
				renderedComps.add(comp);
			if (subId.isEmpty())
				responses.add(new AuOuter(comp, redraw(comp)));
			else
				responses.add(new AuOuterPartial(comp, redraw(comp), subId));
		}

		//6. add attached components (including setParent)
		//Due to cyclic references, we have to process all siblings
		//at the same time
		final List<Set<Component>> desktops = new LinkedList<Set<Component>>();
		final Component[] attached = _attached.toArray(new Component[_attached.size()]);
		for (int j = 0; j < attached.length; ++j) {
			final Component comp = attached[j];
			//Note: attached comp might change from another page to
			//the one being created. In this case, no need to add
			if (comp != null) {
				final Page page = comp.getPage();
				if (page != null && _exec.isAsyncUpdate(page)) {
					final Component parent = comp.getParent();
					final Set<Component> newsibs = new LinkedHashSet<Component>(32);
					newsibs.add(comp);
					desktops.add(newsibs);

					for (int k = j + 1; k < attached.length; ++k) {
						final Component ck = attached[k];
						if (ck != null && ck.getParent() == parent) {
							newsibs.add(ck);
							attached[k] = null;
						}
					}
				}
			}
		}

		for (Set<Component> newsibs : desktops) {
			if (renderedComps != null)
				renderedComps.addAll(newsibs);
			addResponsesForCreatedPerSiblings(responses, newsibs, croppingInfos);
		}

		//7. Adds smart updates and response at once based on their time stamp
		final List<TimedValue> tvals = new LinkedList<TimedValue>();
		for (Map<String, TimedValue> attrs : _smartUpdated.values()) {
			tvals.addAll(attrs.values());
		}
		if (_responses != null) {
			for (Map.Entry<Object, ResponseInfo> me : _responses.entrySet()) {
				final Object depends = me.getKey();
				if (depends instanceof Component) {
					final Component cd = (Component) depends;
					if (cd.getPage() == null || isCUDisabled(cd))
						continue;
				}
				ResponseInfo ri = me.getValue();
				tvals.addAll(ri.keyless);
				tvals.addAll(ri.values.values());
			}
		}
		if (!tvals.isEmpty()) {
			final TimedValue[] tvs = tvals.toArray(new TimedValue[tvals.size()]);
			Arrays.sort(tvs);
			for (int j = 0; j < tvs.length; ++j)
				responses.add(tvs[j].getResponse());
		}

		//any aborting reason
		//Note: we don't give up other responses (Bug 1647085)
		if (_aborting != null) {
			final AuResponse abtresp = _aborting.getResponse();
			if (abtresp != null)
				responses.add(abtresp); //add to the end
		}

		//free memory
		_invalidated.clear();
		_smartUpdated.clear();
		_attached.clear();
		_uuids.clear();
		_pgInvalid = _pgRemoved = null;
		_responses = null;

		//		if (log.isDebugEnabled()) log.debug("Return responses: "+responses);
		//		System.out.println("Return responses: "+responses);
		return responses;
	}

	/** Process detached components.
	 * After called, _detached is merged backed to _moved if it is required
	 */
	private void doDetached() {
		l_out: for (Map.Entry<Component, Component> me : _detached.entrySet()) {
			Component p = me.getValue();
			for (; p != null; p = p.getParent())
				if (_moved.contains(p) || _detached.containsKey(p) || _invalidated.containsKey(p) || _attached.contains(p))
					continue l_out; //don't merge (ignore it)

			_moved.add(me.getKey()); //merge
		}
		_detached.clear(); //no longer required
	}

	/** process moved components.
	 *
	 * <p>After called, _moved becomes empty.
	 * If they are removed, corresponding AuRemove are generated.
	 * If not, they are added to _attached.
	 *
	 * @return the dead components (i.e., not belong to any page)
	 */
	private Set<Component> doMoved(List<AuResponse> responses) {
		//Remove components that have to removed from the client
		final Set<Component> removed = new LinkedHashSet<Component>();
		for (Component comp : _moved) {
			final Page page = comp.getPage();
			if (page == null) {
				removed.add(comp);

				if (_responses != null)
					_responses.remove(comp);
				_invalidated.remove(comp);
				_smartUpdated.remove(comp);

				responses.add(new AuRemove(uuid(comp)));
				//Use the original UUID is important since it might be reused
			} else { //page != null
				if (_exec.isAsyncUpdate(page))
					responses.add(new AuRemove(uuid(comp)));
				//Use the original UUID is important since it might be reused
				_attached.add(comp);
				//copy to _attached since we handle them later in the same way
			}
		}

		_moved.clear(); //no longer required
		return removed;
	}

	/** Stores the original UUID of the specified component.
	 */
	private void snapshotUuid(Component comp) {
		if (!_uuids.containsKey(comp))
			_uuids.put(comp, comp.getUuid());
	}

	/** Returns the original UUID of the specified component.
	 */
	private String uuid(Component comp) {
		final String uuid = _uuids.get(comp);
		return uuid != null ? uuid : comp.getUuid();
	}

	/** Adds responses for a set of siblings which is new attached (or
	 * parent is changed).
	 */
	private static void addResponsesForCreatedPerSiblings(List<AuResponse> responses, Set<Component> newsibs,
			Map<Component, Set<? extends Component>> croppingInfos) throws IOException {
		final Component parent;
		final Page page;
		{
			final Component comp = newsibs.iterator().next();
			parent = comp.getParent();
			page = comp.getPage();
		}

		Collection<? extends Component> sibs = parent != null ? getAvailableAtClient(parent, croppingInfos) : null;
		//		if (log.finerable()) log.finer("All sibs: "+sibs+" newsibs: "+newsibs);

		/* Algorithm: 5.0.7
		1. Groups newsibs
		2. For each group, see if it is better to use AuAppendChild/AuInsertBefore/AuInsertAfter
		(Note: newsibs might not be ordered correctly, so we have to go through nextGroupedSiblings)
		*/
		for (List<Component> group; (group = nextGroupedSiblings(newsibs)) != null;) {
			final Collection<String> contents = redrawComponents(group);
			final Component last = group.get(group.size() - 1);
			Component nxt, prv;
			if ((nxt = last.getNextSibling()) == null || (sibs != null && !sibs.contains(nxt))) { //nextsib not available at client
				if (parent != null //since page might not available, we try AuInsertAfter first if parent is null
						&& !(parent instanceof Native) && !(parent instanceof StubComponent)) { //parent valid
					responses.add(new AuAppendChild(parent, contents));
				} else {
					final Component first = group.get(0);
					if ((prv = first.getPreviousSibling()) != null && (sibs == null || sibs.contains(prv)) //prv is available
							&& !(prv instanceof Native) && !(prv instanceof StubComponent)) { //prv valid
						responses.add(new AuInsertAfter(prv, contents));
					} else {
						if (parent != null)
							throw new UiException("Adding child to native or stubs not allowed: " + parent);
						responses.add(new AuAppendChild(page, contents));
					}
				}
			} else if (nxt instanceof Native || nxt instanceof StubComponent) { //native
				final Component first = group.get(0);
				if ((prv = first.getPreviousSibling()) == null || (sibs != null && !sibs.contains(prv))) //prv is not available
					throw new UiException("Inserting a component before a native one not allowed: " + nxt);

				//prv is available, so use AuInsertAfter prv instead
				responses.add(new AuInsertAfter(prv, contents));
			} else {
				//use AuInsertBefore nxt
				responses.add(new AuInsertBefore(nxt, contents));
			}
		}
	}

	private static List<Component> nextGroupedSiblings(Set<Component> newsibs) {
		if (newsibs.isEmpty())
			return null;

		final List<Component> group = new LinkedList<Component>();
		final Component first;
		{
			final Iterator<Component> it = newsibs.iterator();
			first = it.next();
			it.remove();
		}
		group.add(first);

		for (Component c = first; (c = c.getNextSibling()) != null && newsibs.remove(c);) //next is also new
			group.add(c);
		for (Component c = first; (c = c.getPreviousSibling()) != null && newsibs.remove(c);) //prev is also new
			group.add(0, c);
		return group;
	}

	/** Removes redundant components in _invalidated, _smartUpdated and _attached.
	 */
	private void removeRedundant() {
		int initsz = (_invalidated.size() + _attached.size()) / 2 + 30;
		final Set<Component> ins = new HashSet<Component>(initsz), //one of ancestor in _invalidated or _attached
				outs = new HashSet<Component>(initsz); //none of ancestor in _invalidated nor _attached
		final List<Component> ancs = new ArrayList<Component>(50);
		//process _invalidated
		for (Iterator<Component> it = _invalidated.keySet().iterator(); it.hasNext();) {
			Component p = it.next();
			if (_attached.contains(p)) { //attached has higher priority
				it.remove();
				continue;
			}
			boolean removed = false;
			while ((p = p.getParent()) != null) { //don't check p in _invalidated
				if (outs.contains(p)) //checked
					break;
				if (ins.contains(p) || _invalidated.containsKey(p) || _attached.contains(p)) {
					it.remove();
					removed = true;
					break;
				}
				ancs.add(p);
			}
			if (removed)
				ins.addAll(ancs);
			else
				outs.addAll(ancs);
			ancs.clear();
		}

		//process _attached
		for (Iterator<Component> it = _attached.iterator(); it.hasNext();) {
			Component p = it.next();
			boolean removed = false;
			while ((p = p.getParent()) != null) { //don't check p in _attached
				if (outs.contains(p)) //checked
					break;
				if (ins.contains(p) || _invalidated.containsKey(p) || _attached.contains(p)) {
					it.remove();
					removed = true;
					break;
				}
				ancs.add(p);
			}
			if (removed)
				ins.addAll(ancs);
			else
				outs.addAll(ancs);
			ancs.clear();
		}

		//process _smartUpdated
		for (Iterator<Component> it = _smartUpdated.keySet().iterator(); it.hasNext();) {
			Component p = it.next();
			boolean removed = false, first = true;
			for (; p != null; p = p.getParent()) { //check p in _smartUpdated
				if (outs.contains(p)) //checked
					break;
				if (ins.contains(p) || _invalidated.containsKey(p) || _attached.contains(p)) {
					it.remove();
					removed = true;
					break;
				}
				if (first)
					first = false; //No need to add 1st p
				else
					ancs.add(p);
			}
			if (removed)
				ins.addAll(ancs);
			else
				outs.addAll(ancs);
			ancs.clear();
		}
	}

	/** Redraw the specified component into a string.
	 */
	private static String redraw(Component comp) throws IOException {
		final StringWriter out = new StringWriter(1024 * 8);
		((ComponentCtrl) comp).redraw(out);
		return out.toString();
	}

	/** Redraws the whole page. */
	private static String redraw(Page page) throws IOException {
		final StringWriter out = new StringWriter(1024 * 8);
		((PageCtrl) page).redraw(out);
		return out.toString();
	}

	private static List<String> redrawComponents(Collection<Component> comps) throws IOException {
		final List<String> list = new LinkedList<String>();
		for (Component comp : comps)
			list.add(redraw(comp));
		return list;
	}

	/** Called before a component redraws itself if the component might
	 * include another page.
	 * <p>Since 5.0.6, the owner must implement {@link Includer}.
	 * @return the previous owner
	 * @since 5.0.0
	 */
	public Component setOwner(Component comp) {
		Component old = _owner;
		if (comp != null && !(comp instanceof Includer))
			throw new IllegalArgumentException(comp.getClass() + " must implement " + Includer.class.getName());
		_owner = comp;
		return old;
	}

	/** Returns the owner component for this execution, or null if
	 * this execution is not owned by any component.
	 * The owner is the top of the stack pushed by {@link #setOwner}.
	 * <p>Note: the owner, if not null, must implement {@link Includer}.
	 */
	public Component getOwner() {
		return _owner;
	}

	/** Used to hold smart update and response with a time stamp.
	 */
	private static class TimedValue implements Comparable {
		private final int _priority;
		private final int _timed;
		private final AuResponse _response;

		private TimedValue(int timed, AuResponse response, int priority) {
			_timed = timed;
			_response = response;
			_priority = priority;
		}

		private TimedValue(int timed, Component comp, String name, Object value, int priority) {
			_timed = timed;
			_response = new AuSetAttribute(comp, name, value);
			_priority = priority;
		}

		public String toString() {
			return '(' + _timed + ":" + _response + ')';
		}

		public int compareTo(Object o) {
			final TimedValue tv = (TimedValue) o;
			return _priority == tv._priority ? _timed > tv._timed ? 1 : _timed == tv._timed ? 0 : -1
					: _priority > tv._priority ? -1 : 1; //higher priority, earlier (smaller)
		}

		/** Returns the response representing this object. */
		private AuResponse getResponse() {
			return _response;
		}
	};

	/** Sets the reason to abort the current execution.
	 * if not null, it means the current execution is aborting
	 * and the specified argument is the reason to aborting.
	 * Its interpretation depends on {@link org.zkoss.zk.ui.sys.UiEngine}.
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
	 * {@link org.zkoss.zk.ui.sys.UiEngine}'s job to detect this flag
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
	 * 
	 * <p>The execution is aborting if {@link #getAbortingReason} returns
	 * not null and the returned reason's {@link AbortingReason#isAborting}
	 * is true.
	 *
	 * <p>Note: {@link Execution#isVoided} means the execution is voided
	 * and no output shall be generated. The request is taken charged
	 * by other servlet.
	 * On the other hand, {@link #isAborting} means the execution
	 * is aborting and the output shall still be generated (and sent).
	 * The request is still taken charged by this execution.
	 */
	public boolean isAborting() {
		return _aborting != null && _aborting.isAborting();
	}

	private static class ResponseInfo {
		/** A list of keyless responses. */
		private final List<TimedValue> keyless = new LinkedList<TimedValue>();
		/** A map of key and responses. */
		private final Map<String, TimedValue> values = new HashMap<String, TimedValue>(4);
	}
}
