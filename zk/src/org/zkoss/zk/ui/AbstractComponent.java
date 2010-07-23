/* AbstractComponent.java

	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:49:42     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.AbstractSequentialList;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Date;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.D;
import org.zkoss.lang.Library;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Strings;
import org.zkoss.lang.Objects;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.io.PrintWriterX;
import org.zkoss.io.Serializables;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Deferrable;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Macro;
import org.zkoss.zk.ui.ext.RawId;
import org.zkoss.zk.ui.ext.NonFellow;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.util.ComponentSerializationListener;
import org.zkoss.zk.ui.util.ComponentActivationListener;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.Names;
import org.zkoss.zk.ui.sys.ComponentRedraws;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.JsContentRenderer;
import org.zkoss.zk.ui.sys.JavaScriptValue;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.EventHandlerMap;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinitionMap;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.impl.ListenerIterator;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.ui.impl.SimpleScope;
import org.zkoss.zk.fn.ZkFns;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.au.out.AuClientInfo;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.scripting.*;

/**
 * A skeletal implementation of {@link Component}.
 *
 * @author tomyeh
 */
public class AbstractComponent
implements Component, ComponentCtrl, java.io.Serializable {
	private static final Log log = Log.lookup(AbstractComponent.class);
	private static final long serialVersionUID = 20100719L;

	/** Map(Class, Map(String name, Integer flags)). */
	private static final Map _clientEvents = new HashMap(128);
	private static final String DEFAULT = "default";

	/*package*/ transient Page _page;
	private String _id = "";
	private String _uuid;
	private transient ComponentDefinition _def;

	private transient AbstractComponent _parent;
	/** The next sibling. */
	/*package*/ transient AbstractComponent _next;
	/** The previous sibling. */
	/*package*/ transient AbstractComponent _prev;
	/** ChildInfo: use a class (rather than multiple member) to save footprint */
	private transient ChildInfo _chdinf;
	/** AuxInfo: use a class (rather than multiple member) to save footprint */
	private AuxInfo _auxinf;

	/** Constructs a component with auto-generated ID.
	 * @since 3.0.7 (becomes public)
	 */
	public AbstractComponent() {
		final String mold = getDefaultMold(getClass());
		if (mold != null && mold.length() > 0 && !DEFAULT.equals(mold))
			initAuxInfo().mold = mold;

		final Execution exec = Executions.getCurrent();
		final Object curInfo = ComponentsCtrl.getCurrentInfo();
		if (curInfo != null) {
			ComponentsCtrl.setCurrentInfo((ComponentInfo)null); //to avoid mis-use
			if (curInfo instanceof ComponentInfo) {
				final ComponentInfo compInfo = (ComponentInfo)curInfo;
				_def = compInfo.getComponentDefinition();
				addSharedAnnotationMap(_def.getAnnotationMap());
				addSharedAnnotationMap(compInfo.getAnnotationMap());
			} else {
				_def = (ComponentDefinition)curInfo;
				addSharedAnnotationMap(_def.getAnnotationMap());
			}
		} else {
			_def = getDefinition(exec, getClass());
			if (_def != null)
				addSharedAnnotationMap(_def.getAnnotationMap());
			else
				_def = ComponentsCtrl.DUMMY;
		}

		if (this instanceof IdSpace)
			initAuxInfo().spaceInfo = new SpaceInfo();

//		if (D.ON && log.debugable()) log.debug("Create comp: "+this);
	}
	/** Returns the component definition of the specified class, or null
	 * if not found.
	 */
	private static ComponentDefinition getDefinition(Execution exec, Class cls) {
		if (exec != null) {
			final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
			final PageDefinition pgdef = execCtrl.getCurrentPageDefinition();
			final Page page = execCtrl.getCurrentPage();

			final ComponentDefinition compdef =
				pgdef != null ? pgdef.getComponentDefinition(cls, true):
				page != null ? 	page.getComponentDefinition(cls, true): null;
			if (compdef != null && compdef.getLanguageDefinition() != null)
				return compdef; //already from langdef (not from pgdef)

			final ComponentDefinition compdef2 =
				Components.getDefinitionByDeviceType(exec.getDesktop().getDeviceType(), cls);
			return compdef != null && (compdef2 == null ||
			!Objects.equals(compdef.getImplementationClass(), compdef2.getImplementationClass())) ?
				compdef: compdef2; //Feature 2816083: use compdef2 if same class
		}

		for (Iterator it = LanguageDefinition.getDeviceTypes().iterator(); it.hasNext();) {
			final ComponentDefinition compdef =
				Components.getDefinitionByDeviceType((String)it.next(), cls);
			if (compdef != null)
				return compdef;
		}
		return null;
	}
	private ComponentDefinition
	getDefinitionByDeviceType(String deviceType, String name) {
		for (Iterator it = LanguageDefinition.getByDeviceType(deviceType).iterator();
		it.hasNext();) {
			final LanguageDefinition ld = (LanguageDefinition)it.next();
			try {
				final ComponentDefinition def = ld.getComponentDefinition(name);
				if (def.isInstance(this))
					return def;
			} catch (DefinitionNotFoundException ex) { //ignore
			}
		}
		return null;
	}

	/**
	 * Creates and returns the instance for storing child components.
	 * <p>Default: it instantiates {@link AbstractComponent.Children}.
	 * @deprecated As of release 5.0.4, override {@link #getChildren} instead.
	 * @since 3.5.1
	 */
	protected List newChildren() {
		return new Children();
	}
	/** The default implementation for {@link #getChildren}.
	 * It is suggested to extend this class if you want to override
	 * {@link #getChildren} to instantiate your own instance.
	 * @since 3.5.1
	 */
	protected class Children extends AbstractSequentialList {
		public int size() {
			return nChild();
		}
		public ListIterator listIterator(int index) {
			return new ChildIter(index);
		}
	}

	/** Adds to the ID spaces, if any, when ID is changed.
	 * Caller has to make sure the uniqueness (and not auto id).
	 */
	private static void addToIdSpaces(final Component comp) {
		if (comp instanceof IdSpace)
			((AbstractComponent)comp).bindToIdSpace(comp);

		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component)
			((AbstractComponent)is).bindToIdSpace(comp);
		else if (is != null)
			((AbstractPage)is).addFellow(comp);
	}
	private static IdSpace getSpaceOwnerOfParent(Component comp) {
		final Component parent = comp.getParent();
		if (parent != null) return parent.getSpaceOwner();
		else return comp.getPage();
	}
	/** Removes from the ID spaces, if any, when ID is changed. */
	private static void removeFromIdSpaces(final Component comp) {
		final String compId = comp.getId();
		if (comp instanceof NonFellow || isAutoId(compId))
			return; //nothing to do

		if (comp instanceof IdSpace)
			((AbstractComponent)comp).unbindFromIdSpace(compId);

		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component)
			((AbstractComponent)is).unbindFromIdSpace(compId);
		else if (is != null)
			((AbstractPage)is).removeFellow(comp);
	}
	/** Checks the uniqueness in ID space when changing ID. */
	private static void checkIdSpaces(final AbstractComponent comp, String newId) {
		if (comp instanceof NonFellow)
			return; //no need to check

		if (comp instanceof IdSpace
		&& comp._auxinf.spaceInfo.fellows.containsKey(newId))
			throw new UiException("Not unique in the ID space of "+comp);

		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component) {
			if (((AbstractComponent)is)._auxinf.spaceInfo.fellows.containsKey(newId))
				throw new UiException("Not unique in ID space "+is+": "+newId);
		} else if (is != null) {
			if (is.hasFellow(newId))
				throw new UiException("Not unique in ID space "+is+": "+newId);
		}
	}
	private static boolean isAutoId(String compId) {
		return compId.length() == 0;
	}

	/** Adds its descendants to the ID space when parent or page is changed,
	 * excluding comp.
	 */
	private static void addToIdSpacesDown(Component comp) {
		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component)
			addToIdSpacesDown(comp, (Component)is);
		else if (is != null)
			addToIdSpacesDown(comp, (AbstractPage)is);
	}
	/** comp's ID might be auto id.
	 * @param owner it must be an IdSpace
	 */
	private static void addToIdSpacesDown(Component comp, Component owner) {
		if (!(comp instanceof NonFellow) && !isAutoId(comp.getId()))
			((AbstractComponent)owner).bindToIdSpace(comp);

		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = (AbstractComponent)comp.getFirstChild();
			ac != null; ac = ac._next)
				addToIdSpacesDown(ac, owner); //recursive

		((AbstractComponent)comp).notifyIdSpaceChanged((IdSpace)owner);
	}
	/** comp's ID might be auto id. */
	private static void addToIdSpacesDown(Component comp, AbstractPage owner) {
		if (!(comp instanceof NonFellow) && !isAutoId(comp.getId()))
			owner.addFellow(comp);

		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = (AbstractComponent)comp.getFirstChild();
			ac != null; ac = ac._next)
				addToIdSpacesDown(ac, owner); //recursive

		((AbstractComponent)comp).notifyIdSpaceChanged(owner);
	}
	private void notifyIdSpaceChanged(IdSpace newIdSpace) {
		if (_auxinf != null && _auxinf.attrs != null)
			_auxinf.attrs.notifyIdSpaceChanged(newIdSpace);
	}

	/** Adds its descendants to the ID space when parent or page is changed,
	 * excluding comp.
	 */
	private static void removeFromIdSpacesDown(Component comp) {
		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component)
			removeFromIdSpacesDown(comp, (Component)is);
		else if (is != null)
			removeFromIdSpacesDown(comp, (AbstractPage)is);
	}
	private static void removeFromIdSpacesDown(Component comp, Component owner) {
		final String compId = comp.getId();
		if (!(comp instanceof NonFellow) && !isAutoId(compId))
			((AbstractComponent)owner).unbindFromIdSpace(compId);

		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = (AbstractComponent)comp.getFirstChild();
			ac != null; ac = ac._next)
				removeFromIdSpacesDown(ac, owner); //recursive

		((AbstractComponent)comp).notifyIdSpaceChanged(null);
	}
	private static void removeFromIdSpacesDown(Component comp, AbstractPage owner) {
		if (!(comp instanceof NonFellow) && !isAutoId(comp.getId()))
			owner.removeFellow(comp);

		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = (AbstractComponent)comp.getFirstChild();
			ac != null; ac = ac._next)
				removeFromIdSpacesDown(ac, owner); //recursive

		((AbstractComponent)comp).notifyIdSpaceChanged(null);
	}

	/** Checks the uniqueness in ID space when changing parent. */
	private static void checkIdSpacesDown(Component comp, Component newparent) {
		final IdSpace is = newparent.getSpaceOwner();
		if (is instanceof Component)
			checkIdSpacesDown(comp, ((AbstractComponent)is)._auxinf.spaceInfo);
		else if (is != null)
			checkIdSpacesDown(comp, (AbstractPage)is);
	}
	/** Checks comp and its descendants for the specified SpaceInfo. */
	private static void checkIdSpacesDown(Component comp, SpaceInfo si) {
		final String compId = comp.getId();
		if (!(comp instanceof NonFellow)
		&& !isAutoId(compId) && si.fellows.containsKey(compId))
			throw new UiException("Not unique in the new ID space: "+compId);
		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = (AbstractComponent)comp.getFirstChild();
			ac != null; ac = ac._next)
				checkIdSpacesDown(ac, si); //recursive
	}
	/** Checks comp and its descendants for the specified page. */
	private static void checkIdSpacesDown(Component comp, AbstractPage page) {
		final String compId = comp.getId();
		if (!(comp instanceof NonFellow)
		&& !isAutoId(compId) && page.hasFellow(compId))
			throw new UiException("Not unique in the ID space of "+page+": "+compId);
		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = (AbstractComponent)comp.getFirstChild();
			ac != null; ac = ac._next)
				checkIdSpacesDown(ac, page); //recursive
	}

	/** Bind comp to this ID space (owned by this component).
	 * Called only if IdSpace is implemented.
	 * comp's ID must be unquie (and not auto id)
	 */
	private void bindToIdSpace(Component comp) {
		_auxinf.spaceInfo.fellows.put(comp.getId(), comp);
	}
	/** Unbind comp from this ID space (owned by this component).
	 * Called only if IdSpace is implemented.
	 */
	private void unbindFromIdSpace(String compId) {
		_auxinf.spaceInfo.fellows.remove(compId);
	}

	//-- Extra utlities --//
	/** Returns the UI engine based on {@link #_page}'s getDesktop().
	 * Don't call this method when _page is null.
	 */
	private UiEngine getAttachedUiEngine() {
		return ((WebAppCtrl)_page.getDesktop().getWebApp()).getUiEngine();
	}
	/** Returns the UI engine of the current execution, or null
	 * if no current execution.
	 */
	private UiEngine getCurrentUiEngine() {
		final Execution exec = Executions.getCurrent();
		return exec != null ?
			((WebAppCtrl)exec.getDesktop().getWebApp()).getUiEngine(): null;
	}

	//-- Component --//
	public Page getPage() {
		return _page;
	}
	public Desktop getDesktop() {
		return _page != null ? _page.getDesktop(): null;
	}

	public void setPage(Page page) {
		if (page != _page)
			setPageBefore(page, null); //append
	}
	public void setPageBefore(Page page, Component refRoot) {
		if (refRoot != null && (page == null
		|| refRoot.getParent() != null || refRoot.getPage() != page))
			refRoot = null;
		if (refRoot != null /*&& refRoot.getPage() == page (checked)*/
		&& (refRoot == this || refRoot == _next))
			return; //nothing to do

		if (_parent != null)
			throw new UiException("Only the parent of a root component can be changed: "+this);

		final Page oldpg = _page;
		final boolean samepg = page == _page;
		if (!samepg) {
			if (page != null) {
				if (_page != null && _page.getDesktop() != page.getDesktop())
					throw new UiException("The new page must be in the same desktop: "+page);
					//Not allow developers to access two desktops simutaneously
				checkIdSpacesDown(this, (AbstractPage)page);

				//No need to check UUID since checkIdSpacesDown covers it
				//-- a page is an ID space
			} else { //detach from a page
				checkDetach(_page);
			}

			if (_page != null) removeFromIdSpacesDown(this);
		}

		addMoved(_parent, _page, page); //Not depends on UUID
		if (!samepg)
			setPage0(page); //UUID might be changed here
		if (page != null && (samepg || refRoot != null))
			((AbstractPage)page).moveRoot(this, refRoot);

		if (!samepg && _page != null) addToIdSpacesDown(this);

		afterComponentPageChanged(page, oldpg);
	}
	/** Checks whether it is OK to detach the specified page.
	 * @param page the page to detach (never null).
	 */
	private static void checkDetach(Page page) {
		final Execution exec = Executions.getCurrent();
		if (exec == null)
			throw new UiException("You cannot access a desktop other than an event listener");
		if (page.getDesktop() != exec.getDesktop())
			throw new UiException("You cannot access components belong to other desktop");
	}
	/** Called when this component is moved from the specified parent
	 * and/or page to the new page.
	 *
	 * <p>Default: it notifies {@link UiEngine} to update the component
	 * at the client (usually remove-and-add).
	 *
	 * <p>It is designed to let derived classes overriding this method
	 * to disable this update. However, you rarely need to override it.
	 * One possible but rare case: the component's
	 * visual part at the client updates the visual representation
	 * at the client and then notify the component at the server
	 * to update its children accordingly. In this case, it is redudant
	 * if we ask UI Engine to send the updates to client.
	 *
	 * @param oldparent the parent before moved.
	 * The new parent can be found by calling {@link #getParent}.
	 * @param oldpg the parent before moved.
	 * @param newpg the new page. {@link #getPage} might return
	 * the old page.
	 */
	protected void addMoved(Component oldparent, Page oldpg, Page newpg) {
		final Desktop dt;
		if (oldpg != null) dt = oldpg.getDesktop();
		else if (newpg != null) dt = newpg.getDesktop();
		else return;

		((WebAppCtrl)dt.getWebApp())
			.getUiEngine().addMoved(this, oldparent, oldpg, newpg);
	}

	/** Ses the page without fixing IdSpace
	 */
	private void setPage0(Page page) {
		if (page == _page)
			return; //nothing changed

		//assert D.OFF || _parent == null || _parent.getPage() == page;
		//detach
		final boolean bRoot = _parent == null;
		if (_page != null) {
			if (bRoot) ((AbstractPage)_page).removeRoot(this);
			if (page == null
			&& ((DesktopCtrl)_page.getDesktop()).removeComponent(this, true))
				_uuid = null; //recycled (so reset it -- refer to DesktopImpl for reason)
		}

		final Page oldpage = _page;
		_page = page;

		if (_page != null) {
			if (bRoot) ((AbstractPage)_page).addRoot(this); //Not depends on uuid
			final Desktop desktop = _page.getDesktop();
			if (oldpage == null) {
				if (_uuid == null || _uuid == ComponentsCtrl.ANONYMOUS_ID
				|| desktop.getComponentByUuidIfAny(_uuid) != null)
					_uuid = nextUuid(desktop);

				((DesktopCtrl)desktop).addComponent(this); //depends on uuid
			}

			onPageAttached(_page, oldpage);
		} else {
			onPageDetached(oldpage);
		}

		//process all children recursively
		for (AbstractComponent p = (AbstractComponent)getFirstChild();
		p != null; p = p._next)
			p.setPage0(page); //recursive
	}

	private String nextUuid(Desktop desktop) {
		String uuid;
		do {
			uuid = ((DesktopCtrl)desktop).getNextUuid(this);
		} while (desktop.getComponentByUuidIfAny(uuid) != null);
		return uuid;
	}
	public String getId() {
		return _id;
	}
	public void setId(String id) {
		if (id == null) id = "";
		if (!id.equals(_id)) {
			boolean rawId = this instanceof RawId;
			String newUuid;
			if (rawId) newUuid = id;
			else if ((newUuid = id2Uuid(id)) != null)
				rawId = true;

			if (id.length() > 0) {
				if (Names.isReserved(id))
					throw new UiException("Invalid ID: "+id+". Cause: reserved words not allowed: "+Names.getReservedNames());

				if (rawId && _page != null) {
					final Component c = _page.getDesktop().getComponentByUuidIfAny(newUuid);
					if (c != null && c != this)
						throw new UiException("Replicated UUID is not allowed for "+getClass()+": "+newUuid);
				}

				checkIdSpaces(this, id);
			}

			removeFromIdSpaces(this);
			if (rawId) { //we have to change UUID
				if (_page != null) {
						//called before uuid is changed
					final Desktop dt = _page.getDesktop();
					((DesktopCtrl)dt).removeComponent(this, false);

					if (newUuid.length() == 0)
						newUuid = nextUuid(dt);

					if (!Objects.equals(_uuid, newUuid))
						getAttachedUiEngine().addUuidChanged(this);
				}

				_id = id;
				_uuid = newUuid;

				if (_page != null) {
					((DesktopCtrl)_page.getDesktop()).addComponent(this);
				}
			} else {
				_id = id;
			}

			if (_id.length() > 0)
				addToIdSpaces(this);

			smartUpdate("id", _id);
		}
	}

	public String getUuid() {
		if (_uuid == null) {
			final Execution exec = Executions.getCurrent();
			_uuid = exec == null ?
				ComponentsCtrl.ANONYMOUS_ID: nextUuid(exec.getDesktop());
		}
		return _uuid;
	}

	public IdSpace getSpaceOwner() {
		Component p = this;
		do {
			if (p instanceof IdSpace)
				return (IdSpace)p;
		} while ((p = p.getParent()) != null);
		return _page;
	}
	public boolean hasFellow(String compId) {
		if (this instanceof IdSpace)
			return _auxinf.spaceInfo.fellows.containsKey(compId);

		final IdSpace idspace = getSpaceOwner();
		return idspace != null && idspace.hasFellow(compId);
	}
	public Component getFellow(String compId)
	throws ComponentNotFoundException {
		if (this instanceof IdSpace) {
			final Component comp = (Component)_auxinf.spaceInfo.fellows.get(compId);
			if (comp == null)
				throw new ComponentNotFoundException("Fellow component not found: "+compId);
			return comp;
		}

		final IdSpace idspace = getSpaceOwner();
		if (idspace == null)
			throw new ComponentNotFoundException("This component doesn't belong to any ID space: "+this);
		return idspace.getFellow(compId);
	}
	public Component getFellowIfAny(String compId) {
		if (this instanceof IdSpace)
			return (Component)_auxinf.spaceInfo.fellows.get(compId);

		final IdSpace idspace = getSpaceOwner();
		return idspace == null ? null: idspace.getFellowIfAny(compId);
	}
	public Collection getFellows() {
		if (this instanceof IdSpace)
			return Collections.unmodifiableCollection(_auxinf.spaceInfo.fellows.values());

		final IdSpace idspace = getSpaceOwner();
		return idspace == null ? Collections.EMPTY_LIST: idspace.getFellows();
	}
	public Component getFellow(String compId, boolean recurse)
	throws ComponentNotFoundException {
		final Component comp = getFellowIfAny(compId, recurse);
		if (comp == null)
			throw new ComponentNotFoundException("Fellow component not found: "+compId);
		return comp;
	}
	public Component getFellowIfAny(String compId, boolean recurse) {
		if (!recurse)
			return getFellowIfAny(compId);

		for (IdSpace idspace = getSpaceOwner(); idspace != null;) {
			Component f = idspace.getFellowIfAny(compId);
			if (f != null) return f;
			idspace = Components.getParentIdSpace(idspace);
		}
		return null;
	}
	public boolean hasFellow(String compId, boolean recurse) {
		return getFellowIfAny(compId, recurse) != null;
	}

	public Component getNextSibling() {
		return _next;
	}
	public Component getPreviousSibling() {
		return _prev;
	}
	public Component getFirstChild() {
		return _chdinf != null ? _chdinf.first: null;
	}
	public Component getLastChild() {
		return _chdinf != null ? _chdinf.last: null;
	}
	private final int nChild() {
		return _chdinf != null ? _chdinf.nChild: 0;
	}
	private int modCntChd() {
		return _chdinf != null ? _chdinf.modCntChd: 0;
	}

	public String setWidgetListener(String evtnm, String script) {
		if (evtnm == null)
			throw new IllegalArgumentException();

		final String old;
		if (script != null) {
			if (initAuxInfo().wgtlsns == null) _auxinf.wgtlsns = new LinkedHashMap();
			old = (String)_auxinf.wgtlsns.put(evtnm, script);
		} else
			old = _auxinf != null && _auxinf.wgtlsns != null ?
				(String)_auxinf.wgtlsns.remove(evtnm): null;
		if (!Objects.equals(script, old))
			smartUpdateWidgetListener(evtnm, script);
		return old;
	}
	public String getWidgetListener(String evtnm) {
		return _auxinf != null && _auxinf.wgtlsns != null ?
			(String)_auxinf.wgtlsns.get(evtnm): null;
	}
	public Set getWidgetListenerNames() {
		return _auxinf != null && _auxinf.wgtlsns != null ?
			_auxinf.wgtlsns.keySet(): Collections.EMPTY_SET;
	}

	public String setWidgetOverride(String name, String script) {
		if (name == null)
			throw new IllegalArgumentException();

		final String old;
		if (script != null) {
			if (initAuxInfo().wgtovds == null) _auxinf.wgtovds = new LinkedHashMap();
			old = (String)_auxinf.wgtovds.put(name, script);
		} else
			old = _auxinf != null && _auxinf.wgtovds != null ?
				(String)_auxinf.wgtovds.remove(name): null;
		if (!Objects.equals(script, old))
			smartUpdateWidgetOverride(name, script);
		return old;
	}
	public String getWidgetOverride(String name) {
		return _auxinf != null && _auxinf.wgtovds != null ?
			(String)_auxinf.wgtovds.get(name): null;
	}
	public Set getWidgetOverrideNames() {
		return _auxinf != null && _auxinf.wgtovds != null ?
			_auxinf.wgtovds.keySet(): Collections.EMPTY_SET;
	}

	public String setWidgetAttribute(String name, String value) {
		if (name == null)
			throw new IllegalArgumentException();

		final String old;
		if (value != null) {
			if (initAuxInfo().wgtattrs == null) _auxinf.wgtattrs = new LinkedHashMap();
			old = (String)_auxinf.wgtattrs.put(name, value);
		} else
			old = _auxinf != null && _auxinf.wgtattrs != null ?
				(String)_auxinf.wgtattrs.remove(name): null;
		return old;
	}
	public String getWidgetAttribute(String name) {
		return _auxinf != null && _auxinf.wgtattrs != null ?
			(String)_auxinf.wgtattrs.get(name): null;
	}
	public Set getWidgetAttributeNames() {
		return _auxinf.wgtattrs != null ? _auxinf.wgtattrs.keySet(): Collections.EMPTY_SET;
	}

	public Map getAttributes(int scope) {
		switch (scope) {
		case SPACE_SCOPE:
			if (this instanceof IdSpace)
				return getAttributes();
			final IdSpace idspace = getSpaceOwner();
			return idspace != null ? idspace.getAttributes(): Collections.EMPTY_MAP;
		case PAGE_SCOPE:
			return _page != null ?
				_page.getAttributes(): Collections.EMPTY_MAP;
		case DESKTOP_SCOPE:
			return _page != null ?
				_page.getDesktop().getAttributes(): Collections.EMPTY_MAP;
		case SESSION_SCOPE:
			return _page != null ?
				_page.getDesktop().getSession().getAttributes(): Collections.EMPTY_MAP;
		case APPLICATION_SCOPE:
			return _page != null ?
				_page.getDesktop().getWebApp().getAttributes(): Collections.EMPTY_MAP;
		case COMPONENT_SCOPE:
			return getAttributes();
		case REQUEST_SCOPE:
			final Execution exec = getExecution();
			if (exec != null) return exec.getAttributes();
			//fall thru
		default:
			return Collections.EMPTY_MAP;
		}
	}
	private SimpleScope attrs() {
		if (initAuxInfo().attrs == null)
			_auxinf.attrs = new SimpleScope(this);
		return _auxinf.attrs;
	}
	private Execution getExecution() {
		return _page != null ? _page.getDesktop().getExecution():
			Executions.getCurrent();
	}
	public Object getAttribute(String name, int scope) {
		return getAttributes(scope).get(name);
	}
	public boolean hasAttribute(String name, int scope) {
		return getAttributes(scope).containsKey(name);
	}
	public Object setAttribute(String name, Object value, int scope) {
		if (value != null) {
			final Map attrs = getAttributes(scope);
			if (attrs == Collections.EMPTY_MAP)
				throw new IllegalStateException("This component, "+this
					+", doesn't belong to the "+Components.scopeToString(scope)+" scope");
			return attrs.put(name, value);
		} else {
			return removeAttribute(name, scope);
		}
	}
	public Object removeAttribute(String name, int scope) {
			final Map attrs = getAttributes(scope);
			if (attrs == Collections.EMPTY_MAP)
				throw new IllegalStateException("This component doesn't belong to any ID space: "+this);
		return attrs.remove(name);
	}

	public Map getAttributes() {
		return attrs().getAttributes();
	}
	public Object getAttribute(String name) {
		return _auxinf != null && _auxinf.attrs != null ? _auxinf.attrs.getAttribute(name): null;
	}
	public boolean hasAttribute(String name) {
		return _auxinf != null && _auxinf.attrs != null && _auxinf.attrs.hasAttribute(name);
	}
	public Object setAttribute(String name, Object value) {
		return value != null ? attrs().setAttribute(name, value): removeAttribute(name);
	}
	public Object removeAttribute(String name) {
		return _auxinf != null && _auxinf.attrs != null ? _auxinf.attrs.removeAttribute(name): null;
	}
	
	public Object getAttribute(String name, boolean recurse) {
		Object val = getAttribute(name);
		if (val != null || !recurse || hasAttribute(name))
			return val;

		if (_parent != null)
			return _parent.getAttribute(name, true);
		if (_page != null)
			return _page.getAttribute(name, true);
		return null;
	}
	public boolean hasAttribute(String name, boolean recurse) {
		if (hasAttribute(name))
			return true;

		if (recurse) {
			if (_parent != null)
				return _parent.hasAttribute(name, true);
			if (_page != null)
				return _page.hasAttribute(name, true);
		}
		return false;
	}
	public Object setAttribute(String name, Object value, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			if (_parent != null) {
				if (_parent.hasAttribute(name, true))
					return _parent.setAttribute(name, value, true);
			} else if (_page != null) {
				if (_page.hasAttribute(name, true))
					return _page.setAttribute(name, value, true);
			}
		}
		return setAttribute(name, value);
	}
	public Object removeAttribute(String name, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			if (_parent != null) {
				if (_parent.hasAttribute(name, true))
					return _parent.removeAttribute(name, true);
			} else if (_page != null) {
				if (_page.hasAttribute(name, true))
					return _page.removeAttribute(name, true);
			}
			return null;
		}
		return removeAttribute(name);
	}

	public Object getAttributeOrFellow(String name, boolean recurse) {
		Object val = getAttribute(name);
		if (val != null || hasAttribute(name))
			return val;

		if (this instanceof IdSpace) { //fellow last
			val = getFellowIfAny(name);
			if (val != null)
				return val;
		}

		if (recurse) {
			if (_parent != null)
				return _parent.getAttributeOrFellow(name, true);
			if (_page != null)
				return _page.getAttributeOrFellow(name, true);
		}
		return null;
	}
	public boolean hasAttributeOrFellow(String name, boolean recurse) {
		if (hasAttribute(name)
		|| (this instanceof IdSpace && hasFellow(name)))
			return true;

		if (recurse) {
			if (_parent != null)
				return _parent.hasAttributeOrFellow(name, true);
			if (_page != null)
				return _page.hasAttributeOrFellow(name, true);
		}
		return false;
	}

	public boolean addScopeListener(ScopeListener listener) {
		return attrs().addScopeListener(listener);
	}
	public boolean removeScopeListener(ScopeListener listener) {
		return attrs().removeScopeListener(listener);
	}

	/** @deprecated As of release 5.0.0, replaced with {@link #setAttribute}. */
	public void setVariable(String name, Object val, boolean local) {
		getNamespace().setVariable(name, val, local);
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #hasAttribute}. */
	public boolean containsVariable(String name, boolean local) {
		return getNamespace().containsVariable(name, local);
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #getAttribute}. */
	public Object getVariable(String name, boolean local) {
		return getNamespace().getVariable(name, local);
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #removeAttribute}. */
	public void unsetVariable(String name, boolean local) {
		getNamespace().unsetVariable(name, local);
	}

	public Component getParent() {
		return _parent;
	}
	public void setParent(Component parent) {
		if (_parent == parent)
			return; //nothing changed

		checkParentChild(parent, this); //create _chdinf
		beforeParentChanged(parent);

		final boolean idSpaceChanged =
			parent != null ?
				parent.getSpaceOwner() !=
					(_parent != null ? _parent.getSpaceOwner(): _page):
				_page != null || _parent.getSpaceOwner() != null;

		if (idSpaceChanged) removeFromIdSpacesDown(this);

		//call removeChild and clear _parent
		final AbstractComponent op = _parent;
		if (op != null) {
			if (!op._chdinf.inRemoving(this)) {
				op._chdinf.markRemoving(this, true);
				try {
					op.removeChild(this); //spec: call back removeChild
				} finally {
					op._chdinf.markRemoving(this, false);
				}
			}
			_parent = null; //op.removeChild assumes _parent not changed yet
		} else {
			if (_page != null)
				((AbstractPage)_page).removeRoot(this); //Not depends on uuid
		}

		//call insertBefore and set _parent
		if (parent != null) {
			final AbstractComponent np = (AbstractComponent)parent;
			if (!np._chdinf.inAdding(this)) {
				np._chdinf.markAdding(this, true);
				try {
					np.insertBefore(this, null); //spec: call back inserBefore
				} finally {
					np._chdinf.markAdding(this, false);
				}
			}
			_parent = np; //np.insertBefore assumes _parent not changed yet
		} //if parent == null, assume no page at all (so no addRoot)

		//correct _page
		final Page newpg = _parent != null ? _parent.getPage(): null,
			oldpg = _page;
		addMoved(op, _page, newpg); //Not depends on UUID
		setPage0(newpg); //UUID might be changed here

		if (_auxinf != null && _auxinf.attrs != null)
			_auxinf.attrs.notifyParentChanged(_parent != null ? _parent: (Scope)_page);
		if (idSpaceChanged) addToIdSpacesDown(this); //called after setPage

		//call back UiLifeCycle
		afterComponentPageChanged(newpg, oldpg);
		if (newpg != null || oldpg != null) {
			final Desktop desktop = (oldpg != null ? oldpg: newpg).getDesktop();
			if (desktop != null) {
				((DesktopCtrl)desktop).afterComponentMoved(parent, this, op);
				desktop.getWebApp().getConfiguration().afterComponentMoved(parent, this, op);
			}
		}
	}
	private void afterComponentPageChanged(Page newpg, Page oldpg) {
		if (newpg == oldpg) return;

		final Desktop desktop = (oldpg != null ? oldpg: newpg).getDesktop();
		if (desktop == null) return; //just in case

		//Note: if newpg and oldpg both non-null, they must be the same
		if (oldpg != null) {
			((DesktopCtrl)desktop).afterComponentDetached(this, oldpg);
			desktop.getWebApp().getConfiguration().afterComponentDetached(this, oldpg);
		} else {
			((DesktopCtrl)desktop).afterComponentAttached(this, newpg);
			desktop.getWebApp().getConfiguration().afterComponentAttached(this, newpg);
		}
	}

	/**
	 * Checks the parent-child relation.
	 * Notice it will create parent._chdinf
	 * @param parent the parent (will-be). It may be null.
	 * @param child the child (will-be). It cannot be null.
	 */
	private static void checkParentChild(Component parent, Component child)
	throws UiException {
		if (parent != null) {
			final AbstractComponent acp = (AbstractComponent)parent;
			if (acp.initChildInfo().inAdding(child))
				return; //check only once

			if (Components.isAncestor(child, parent))
				throw new UiException("A child cannot be a parent of its ancestor: "+child);
			if (!acp.isChildable())
				throw new UiException("Child not allowed in "+parent.getClass().getName());

			final Page parentpg = parent.getPage(), childpg = child.getPage();
			if (parentpg != null && childpg != null
			&& parentpg.getDesktop() != childpg.getDesktop())
				throw new UiException("The parent and child must be in the same desktop: "+parent);

			final Component oldparent = child.getParent();
			if (parent.getSpaceOwner() !=
			(oldparent != null ? oldparent.getSpaceOwner(): childpg))
				checkIdSpacesDown(child, parent);
		} else {
			final Page childpg = child.getPage();
			if (childpg != null)
				checkDetach(childpg);
		}
	}

	public boolean insertBefore(Component newChild, Component refChild) {
		if ((newChild instanceof Macro) && ((Macro)newChild).isInline())
			return ((Macro)newChild).setInlineParent(this, refChild);

		checkParentChild(this, newChild); ///create _chdinf

		if (refChild != null && refChild.getParent() != this)
			refChild = null;

		if (newChild == refChild)
			return false; //nothing changed (Listbox and other assumes this)

		beforeChildAdded(newChild, refChild);

		final AbstractComponent nc = (AbstractComponent)newChild;
		final boolean moved = nc._parent == this; //moved in the same parent
		if (moved) {
			if (nc._next == refChild)
				return false; //nothing changed
			nc.addMoved(this, _page, _page);

			//detach from original place
			setNext(nc._prev, nc._next);
			setPrev(nc._next, nc._prev);
		} else { //new added
			//Note: call setParent to detach nc from old parent, if any,
			//before maintaining nc's _next, _prev...
			if (!_chdinf.inAdding(nc)) {
				_chdinf.markAdding(nc, true);
				try {
					nc.setParent(this); //spec: callback setParent
				} finally {
					_chdinf.markAdding(nc, false);
				}
			} else {
				nc._parent = this;
				//Set it since deriving class might assume parent is correct
				//after insertBefore. For example, Tabs.insertBefore().
				//
				//However, we don't call setPage0 and other here,
				//since the codes will become too complex.
				//In other words, when super.insertBefore() returns in a
				//deriving class, _parent is correct but _page may or may not
			}
		}

		if (refChild != null) {
			final AbstractComponent ref = (AbstractComponent)refChild;
			setNext(nc, ref);
			setPrev(nc, ref._prev);
			setNext(ref._prev, nc);
			setPrev(ref, nc);
		} else {
			if (_chdinf.last == null) {
				_chdinf.first = _chdinf.last = nc;
				nc._next = nc._prev = null;
			} else {
				_chdinf.last._next = nc;
				nc._prev = _chdinf.last;
				nc._next = null;
				_chdinf.last = nc;
			}
		}

		++_chdinf.modCntChd;
		if (!moved) { //new added
			++_chdinf.nChild;
			onChildAdded(nc);
		}
		return true;
	}
	private void setNext(AbstractComponent comp, AbstractComponent next) {
		if (comp != null) comp._next = next;
		else _chdinf.first = next;
	}
	private void setPrev(AbstractComponent comp, AbstractComponent prev) {
		if (comp != null) comp._prev = prev;
		else _chdinf.last = prev;
	}

	/** Appends a child to the end of all children.
	 * It calls {@link #insertBefore} with refChild to be null.
	 * Derives cannot override this method, and they shall override
	 * {@link #insertBefore} instead.
	 */
	public boolean appendChild(Component child) { //Yes, final; see below
		return insertBefore(child, null); //NOTE: we must go thru insertBefore
			//such that deriving is easy to override
	}
	public boolean removeChild(Component child) {
		final AbstractComponent oc = (AbstractComponent)child;
		if (oc._parent != this)
			return false; //nothing to do

		beforeChildRemoved(child);

		setNext(oc._prev, oc._next);
		setPrev(oc._next, oc._prev);
		oc._next = oc._prev = null;

		if (!_chdinf.inRemoving(oc)) {
			_chdinf.markRemoving(oc, true);
			try {
				oc.setParent(null); //spec: call back setParent
			} finally {
				_chdinf.markRemoving(oc, false);
			}
		} else {
			oc._parent = null;
				//Correct it since deriving class might assume parent is
				//correct after insertBefore() returns.
				//refer to insertBefore for more info.
		}

		++_chdinf.modCntChd;
		--_chdinf.nChild;
		onChildRemoved(child);
		return true;
	}

	/** Returns whether this component can have a child.
	 * <p>Default: return true (means it can have children).
	 */
	protected boolean isChildable() {
		return true;
	}
	/** Returns a live list of children.
	 * By live we mean the developer could add or remove a child by manipulating the returned list directly.
	 * <p>Default: instantiates and returns an instance of {@link Children}.
	 */
	public List getChildren() {
		return newChildren(); //backward compatible: don't new Children() directly
	}
	/** Returns the root of the specified component.
	 */
	public Component getRoot() {
		for (Component comp = this;;) {
			final Component parent = comp.getParent();
			if (parent == null)
				return comp;
			comp = parent;
		}
	}


	public boolean isVisible() {
		return _auxinf == null || _auxinf.visible;
	}
	public boolean setVisible(boolean visible) {
		final boolean old = _auxinf == null || _auxinf.visible;
		if (old != visible) {
			initAuxInfo().visible = visible;
			smartUpdate("visible", _auxinf.visible);
		}
		return old;
	}
	/** Changes the visibility directly without sending any update to the client.
	 * It is the caller's responsibility to maintain the consistency.
	 * It is rarely called. In most cases, you shall use {@link #setVisible} instead.
	 * @since 5.0.4
	 */
	protected void setVisibleDirectly(boolean visible) {
		initAuxInfo().visible = visible;
	}

	public boolean isInvalidated() {
		return _page == null || getAttachedUiEngine().isInvalidated(this);
	}
	public void invalidate() {
		if (_page != null)
			getAttachedUiEngine().addInvalidate(this);
	}

	/** Causes a response to be sent to the client.
	 * It is the same as <code>response(response.getOverrideKey(), response)</code>
	 *
	 * <p>If {@link AuResponse#getDepends} is not null, the response
	 * depends on the existence of the componet returned by
	 * {@link AuResponse#getDepends}.
	 * In other words, the response is removed if the component is removed.
	 * If it is null, the response is component-independent and it is
	 * always sent to the client.
	 *
	 * <p>Unlike {@link #smartUpdate}, responses are sent even if
	 * {@link Component#invalidate()} was called.
	 * Typical examples include setting the focus, selecting the text and so on.
	 *
	 * <p>It can be called only in the request-processing and event-processing
	 * phases; excluding the redrawing phase.
	 *
	 * @since 5.0.2
	 * @see #response(String, AuResponse)
	 */
	protected void response(AuResponse response) {
		response(response.getOverrideKey(), response);
	}
	/** Causes a response to be sent to the client by overriding the key
	 * returned by {@link AuResponse#getOverrideKey}).
	 *
	 * <p>If {@link AuResponse#getDepends} is not null, the response
	 * depends on the existence of the componet returned by
	 * {@link AuResponse#getDepends}.
	 * In other words, the response is removed if the component is removed.
	 * If it is null, the response is component-independent and it is
	 * always sent to the client.
	 *
	 * <p>Unlike {@link #smartUpdate}, responses are sent even if
	 * {@link Component#invalidate()} was called.
	 * Typical examples include setting the focus, selecting the text and so on.
	 *
	 * <p>It can be called only in the request-processing and event-processing
	 * phases; excluding the redrawing phase.
	 *
	 * @param key could be anything.
	 * The second invocation of this method
	 * in the same execution with the same key and the same depends
	 * ({@link AuResponse#getDepends}) will override the previous one.
	 * However, if key is null, it won't override any other. All responses
	 * with key == null will be sent.<br/>
	 * Notice that if {@link AuResponse#getDepends} is null, then be careful
	 * of the key you used since it is shared in the same execution
	 * (rather than a particular component).
	 * @since 5.0.0 (become protected)
	 */
	protected void response(String key, AuResponse response) {
		//if response not depend on this component, it must be generated
		if (_page != null) {
			getAttachedUiEngine().addResponse(key, response);
		} else if (response.getDepends() != this) {
			final UiEngine uieng = getCurrentUiEngine();
			if (uieng != null) uieng.addResponse(key, response);
		}
	}

	/** Smart-updates a property of the peer widget associated with
	 * the component, running at the client, with the given value.
	 *
	 * <p>The second invocation with the same property will replace the previous
	 * call. In other words, the same property will be set only once in
	 * each execution. If you prefer to send both updates to the client,
	 * use {@link #smartUpdate(String, Object, boolean)} instead.
	 *
	 * <p>This method has no effect if {@link #invalidate()} is ever invoked
	 * (in the same execution), since {@link #invalidate()} assumes
	 * the whole content shall be redrawn and all smart updates to
	 * this components can be ignored,
	 *
	 * <p>Once {@link #invalidate} is called, all invocations to {@link #smartUpdate(String, Object)}
	 * will then be ignored, and {@link #redraw} will be invoked later.
	 *
	 * <p>It can be called only in the request-processing and event-processing
	 * phases; excluding the redrawing phase.
	 *
	 * <p>There are two ways to draw a component, one is to invoke
	 * {@link Component#invalidate()}, and the other is {@link #smartUpdate(String, Object)}.
	 * While {@link Component#invalidate()} causes the whole content to redraw,
	 * {@link #smartUpdate(String, Object)} let component developer control which part
	 * to redraw.
	 *
	 * @param value the new value.
	 * If it is {@link org.zkoss.zk.ui.util.DeferredValue}, the value
	 * will be retrieved (by calling {@link org.zkoss.zk.ui.util.DeferredValue#getValue})
	 * in the rendering phase. It is useful if the value can not be determined now.
	 * <p>For some old application servers (example, Webshpere 5.1),
	 * {@link Execution#encodeURL} cannot be called in the event processing
	 * thread. So, the developers have to use {@link org.zkoss.zk.ui.util.DeferredValue}
	 * or disable the use of the event processing thread
	 * (by use of <code>disable-event-thread</code> in zk.xml).
	 * <p>If you want to generate the JavaScript code directly (i.e.,
	 * the value is a valid JavaScript snippet), you can use
	 * {@link JavaScriptValue}.
	 * <p>In addition, the value can be any kind of objects that
	 * the client accepts (marshaled by JSON) (see also {@link org.zkoss.json.JSONAware}).
	 * @since 5.0.0 (become protected)
	 * @see #updateByClient
	 * @see #smartUpdate(String, Object, boolean)
	 */
	protected void smartUpdate(String attr, Object value) {
		smartUpdate(attr, value, false);
	}
	/** Smart-updates a property of the peer widget with the given value
	 * that allows caller to decide whether to append or overwrite.
	 * In other words, {@link #smartUpdate(String, Object)} is a shortcut of
	 * <code>smartUpdate(attr, value, false)</code>.
	 *
	 * <p>For example, if you invoke <code>smartUpdate("attr", "value1")</code>
	 * and <code>smartUpdate("attr", "value2")</code>, then only <code>value2</code>
	 * will be sent to the client.
	 * <p>However, if you invoke <code>smartUpdate("attr", "value1", true)</code>
	 * and <code>smartUpdate("attr", "value2", true)</code>,
	 * then both <code>value1</code> and <code>value2</code>
	 * will be sent to the client. In other words, <code>wgt.setAttr("value1")</code>
	 * and <code>wgt.setAttr("value2")</code> will be invoked at the client
	 * accordingly.
	 *
	 * @param append whether to append the updates of properties with the same
	 * name. If false, only the last value of the same property will be sent
	 * to the client.
	 * @since 5.0.0
	 * @see #smartUpdate(String, Object)
	 */
	protected void smartUpdate(String attr, Object value, boolean append) {
		if (_page != null)
			getAttachedUiEngine().addSmartUpdate(this, attr, value, append);
	}
	/** A special smart update to update a value in int.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, int value) {
		smartUpdate(attr, new Integer(value));
	}
	/** A special smart update to update a value in long.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, long value) {
		smartUpdate(attr, new Long(value));
	}
	/** A special smart update to update a value in byte.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, byte value) {
		smartUpdate(attr, new Byte(value));
	}
	/** A special smart update to update a value in character.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, char value) {
		smartUpdate(attr, new Character(value));
	}
	/** A special smart update to update a value in boolean.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, boolean value) {
		smartUpdate(attr, Boolean.valueOf(value));
	}
	/** A special smart update to update a value in float.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, float value) {
		smartUpdate(attr, new Float(value));
	}
	/** A special smart update to update a value in double.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, double value) {
		smartUpdate(attr, new Double(value));
	}
	/** A special smart update to update a value in Date.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, Date value) {
		smartUpdate(attr, (Object)value);
	}
	/** A special smart update to update an event listener for the
	 * peer widget.
	 * By default, it assumes the peer widget has a method called
	 * <code>setListener<code> and it will be invoked as follows.
	 *
	 * <pre><code>wgt.setListener([evtnm, script]);</code></pre>
	 *
	 * <p>Devices that supports it in another way have to override this
	 * method. Devices that don't support it have to override this method
	 * to throw UnsupportedOperationException.
	 *
	 * @param evtnm the event name, such as onClick
	 * @param script the script. If null, it means to remove the event
	 * listener from the peer widget
	 * @since 5.0.0
	 */
	protected void smartUpdateWidgetListener(String evtnm, String script) {
		smartUpdate("listener", new String[] {evtnm, script}, true);
	}
	/** A special smart update to update a method or a field of the peer widget.
	 * By default, it invokes the client widget's <code>setOverride</code> as follows.
	 *
	 * <pre><code>wgt.setOverride([name: script]);</code></pre>
	 *
	 * <p>Devices that supports it in another way have to override this
	 * method. Devices that don't support it have to override this method
	 * to throw UnsupportedOperationException.
	 *
	 * @param name the method name, such as setValue
	 * @param script the content of the method or field to override.
	 * Notice that it must be a valid JavaScript snippet.
	 * If null, the previous method/field override
	 * will be remove. And, the method/field defined in original widget will
	 * be restored.
	 * @since 5.0.0
	 */
	protected void smartUpdateWidgetOverride(String name, String script) {
		smartUpdate("override", new Object[] {name, new JavaScriptValue(script)}, true);
	}

	public void detach() {
		if (getParent() != null) setParent(null);
		else setPage(null);
	}

	/** Default: does nothing.
	 * @see ComponentCtrl#beforeChildAdded
	 * @since 3.6.2
	 */
	public void beforeChildAdded(Component child, Component insertBefore) {
	}
	/** Default: does nothing.
	 * @see ComponentCtrl#beforeChildRemoved
	 * @since 3.6.2
	 */
	public void beforeChildRemoved(Component child) {
	}
	/** Default: does nothing.
	 * @see ComponentCtrl#beforeParentChanged
	 * @since 3.6.2
	 */
	public void beforeParentChanged(Component parent) {
	}
	/** Default: does nothing.
	 * @see ComponentCtrl#onChildAdded
	 */
	public void onChildAdded(Component child) {
	}
	/** Default: does nothing.
	 * @see ComponentCtrl#onChildRemoved
	 */
	public void onChildRemoved(Component child) {
	}
	/** Default: handles special event listeners.
	 * @see ComponentCtrl#onPageAttached
	 * @since 3.0.0
	 */
	public void onPageAttached(Page newpage, Page oldpage) {
		if (oldpage == null) //new added
			onListenerChange(newpage.getDesktop(), true);
	}
	/** Default: handles special event listeners.
	 * @see ComponentCtrl#onPageDetached
	 * @since 3.0.0
	 */
	public void onPageDetached(Page page) {
		onListenerChange(page.getDesktop(), false);
	}

	/** Returns the widget class (aka., widget type), or null if not defined.
	 * <p>Default: return the widget class based on the current mold
	 * (by use of {@link ComponentDefinition#getWidgetClass}), or null
	 * if not found.
	 * <p>To override in Java, you could invoke {@link #setWidgetClass}.
	 * To override in ZUML, you could use the client namespace as follows.
	 * <pre><code>
&lt;window xmlns:w="http://www.zkoss.org/2005/zk/client"
w:use="foo.MyWindow"&gt;
&lt;/window&gt;
	 *</code></pre>
	 * @since 5.0.0
	 */
	public String getWidgetClass() {
		if (_auxinf != null && _auxinf.wgtcls != null)
			return _auxinf.wgtcls;
		final String widgetClass = _def.getWidgetClass(getMold());
		return widgetClass != null ? widgetClass: _def.getDefaultWidgetClass();
	}
	public void setWidgetClass(String wgtcls) {
		if (wgtcls != null && wgtcls.length() > 0) {
			initAuxInfo().wgtcls = wgtcls;
		} else if (_auxinf != null) {
			_auxinf.wgtcls = null;
		}
	}

	public String getMold() {
		final String mold = _auxinf != null ? _auxinf.mold: null;
		return mold != null ? mold: DEFAULT;
	}
	public void setMold(String mold) {
		if (mold != null && (DEFAULT.equals(mold) || mold.length() == 0))
			mold = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.mold: DEFAULT, mold)) {
			if (!_def.hasMold(mold != null ? mold: DEFAULT))
				throw new UiException("Unknown mold: "+mold+"; allowed: "+_def.getMoldNames());
			final String oldtype = getWidgetClass();
			initAuxInfo().mold = mold;
			if (Objects.equals(oldtype, getWidgetClass()))
				smartUpdate("mold", getMold());
			else
				invalidate();
		}
	}

	public boolean disableClientUpdate(boolean disable) {
		final UiEngine uieng =
			_page != null ? getAttachedUiEngine(): getCurrentUiEngine();
		return uieng != null && uieng.disableClientUpdate(this, disable);
	}

	//-- in the redrawing phase --//
	/** Redraws this component and all its decendants.
	 * <p>Default: It uses {@link JsContentRenderer} to render all information
	 * in JavaScript codes. For devices that don't support JavaScript,
	 * it must override this method.
	 * <p>To generate all information, it first invokes
	 * {@link #renderProperties} to render component's
	 * properties,
	 * and  then {@link #redrawChildren} to redraw children (and descendants)
	 * (by calling their {@link #redraw}).
	 *
	 * <p>If a dervied class wants to render more properties, it can override
	 * {@link #renderProperties}.
	 * <p>If a derived class renders only a subset of its children
	 * (such as paging/cropping), it could override {@link #redrawChildren}.
	 * <p>If a deriving class wants to do something before
	 * {@link #renderProperties}, it has to override {@link #redraw}.
	 * <p>If a deriving class doesn't want to render in JavaScript codes,
	 * it has to override {@link #redraw} with the proper implementation
	 * of {@link ContentRenderer}.
	 */
	public void redraw(final Writer out) throws IOException {
		final int order = ComponentRedraws.beforeRedraw(false);
		final boolean aupg = isAsyncUpdate();
		final String extra;
		try {
			if (order < 0)
				out.write(aupg ? "[": "zkx(");
			else if (order > 0) //not first child
				out.write(',');

			final JsContentRenderer renderer = new JsContentRenderer();
			renderProperties(renderer);

			final String wgtcls = getWidgetClass();
			if (wgtcls == null)
				throw new UiException("Widget class required for "+this+" with "+getMold());
			out.write("\n['");
			out.write(wgtcls);
			out.write("','");
			out.write(getUuid());
			out.write("',{");
			out.write(renderer.getBuffer().toString());
			out.write("},[");

			redrawChildren(out);

			out.write(']');
			final String mold = getMold();
			if (!DEFAULT.equals(mold)) {
				out.write(",'");
				out.write(mold);
				out.write('\'');
			}
			out.write(']');

		} finally {
			extra = ComponentRedraws.afterRedraw();
		}
		if (order < 0) {
			if (aupg) {
				if (extra.length() > 0) {
					out.write(",0,null,'");
					out.write(Strings.escape(extra, Strings.ESCAPE_JAVASCRIPT));
					out.write('\'');
				}
				out.write(']');
			} else {
				if (extra.length() > 0)
					out.write(",1"); //Bug 2983792 (delay until non-defer script evaluated)
				out.write(");\n");
				out.write(extra);
			}
		}
	}
	private final boolean isAsyncUpdate() {
		final Execution exec = Executions.getCurrent();
		return exec != null && exec.isAsyncUpdate(_page);
	}

	/** Redraws childrens (and then recursively descandants).
	 * <p>Default: it invokes {@link #redraw} for all its children.
	 * <p>If a derived class renders only a subset of its children
	 * (such as paging/cropping), it could override {@link #redrawChildren}.
	 * @since 5.0.0
	 * @see #redraw
	 */
	protected void redrawChildren(Writer out) throws IOException {
		final Object xc = getExtraCtrl();
		if (xc instanceof Cropper) {
			final Set crop = ((Cropper)xc).getAvailableAtClient();
			if (crop != null) {
				for (Iterator it = crop.iterator(); it.hasNext();) {
					final Component c = (Component)it.next();
					if (c.getParent() == this)
						((ComponentCtrl)c).redraw(out);
					//Note: getAvialableAtClient might return all level
					//of children in the same crop scope
				}
				return;
			}
		}
		
		for (Component child = getFirstChild(); child != null;) {
			Component next = child.getNextSibling();
			((ComponentCtrl)child).redraw(out);
			child = next;
		}
	}
	/** Called by ({@link ComponentCtrl#redraw}) to render the
	 * properties, excluding the enclosing tag and children.
	 *
	 * <p>Default: it renders {@link #getId} if it was assigned,
	 * and event names if listened (and listed in {@link #getClientEvents}).
	 *
	 * <p>Note: it doesn't render {@link #getWidgetClass}, {@link #getUuid}
	 * and {@link #getMold}, which are caller's job.
	 *
	 * @since 5.0.0
	 */
	protected void renderProperties(ContentRenderer renderer)
	throws IOException {
		render(renderer, "id", _id);
		if (_auxinf != null && !_auxinf.visible) //don't call isVisible since it might be overriden (backward compatible)
			renderer.render("visible", false);

		Boolean shallHandleImportant = null;
		for (Iterator it = getClientEvents().entrySet().iterator();
		it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String evtnm = (String)me.getKey();
			final int flags = ((Integer)me.getValue()).intValue();
			if ((flags & CE_IMPORTANT) != 0) {
				if (shallHandleImportant == null) {
					Execution exec = Executions.getCurrent();
					shallHandleImportant = Boolean.valueOf(exec != null && markImportantEvent(exec));
				}
				if (shallHandleImportant.booleanValue())
					renderer.render("$$" + evtnm, (flags & CE_NON_DEFERRABLE) != 0);
			}
			if (Events.isListened(this, evtnm, false))
				renderer.render('$' + evtnm, Events.isListened(this, evtnm, true));
					//$onClick and so on
		}

		if (_auxinf != null)
			_auxinf.render(renderer);

		Object o = getAttribute(Attributes.CLIENT_ROD);
		if (o != null)
			renderer.render("z$rod",
				(o instanceof Boolean && ((Boolean)o).booleanValue())
				|| !"false".equals(o));
	}
	/** Adds this widget class to indicate that its important events
	 * are generated
	 */
	private boolean markImportantEvent(Execution exec) {
		Set wgtcls = (Set)exec.getAttribute(IMPORTANT_EVENTS);
		if (wgtcls == null)
			exec.setAttribute(IMPORTANT_EVENTS, wgtcls = new HashSet(8));
		return wgtcls.add(getWidgetClass());
	}
	private static final String IMPORTANT_EVENTS = "org.zkoss.zk.ui.importantEvents";
	/** An utility to be called by {@link #renderProperties} to
	 * render a string-value property.
	 * It ignores if value is null or empty.
	 * If you want to render it even if null/empty, invoke
	 * {@link ContentRenderer#render(String, String)} directly.
	 * @since 5.0.0
	 */
	protected void render(ContentRenderer renderer,
	String name, String value) throws IOException {
		if (value != null && value.length() > 0)
			renderer.render(name, value);
	}
	/** An utility to be called by {@link #renderProperties} to
	 * render a string-value property.
	 * It ignores if value is null.
	 * If you want to render it even if null, invoke
	 * {@link ContentRenderer#render(String, Object)} directly.
	 * @since 5.0.0
	 */
	protected void render(ContentRenderer renderer,
	String name, Object value) throws IOException {
		if (value instanceof String)
			render(renderer, name, (String)value);
		else if (value != null)
			renderer.render(name, value);
	}
	/** An utility to be called by {@link #renderProperties} to
	 * render a boolean-value property if it is true.
	 * If you want to render it no matter true or false, use
	 * {@link ContentRenderer#render(String, boolean)} directly.
	 * @since 5.0.0
	 */
	protected void render(ContentRenderer renderer,
	String name, boolean value) throws IOException {
		if (value)
			renderer.render(name, true);
	}

	/** Returns a map of event information that the client might send to this component.
	 * The key of the returned map is a String instance representing the event name,
	 * and the value an integer representing the flags
	 * (a combination of {@link #CE_IMPORTANT}, {@link #CE_NON_DEFERRABLE}, {@link #CE_BUSY_IGNORE},
	 * {@link #CE_DUPLICATE_IGNORE} and {@link #CE_REPEAT_IGNORE}).
	 * <p>Default: return the collection of events
	 * added by {@link #getClientEvents}.
	 *
	 * <p>Rather than overriding this method, it is suggested
	 * to invoke {@link #addClientEvent} in the <code>static</code> statement.
	 * For example,
	 * <pre><code>public MyComponent extend HtmlBasedComponent {
	 *  static {
	 *    addClientEvent(MyComponent.class, "onOpen", 0);
	 *  }</code></pre>
	 *
	 * @since 5.0.0
	 */
	public Map getClientEvents() {
		for (Class cls = getClass(); cls != null; cls = cls.getSuperclass()) {
			Map events;
			synchronized (_clientEvents) {
				events = (Map)_clientEvents.get(cls);
			}
			if (events != null) return events;
		}
		return Collections.EMPTY_MAP;
	}
	/** Adds an event that the client might send to the server.
	 * It must be called when loading the class (i.e., in <code>static {}</code>).
	 * It cannot be called after that.
	 * @param cls the component's class (implementation class).
	 * @param flags a combination of {@link #CE_IMPORTANT}, {@link #CE_NON_DEFERRABLE}
	 * {@link #CE_BUSY_IGNORE}, {@link #CE_DUPLICATE_IGNORE}
	 * and {@link #CE_REPEAT_IGNORE}.
	 * @since 5.0.0
	 */
	protected static void addClientEvent(Class cls, String evtnm, int flags) {
		Map events;
		synchronized (_clientEvents) {
			events = (Map)_clientEvents.get(cls);
		}

		//It is OK to race there
		final boolean first = events == null;
		if (first) {
			//for better performance, we pack all event names of super
			//classes, though it costs more memory
			events = new HashMap(8);
			for (Class c = cls ; c != null; c = c.getSuperclass()) {
				Map evts;
				synchronized (_clientEvents) {
					evts = (Map)_clientEvents.get(c);
				}
				if (evts != null) {
					events.putAll(evts);
					break;
				}
			}
		}

		events.put(evtnm, new Integer(flags));
		
		if (first)
			synchronized (_clientEvents) {
				_clientEvents.put(cls, events);
			}
	}

	//Event//
	public boolean addEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new IllegalArgumentException("null");
		if (!Events.isValid(evtnm))
			throw new IllegalArgumentException("Invalid event name: "+evtnm);

		final boolean oldasap = Events.isListened(this, evtnm, true);

		if (initAuxInfo().listeners == null)
			_auxinf.listeners = new HashMap(8);

		List l = (List)_auxinf.listeners.get(evtnm);
		if (l != null) {
			for (Iterator it = l.iterator(); it.hasNext();) {
				final EventListener li = (EventListener)it.next();
				if (listener.equals(li))
					return false;
			}
		} else {
			_auxinf.listeners.put(evtnm, l = new LinkedList());
		}
		l.add(listener);

		final Desktop desktop = getDesktop();
		if (desktop != null) {
			if (Events.ON_CLIENT_INFO.equals(evtnm)) {
				response(new AuClientInfo(desktop));
			} else if (Events.ON_PIGGYBACK.equals(evtnm)) {
				((DesktopCtrl)desktop).onPiggybackListened(this, true);
			} else if (getClientEvents().containsKey(evtnm)) {
				final boolean asap = Events.isListened(this, evtnm, true);
				if (l.size() == 1 || oldasap != asap)
					smartUpdate("$" + evtnm, asap);
			}
		}
		return true;
	}
	public boolean removeEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new IllegalArgumentException("null");

		if (_auxinf != null && _auxinf.listeners != null) {
			final boolean oldasap = Events.isListened(this, evtnm, true);
			final List l = (List)_auxinf.listeners.get(evtnm);
			if (l != null) {
				for (Iterator it = l.iterator(); it.hasNext();) {
					final EventListener li = (EventListener)it.next();
					if (listener.equals(li)) {
						it.remove();
						if (l.isEmpty())
							_auxinf.listeners.remove(evtnm);

						final Desktop desktop = getDesktop();
						if (desktop != null) {
							onListenerChange(desktop, false);

							if (getClientEvents().containsKey(evtnm)) {
								if (l.isEmpty() && !Events.isListened(this, evtnm, false))
									smartUpdate("$" + evtnm, (Object)null); //no listener at all
								else if (oldasap != Events.isListened(this, evtnm, true))
									smartUpdate("$" + evtnm, !oldasap);
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean addForward(
	String orgEvent, Component target, String targetEvent) {
		return addForward0(orgEvent, target, targetEvent, null);
	}
	public boolean addForward(
	String orgEvent, String targetPath, String targetEvent) {
		return addForward0(orgEvent, targetPath, targetEvent, null);
	}
	public boolean addForward(
	String orgEvent, Component target, String targetEvent, Object eventData) {
		return addForward0(orgEvent, target, targetEvent, eventData);
	}
	public boolean addForward(
	String orgEvent, String targetPath, String targetEvent, Object eventData) {
		return addForward0(orgEvent, targetPath, targetEvent, eventData);
	}
	/**
	 * @param target the target. It is either a component, or a string,
	 * which is used internal for implementing {@link #writeObject}
	 */
	private boolean addForward0(
	String orgEvent, Object target, String targetEvent, Object eventData) {
		if (orgEvent == null)
			orgEvent = "onClick";
		else if (!Events.isValid(orgEvent))
			throw new IllegalArgumentException("Illegal event name: "+orgEvent);
		if (targetEvent == null)
			targetEvent = orgEvent;
		else if (!Events.isValid(targetEvent))
			throw new IllegalArgumentException("Illegal event name: "+targetEvent);

		if (initAuxInfo().forwards == null)
			_auxinf.forwards = new HashMap(4);

		Object[] info = (Object[])_auxinf.forwards.get(orgEvent);
		final List fwds;
		if (info != null) {
			fwds = (List)info[1];
			for (Iterator it = fwds.iterator(); it.hasNext();) {
				final Object[] fwd = (Object[])it.next();
				if (Objects.equals(fwd[0], target)
				&& Objects.equals(fwd[1], targetEvent)) { //found
					if (Objects.equals(fwd[2], eventData)) {
						return false;
					} else {
						fwd[2] = eventData;
						return true;
					}
				}
			}
		} else {
			final ForwardListener listener = new ForwardListener(orgEvent);
			addEventListener(orgEvent, listener);
			info = new Object[] {listener, fwds = new LinkedList()};
			_auxinf.forwards.put(orgEvent, info);
		}

		fwds.add(new Object[] {target, targetEvent, eventData});
		return true;
	}
	public boolean removeForward(
	String orgEvent, Component target, String targetEvent) {
		return removeForward0(orgEvent, target, targetEvent);
	}
	public boolean removeForward(
	String orgEvent, String targetPath, String targetEvent) {
		return removeForward0(orgEvent, targetPath, targetEvent);
	}
	private boolean removeForward0(
	String orgEvent, Object target, String targetEvent) {
		if (_auxinf != null && _auxinf.forwards != null) {
			final Object[] info = (Object[])_auxinf.forwards.get(orgEvent);
			if (info != null) {
				final List fwds = (List)info[1];
				for (Iterator it = fwds.iterator(); it.hasNext();) {
					final Object[] fwd = (Object[])it.next();
					if (Objects.equals(fwd[1], targetEvent)
					&& Objects.equals(fwd[0], target)) { //found
						it.remove(); //remove it

						if (fwds.isEmpty()) { //no more event
							_auxinf.forwards.remove(orgEvent);
							removeEventListener(
								orgEvent, (EventListener)info[0]);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	/** @deprecated As of release 5.0.0, use {@link #getAttribute},
	 * {@link #setAttribute} instead.
	 */
	public Namespace getNamespace() {
		if (this instanceof IdSpace)
			return _auxinf.spaceInfo.ns;

		final IdSpace idspace = getSpaceOwner();
		return idspace instanceof Page ? ((Page)idspace).getNamespace():
			idspace == null ? null: ((Component)idspace).getNamespace();
	}

	public boolean isListenerAvailable(String evtnm, boolean asap) {
		if (_auxinf != null && _auxinf.listeners != null) {
			final List l = (List)_auxinf.listeners.get(evtnm);
			if (l != null) {
				if (!asap)
					return !l.isEmpty();

				for (Iterator it = l.iterator(); it.hasNext();) {
					final EventListener li = (EventListener)it.next();
					if (!(li instanceof Deferrable)
					|| !(((Deferrable)li).isDeferrable()))
						return true;
				}
			}
		}
		return false;
	}
	public Iterator getListenerIterator(String evtnm) {
		if (_auxinf != null && _auxinf.listeners != null) {
			final List l = (List)_auxinf.listeners.get(evtnm);
			if (l != null)
				return new ListenerIterator(l);
		}
		return CollectionsX.EMPTY_ITERATOR;
	}

	public void applyProperties() {
		_def.applyProperties(this);
	}

	public ComponentDefinition getDefinition() {
		return _def;
	}

	//-- ComponentCtrl --//
	public void setDefinition(ComponentDefinition compdef) {
		if (compdef == null)
			throw new IllegalArgumentException("null");
		if (!compdef.isInstance(this))
			throw new IllegalArgumentException("Incompatible "+compdef+" for "+this);
		_def = compdef;
	}
	public void setDefinition(String name) {
		final Execution exec = Executions.getCurrent();
		if (exec != null) {
			final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
			final PageDefinition pgdef = execCtrl.getCurrentPageDefinition();
			final Page page = execCtrl.getCurrentPage();

			ComponentDefinition compdef =
				pgdef != null ? pgdef.getComponentDefinition(name, true):
				page != null ? 	page.getComponentDefinition(name, true): null;
			if (compdef == null)
				compdef = getDefinitionByDeviceType(exec.getDesktop().getDeviceType(), name);
			if (compdef != null) {
				setDefinition(compdef);
				return;
			}
		} else {
			for (Iterator it = LanguageDefinition.getDeviceTypes().iterator(); it.hasNext();) {
				final ComponentDefinition compdef =
					getDefinitionByDeviceType((String)it.next(), name);
				if (compdef != null) {
					setDefinition(compdef);
					return;
				}
			}
		}
		throw new ComponentNotFoundException(name+" not found");
	}

	public ZScript getEventHandler(String evtnm) {
		final EventHandler evthd = _auxinf != null && _auxinf.evthds != null ?
			_auxinf.evthds.get(this, evtnm): null;
		return evthd != null ? evthd.getZScript(): null;
	}
	public void addSharedEventHandlerMap(EventHandlerMap evthds) {
		if (evthds != null && !evthds.isEmpty()) {
			unshareEventHandlerMap(false);
			if (initAuxInfo().evthds == null) {
				_auxinf.evthds = evthds;
				_auxinf.evthdsShared = true;
			} else {
				_auxinf.evthds.addAll(evthds);
			}

			final Desktop desktop = getDesktop();
			if (desktop != null)
				onListenerChange(desktop, true);
		}
	}
	public Set getEventHandlerNames() {
		return _auxinf != null && _auxinf.evthds != null ?
			_auxinf.evthds.getEventNames(): Collections.EMPTY_SET;
	}
	private void onListenerChange(Desktop desktop, boolean listen) {
		if (listen) {
			if (Events.isListened(this, Events.ON_CLIENT_INFO, false)) //asap+deferrable
				response(new AuClientInfo(desktop));
				//We always fire event not a root, since we don't like to
				//check when setParent or setPage is called
			if (Events.isListened(this, Events.ON_PIGGYBACK, false))
				((DesktopCtrl)desktop).onPiggybackListened(this, true);
		} else {
			if (!Events.isListened(this, Events.ON_PIGGYBACK, false))
				((DesktopCtrl)desktop).onPiggybackListened(this, false);
		}
	}
	public void addEventHandler(String name, EventHandler evthd) {
		if (name == null || evthd == null)
			throw new IllegalArgumentException("name and evthd required");

		unshareEventHandlerMap(true);
		_auxinf.evthds.add(name, evthd);
	}
	/** Clones the shared event handlers, if shared.
	 * @param autocreate whether to create an event handler map if not available.
	 */
	private void unshareEventHandlerMap(boolean autocreate) {
		if (_auxinf != null && _auxinf.evthdsShared) {
			_auxinf.evthds = (EventHandlerMap)_auxinf.evthds.clone();
			_auxinf.evthdsShared = false;
		} else if (autocreate && initAuxInfo().evthds == null) {
			_auxinf.evthds = new EventHandlerMap();
		}
	}

	public Annotation getAnnotation(String annotName) {
		return _auxinf != null && _auxinf.annots != null ?
			_auxinf.annots.getAnnotation(annotName): null;
	}
	public Annotation getAnnotation(String propName, String annotName) {
		return _auxinf != null && _auxinf.annots != null ?
			_auxinf.annots.getAnnotation(propName, annotName): null;
	}
	public Collection getAnnotations() {
		return _auxinf != null && _auxinf.annots != null ?
			_auxinf.annots.getAnnotations(): Collections.EMPTY_LIST;
	}
	public Collection getAnnotations(String propName) {
		return _auxinf != null && _auxinf.annots != null ?
			_auxinf.annots.getAnnotations(propName): Collections.EMPTY_LIST;
	}
	public List getAnnotatedPropertiesBy(String annotName) {
		return _auxinf != null && _auxinf.annots != null ?
			_auxinf.annots.getAnnotatedPropertiesBy(annotName): Collections.EMPTY_LIST;
	}
	public List getAnnotatedProperties() {
		return _auxinf != null && _auxinf.annots != null ?
			_auxinf.annots.getAnnotatedProperties(): Collections.EMPTY_LIST;
	}
	public void addSharedAnnotationMap(AnnotationMap annots) {
		if (annots != null && !annots.isEmpty()) {
			unshareAnnotationMap(false);
			if (initAuxInfo().annots == null) {
				_auxinf.annots = annots;
				_auxinf.annotsShared = true;
			} else {
				_auxinf.annots.addAll(annots);
			}
		}
	}
	public void addAnnotation(String annotName, Map annotAttrs) {
		unshareAnnotationMap(true);
		_auxinf.annots.addAnnotation(annotName, annotAttrs);
	}
	public void addAnnotation(String propName, String annotName, Map annotAttrs) {
		unshareAnnotationMap(true);
		_auxinf.annots.addAnnotation(propName, annotName, annotAttrs);
	}
	/** Clones the shared annotations, if shared.
	 * @param autocreate whether to create an annotation map if not available.
	 */
	private void unshareAnnotationMap(boolean autocreate) {
		if (_auxinf != null && _auxinf.annotsShared) {
			_auxinf.annots = (AnnotationMap)_auxinf.annots.clone();
			_auxinf.annotsShared = false;
		} else if (autocreate && initAuxInfo().annots == null) {
			_auxinf.annots = new AnnotationMap();
		}
	}

	public void sessionWillPassivate(Page page) {
		if (_auxinf != null && _auxinf.attrs != null) {
			willPassivate(_auxinf.attrs.getAttributes().values());
			willPassivate(_auxinf.attrs.getListeners());

			if (this instanceof IdSpace) {
			//backward compatible (we store variables in attributes)
				for (Iterator it = _auxinf.attrs.getAttributes().values().iterator();
				it.hasNext();) {
					final Object val = it.next();
					if (val instanceof NamespaceActivationListener) //backward compatible
						((NamespaceActivationListener)val).willPassivate(_auxinf.spaceInfo.ns);
				}
			}
		}

		if (_auxinf != null && _auxinf.listeners != null)
			for (Iterator it = _auxinf.listeners.values().iterator(); it.hasNext();)
				willPassivate((Collection)it.next());

		for (AbstractComponent p = (AbstractComponent)getFirstChild();
		p != null; p = p._next)
			p.sessionWillPassivate(page); //recursive
	}

	public void sessionDidActivate(Page page) {
		_page = page;

		if (_auxinf != null && _auxinf.attrs != null) {
			didActivate(_auxinf.attrs.getAttributes().values());
			didActivate(_auxinf.attrs.getListeners());
			if (_parent == null)
				_auxinf.attrs.notifyParentChanged(_page);

			if (this instanceof IdSpace) {
			//backward compatible (we store variables in attributes)
				for (Iterator it = _auxinf.attrs.getAttributes().values().iterator();
				it.hasNext();) {
					final Object val = it.next();
					if (val instanceof NamespaceActivationListener) //backward compatible
						((NamespaceActivationListener)val).didActivate(_auxinf.spaceInfo.ns);
				}
			}
		}

		if (_auxinf != null && _auxinf.listeners != null)
			for (Iterator it = _auxinf.listeners.values().iterator(); it.hasNext();)
				didActivate((Collection)it.next());

		for (AbstractComponent p = (AbstractComponent)getFirstChild();
		p != null; p = p._next)
			p.sessionDidActivate(page); //recursive
	}
	/** Utility to invoke {@link ComponentActivationListener#willPassivate}
	 * for each object in the collection.
	 * @param c a collection of objects. Ignored if null.
	 * @since 3.6.4
	 */
	protected void willPassivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willPassivate(it.next());
	}
	/** Utility to invoke {@link ComponentActivationListener#willPassivate}
	 * for the specified object.
	 * @param o the object to invoke. Ignore if 
	 * ComponentActivationListener not implemented or null.
	 * @since 3.6.4
	 */
	protected void willPassivate(Object o) {
		if (o instanceof ComponentActivationListener)
			((ComponentActivationListener)o).willPassivate(this);
	}
	/** Utility to invoke {@link ComponentActivationListener#didActivate}
	 * for each object in the collection.
	 * @param c a collection of objects. Ignored if null.
	 * @since 3.6.4
	 */
	protected void didActivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didActivate(it.next());
	}
	/** Utility to invoke {@link ComponentActivationListener#didActivate}
	 * for the specified object.
	 * @param o the object to invoke. Ignore if 
	 * ComponentActivationListener not implemented or null.
	 * @since 3.6.4
	 */
	protected void didActivate(Object o) {
		if (o instanceof ComponentActivationListener)
			((ComponentActivationListener)o).didActivate(this);
	}

	/** Returns the extra controls that tell ZK how to handle this component
	 * specially.
	 * It is used only by component developers.
	 *
	 * <p>Default: null.
	 *
	 * @see ComponentCtrl#getExtraCtrl
	 */
	public Object getExtraCtrl() {
		return newExtraCtrl(); //backward compatible; don't return null directly
			//3.0.3: create as late as possible so component has a chance
			//to customize which object to instantiate
	}
	/**
	 * Used by {@link #getExtraCtrl} to create extra controls.
	 * It is used only by component developers.
	 *
	 * <p>Default: return null.
	 *
	 * <p>To provide extra controls, it is simpler to override this method
	 * instead of {@link #getExtraCtrl}.
	 * By use of {@link #newExtraCtrl}, you don't need to care of
	 * cloning and serialization.
	 * @deprecated As of release 5.0.4, override {@link #getExtraCtrl} instead.
	 */
	protected Object newExtraCtrl() {
		return null;
	}

	/** Notifies that an {@link WrongValueException} instance is thrown,
	 * and {@link WrongValueException#getComponent} is this component.
	 * It is a callback and the component can store the error message,
	 * show up the custom information, or even 'eat' the exception.
	 *
	 * <p>Default: does nothing but returns ex.
	 *
	 * @param ex the exception being thrown (never null)
	 * @return the exception to throw, or null to ignore the exception
	 * In most cases, just return ex
	 * @since 2.4.0
	 */
	public WrongValueException onWrongValue(WrongValueException ex) {
		return ex;
	}

	public AuService getAuService() {
		return _auxinf != null ? _auxinf.ausvc: null;
	}
	public void setAuService(AuService ausvc) {
		if (ausvc != null)
			initAuxInfo().ausvc = ausvc;
		else if (_auxinf != null)
			_auxinf.ausvc = null;
	}

	/** Handles an AU request. It is invoked internally.
	 *
	 * <p>Default: it handles echo and setAttr, and it convests other request
	 * to an event (by {@link Event#getEvent}) and then posts the event
	 * (by {@link Events#postEvent}).
	 *
	 * <p>Application developer can plug the custom service to handle
	 * the AU request by {@link #setAuService}.
	 * @since 5.0.0
	 * @see #setAuService
	 */
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if ("echo".equals(cmd)) {
			final List data2 = (List)request.getData().get("");
			Events.postEvent(new Event((String)data2.get(0), this, data2.size() > 1 ? data2.get(1) : null));
		} else if ("setAttr".equals(cmd)) {
			final List data2 = (List)request.getData().get("");
			updateByClient((String)data2.get(0), data2.get(1));
		} else
			Events.postEvent(Event.getEvent(request));
	}

	/** Called when the widget running at the client asks the server
	 * to update a value (with an AU request named <code>setAttr</code>).
	 *
	 * <p>By default, it uses reflection to find out the setter to update
	 * the value. Nothing happens if the method is not found.
	 * You can override it if necessary.
	 *
	 * <p>Notice: this method will invoke {@link #disableClientUpdate} to
	 * disable any update to the client, when calling the setter
	 *
	 * <p>See also <a href="http://docs.zkoss.org/wiki/Zk.Widget#smartUpdate">zk.Widget.smartUpdate()</a>.
	 * @since 5.0.0
	 */
	protected void updateByClient(String name, Object value) {
		Method m;
		Object[] args = new Object[] {value};
		try {
			m = Classes.getMethodByObject(getClass(),
				Classes.toMethodName(name, "set"), args);
		} catch (NoSuchMethodException ex) {
			if (log.debugable()) log.debug("setter not found", ex);
			return; //ingore it
		}

		disableClientUpdate(true);
		try {
			m.invoke(this, args);
		} catch (Throwable ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			disableClientUpdate(false);
		}
	}

	//-- Object --//
	public String toString() {
		final String clsnm = getClass().getName();
		final int j = clsnm.lastIndexOf('.');
		return "<"+clsnm.substring(j+1)+' '+(_id.length() > 0  ? _id: _uuid)+'>';
	}
	public boolean equals(Object o) { //no more override
		return this == o;
	}

	/** Holds info shared of the same ID space. */
	private class SpaceInfo {
		private NS ns = new NS();
		/** A map of ((String id, Component fellow). */
		private Map fellows = new HashMap(32);
	}
	/** @deprecated */
	private class NS implements Namespace {
		//Namespace//
		public Component getOwner() {
			return AbstractComponent.this;
		}
		public Page getOwnerPage() {
			return AbstractComponent.this._page;
		}
		public Set getVariableNames() {
			return AbstractComponent.this.getAttributes().keySet();
		}
		public boolean containsVariable(String name, boolean local) {
			return hasAttributeOrFellow(name, !local)
				|| (!local && getXelVariable(name) != null);
		}
		public Object getVariable(String name, boolean local) {
			Object o = getAttributeOrFellow(name, !local);
			return o != null || local ? o: getXelVariable(name);
		}
		private Object getXelVariable(String name) {
			Page page = getOwnerPage();
			return page != null ? page.getXelVariable(null, null, name, true): null;
		}
		public void setVariable(String name, Object value, boolean local) {
			setAttribute(name, value, !local);
		}
		public void unsetVariable(String name, boolean local) {
			removeAttribute(name, !local);
		}

		/** @deprecated */
		public Namespace getParent() {
			final IdSpace owner = getSpaceOwnerOfParent(AbstractComponent.this);
			return owner instanceof Component ? ((Component)owner).getNamespace():
				owner instanceof Page ? ((Page)owner).getNamespace(): null;
		}
		/** @deprecated */
		public void setParent(Namespace parent) {
			throw new UnsupportedOperationException();
		}
		/** @deprecated */
		public boolean addChangeListener(NamespaceChangeListener listener) {
			return false;
		}
		/** @deprecated */
		public boolean removeChangeListener(NamespaceChangeListener listener) {
			return false;
		}
	}

	private class ChildIter implements ListIterator  {
		private AbstractComponent _p, _lastRet;
		private int _j;
		private int _modCntSnap;

		private ChildIter(int index) {
			int nChild;
			if (index < 0 || index > (nChild = nChild()))
				throw new IndexOutOfBoundsException("Index: "+index+", Size: "+nChild());

			if (index < (nChild >> 1)) {
				_p = _chdinf.first;
				for (_j = 0; _j < index; _j++)
					_p = _p._next;
			} else {
				_p = null; //means the end of the list
				for (_j = nChild; _j > index; _j--)
					_p = _p != null ? _p._prev: _chdinf.last;
			}

			_modCntSnap = modCntChd();
		}
		public boolean hasNext() {
			checkComodification();
			return _j < nChild();
		}
		public Object next() {
			if (_j >= nChild())
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
		public Object previous() {
			if (_j <= 0)
				throw new java.util.NoSuchElementException();
			checkComodification();

			_lastRet = _p = _p != null ? _p._prev: _chdinf.last;
			_j--;
			return _lastRet;
		}
		private void checkComodification() {
			if (modCntChd() != _modCntSnap)
				throw new java.util.ConcurrentModificationException();
		}
		public int nextIndex() {
			return _j;
		}
		public int previousIndex() {
			return _j - 1;
		}
		public void add(Object o) {
			final Component newChild = (Component)o;
			if (newChild.getParent() == AbstractComponent.this)
				throw new UnsupportedOperationException("Unable to add component with the same parent: "+o);
				//1. it is confusing to allow adding (with replace)
				//2. the code is sophisticated
			checkComodification();

			insertBefore(newChild, _p);
			++_j;
			_lastRet = null;
				//spec: cause remove to throw ex if no next/previous
			++_modCntSnap;
				//don't assign modCntChd directly since deriving class
				//might manipulate others in insertBefore
		}
		public void remove() {
			if (_lastRet == null)
				throw new IllegalStateException();
			checkComodification();

			if (_p == _lastRet) _p = _lastRet._next; //previous was called
			else --_j; //next was called

			removeChild(_lastRet);
			
			_lastRet = null;
			++_modCntSnap;
		}
		public void set(Object o) {
			throw new UnsupportedOperationException();
				//Possible to implement this but confusing to developers
				//if o has the same parent (since we have to move)
		}
	}

	//Cloneable//
	public Object clone() {
		final AbstractComponent clone;
		try {
			clone = (AbstractComponent)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}

		//1. make it not belonging to any page
		clone._page = null;
		clone._parent = null;

		//2. clone AuxInfo
		if (_auxinf != null)
			clone._auxinf = (AuxInfo)clone._auxinf.clone();

		//3. clone children (deep cloning)
		if (_chdinf != null) {
			clone._chdinf = _chdinf.clone(clone);

			//child's attrs's notification
			for (AbstractComponent p = clone._chdinf.first;
			p != null; p = p._next)
				if (p._auxinf != null && p._auxinf.attrs != null)
					p._auxinf.attrs.notifyParentChanged(clone);
		}

		//4. init AuxInfo
		if (_auxinf != null)
			_auxinf.initClone(clone, clone._auxinf);

		return clone;
	}
	private Object willClone(ComponentCloneListener val) {
		try {
			return val.willClone(this);
		} catch (AbstractMethodError ex) { //backward compatible prior to 5.0
			try {
				final Method m = val.getClass().getMethod(
					"clone", new Class[] {Component.class});
				Fields.setAccessible(m, true);
				return m.invoke(val, new Object[] {this});
			} catch (Exception t) {
				throw UiException.Aide.wrap(t);
			}
		}
	}
	private void cloneSpaceInfoFrom(SpaceInfo from) {
		//rebuild ID space by binding itself and all children
		if (!isAutoId(_id))
			this.bindToIdSpace(this);
		for (AbstractComponent p = (AbstractComponent)getFirstChild();
		p != null; p = p._next)
			addToIdSpacesDown(p, this);
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		//No need to unshare annots and evthds, since stored as an independent copy

		s.defaultWriteObject();

		//write definition
		if (_def == ComponentsCtrl.DUMMY) {
			s.writeObject(null);
		} else {
			LanguageDefinition langdef = _def.getLanguageDefinition();
			if (langdef != null) {
				s.writeObject(langdef.getName());
				s.writeObject(_def.getName());
			} else {
				s.writeObject(_def);
			}
		}

		//write children
		for (AbstractComponent p = (AbstractComponent)getFirstChild();
		p != null; p = p._next)
			s.writeObject(p);
		s.writeObject(null);

		//write auxinf if necessary
		if (_auxinf == null)
			return;

		//write attrs
		if (_auxinf.attrs != null) {
			final Map attrmap = _auxinf.attrs.getAttributes();
			willSerialize(attrmap.values());
			final List attrlns = _auxinf.attrs.getListeners();
			willSerialize(attrlns);

			Serializables.smartWrite(s, attrmap);
			Serializables.smartWrite(s, attrlns);
		} else {
			Serializables.smartWrite(s, (Map)null);
			Serializables.smartWrite(s, (List)null);
		}

		if (_auxinf.listeners != null)
			for (Iterator it = _auxinf.listeners.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				s.writeObject(me.getKey());

				final Collection ls = (Collection)me.getValue();
				willSerialize(ls);
				Serializables.smartWrite(s, ls);
			}
		s.writeObject(null);

		willSerialize(_auxinf.ausvc);
		s.writeObject(_auxinf.ausvc != null
			&& (_auxinf.ausvc instanceof java.io.Serializable
			|| _auxinf.ausvc instanceof java.io.Externalizable) ?
				_auxinf.ausvc: null);
	}
	/** Utility to invoke {@link ComponentSerializationListener#willSerialize}
	 * for each object in the collection.
	 * @param c a collection of objects. Ignored if null.
	 * @since 3.6.4
	 */
	protected void willSerialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willSerialize(it.next());
	}
	/** Utility to invoke {@link ComponentSerializationListener#willSerialize}
	 * for the specified object.
	 * @param o the object to invoke. Ignore if 
	 * ComponentSerializationListener not implemented or null.
	 * @since 3.6.4
	 */
	protected void willSerialize(Object o) {
		if (o instanceof ComponentSerializationListener)
			((ComponentSerializationListener)o).willSerialize(this);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		//read definition
		Object def = s.readObject();
		if (def instanceof String) {
			LanguageDefinition langdef = null;
			try {
				langdef = LanguageDefinition.lookup((String)def);
			} catch (DefinitionNotFoundException ex) {
			} 
			if (langdef != null) {
				_def = langdef.getComponentDefinitionIfAny((String)s.readObject());
				//don't throw exception since some might not be associated
				//with a definition (e.g., JSP's native and page components)
			} else {
				s.readObject(); //ignore the component name
				_def = null;
			}
		} else {
			_def = (ComponentDefinition)def;
		}
		if (_def == null)
			_def = ComponentsCtrl.DUMMY;

		//read children
		for (AbstractComponent q = null;;) {
			final AbstractComponent child = (AbstractComponent)s.readObject();
			if (child == null) {
				if (_chdinf != null)
					_chdinf.last = q;
				break; //no more
			}
			++initChildInfo().nChild;
			if (q != null) q._next = child;
			else _chdinf.first = child;
			child._prev = q;
			child._parent = this;
			q = child;
		}

		//Read auxinf
		if (_auxinf == null)
			return;

		//read attrs
		attrs();
		final Map attrmap = _auxinf.attrs.getAttributes();
		Serializables.smartRead(s, attrmap);
		final List attrlns = _auxinf.attrs.getListeners();
		Serializables.smartRead(s, attrlns);
		if (attrmap.isEmpty() && attrlns.isEmpty())
			_auxinf.attrs = null;
		else if (_parent != null)
			_auxinf.attrs.notifyParentChanged(_parent);

		for (;;) {
			final String evtnm = (String)s.readObject();
			if (evtnm == null) break; //no more

			if (_auxinf.listeners == null) _auxinf.listeners = new HashMap(4);
			final Collection ls = Serializables.smartRead(s, (Collection)null);
			_auxinf.listeners.put(evtnm, ls);
		}

		//restore _auxinf.spaceInfo
		if (this instanceof IdSpace) {
			_auxinf.spaceInfo = new SpaceInfo();

			//restore ID space by binding itself and all children
			if (!isAutoId(_id))
				bindToIdSpace(this);
			for (AbstractComponent ac = (AbstractComponent)getFirstChild();
			ac != null; ac = ac._next)
				addToIdSpacesDown(ac, this);
		}

		//didDeserialize
		didDeserialize(attrmap.values());
		didDeserialize(attrlns);
		if (_auxinf.listeners != null)
			for (Iterator it = _auxinf.listeners.values().iterator(); it.hasNext();)
				didDeserialize((Collection)it.next());
		didDeserialize(_auxinf.ausvc = (AuService)s.readObject());
	}
	/** Utility to invoke {@link ComponentSerializationListener#didDeserialize}
	 * for each object in the collection.
	 * @param c a collection of objects. Ignored if null.
	 * @since 3.6.4
	 */
	protected void didDeserialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didDeserialize(it.next());
	}
	/** Utility to invoke {@link ComponentSerializationListener#didDeserialize}
	 * for the specified object.
	 * @param o the object to invoke. Ignore if 
	 * ComponentSerializationListener not implemented or null.
	 * @since 3.6.4
	 */
	protected void didDeserialize(Object o) {
		if (o instanceof ComponentSerializationListener)
			((ComponentSerializationListener)o).didDeserialize(this);
	}

	/** Used to forward events (for the forward conditions).
	 */
	private class ForwardListener
	implements EventListener, ComponentCloneListener, java.io.Serializable {
	//Note: it is not serializable since it is handled by
	//AbstractComponent.writeObject

		private final String _orgEvent;
		private ForwardListener(String orgEvent) {
			_orgEvent = orgEvent;
		}

		public void onEvent(Event event) {
			final Object[] info = (Object[])_auxinf.forwards.get(_orgEvent);
			if (info != null)
				for (Iterator it = new ArrayList((List)info[1]).iterator();
				it.hasNext();) {
					final Object[] fwd = (Object[])it.next();
					Component target = resolveForwardTarget(fwd[0]);
					if (target == null) {
						final IdSpace owner = getSpaceOwner();
						if (owner instanceof Component) {
							target = (Component)owner;
						} else {
							//Use the root component instead
							for (target = AbstractComponent.this;;) {
								final Component p = target.getParent();
								if (p == null)
									break;
								target = p;
							}
						}
					}

					//bug #2790393 Forward event listener shall be called immediately
					//(since 3.6.2) change from postEvent to sendEvent to
					//make forward event deterministic
					Events.sendEvent(
						new ForwardEvent((String)fwd[1], target, event, fwd[2]));
				}
		}

		//ComponentCloneListener//
		public Object willClone(Component comp) {
			return null; //handle by AbstractComponent.clone
		}
	}
	private Component resolveForwardTarget(Object fwd) {
		return fwd instanceof String ?
			Components.pathToComponent((String)fwd, this): (Component)fwd;
	}

	private static final String NONE = "";
	private static String _id2uuidPrefix = NONE, _id2uuidPrefix2;
	private static int _id2uuidPageOfs;
	private static String id2Uuid(String id) {
		if (id.length() > 0) {
			if (_id2uuidPrefix == NONE) {
				_id2uuidPrefix = Library.getProperty(Attributes.ID_TO_UUID_PREFIX);
				if (_id2uuidPrefix != null) {
					Library.setProperty(Attributes.UUID_RECYCLE_DISABLED, "true"); //disable it

					_id2uuidPageOfs = _id2uuidPrefix.indexOf("${page}");
					if (_id2uuidPageOfs >= 0) {
						_id2uuidPrefix2 = _id2uuidPrefix.substring(_id2uuidPageOfs + 7);
						_id2uuidPrefix = _id2uuidPrefix.substring(0, _id2uuidPageOfs);
					}
				}
			}
			if (_id2uuidPrefix != null) {
				if (_id2uuidPageOfs >= 0) {
					final ExecutionCtrl execCtrl = (ExecutionCtrl)Executions.getCurrent();
					if (execCtrl != null) {
						final Page page = execCtrl.getCurrentPage();
						if (page != null)
							return _id2uuidPrefix + page.getId() + _id2uuidPrefix2 + id;
					}
				}
				return _id2uuidPrefix + id;
			}
		}
		return null;
	}

	/** Returns the default mold for the given class.
	 * <p>Default: check the library property called xxx.mold, where xxx is
	 * the name of the give class. If not found or empty, "default" is assumed.
	 * <p>Subclass might override this method to use the default mold of the base
	 * class, such as
	 * <pre><code>
	 *protected String getDefaultMold(Class klass) {
	 *   return super.getDefaultMold(Button.class);
	 *}</code></pre>
	 * @since 5.0.3
	 */
	protected String getDefaultMold(Class klass) {
		return (String)getDefaultInfo(klass);
	}
	private static Object getDefaultInfo(Class klass) { //use Object for future extension
		Object inf = _infs.get(klass);
		if (inf == null) {
			synchronized (_sinfs) {
				inf = _sinfs.get(klass);
				if (inf == null) {
					String mold = Library.getProperty(klass.getName() + ".mold");
					inf = mold != null && mold.length() > 0 ? mold: DEFAULT;
					_sinfs.put(klass, inf);
				}
				if (++_infcnt > 100 || _sinfs.size() > 20) {
					_infcnt = 0;
					Map infs = new HashMap(_infs);
					infs.putAll(_sinfs);
					_infs = infs;
					_sinfs.clear();
				}
			}
		}
		return inf;
	}
	private static transient Map _infs = new HashMap(), //readonly
		_sinfs = new HashMap(); //synchronized
	private static int _infcnt;

	private final AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}
	/** Merge multiple memembers into an single object (and create on demand)
	 * to minimize the footprint
	 * @since 5.0.4
	 */
	private static class AuxInfo implements java.io.Serializable, Cloneable {
		/** The mold. */
		private String mold;

		/** The info of the ID space, or null if IdSpace is NOT implemented. */
		private transient SpaceInfo spaceInfo;
		/** Component attributes. */
		private transient SimpleScope attrs;
		/** A map of event listener: Map(evtnm, List(EventListener)). */
		private transient Map listeners;

		/** A map of annotations. Serializable since a component might have
		 * its own annotations.
		 */
		private AnnotationMap annots;
		/** A map of event handler to handle events. */
		private EventHandlerMap evthds;
		/** A map of forward conditions:
		 * Map(String orgEvt, [listener, List([target or targetPath,targetEvent])]).
		 */
		private Map forwards;

		/** the Au service. */
		private transient AuService ausvc;

		/** The widget class. */
		private String wgtcls;
		/** A map of client event hanlders, Map(String evtnm, String script). */
		private Map wgtlsns;
		/** A map of client properties to override, Map(String name, String script). */
		private Map wgtovds;
		/** A map of client DOM attributes to set, Map(String name, String value). */
		private Map wgtattrs;

		/** Whether annots is shared with other components. */
		private transient boolean annotsShared;
		/** Whether evthds is shared with other components. */
		private transient boolean evthdsShared;
		/** Whether this component is visible.
		 */
		private boolean visible = true;

		public Object clone() {
			final AuxInfo clone;
			try {
				clone = (AuxInfo)super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
			if (wgtlsns != null)
				clone.wgtlsns = new LinkedHashMap(wgtlsns);
			if (wgtovds != null)
				clone.wgtovds = new LinkedHashMap(wgtovds);
			if (wgtattrs != null)
				clone.wgtattrs = new LinkedHashMap(wgtattrs);

			//clone annotation and event handlers
			if (!annotsShared && annots != null)
				clone.annots = (AnnotationMap)annots.clone();
			if (!evthdsShared && evthds != null)
				clone.evthds = (EventHandlerMap)evthds.clone();
			return clone;
		}
		/** 2nd phase of clone (after children are cloned). */
		private void initClone(AbstractComponent owner, AuxInfo clone) {
			//spaceinfo (after children is cloned)
			if (spaceInfo != null) {
				clone.spaceInfo = owner.new SpaceInfo();
				owner.cloneSpaceInfoFrom(spaceInfo);
			}

			//clone attrs
			if (attrs != null)
				clone.attrs = attrs.clone(owner);

			//clone listener
			if (listeners != null) {
				clone.listeners = new HashMap(4);
				for (Iterator it = listeners.entrySet().iterator();
				it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					final List list = new LinkedList();
					for (Iterator it2 = ((List)me.getValue()).iterator();
					it2.hasNext();) {
						Object val = it2.next();
						if (val instanceof ComponentCloneListener) {
							val = owner.willClone((ComponentCloneListener)val);
							if (val == null) continue; //don't use it in clone
						}
						list.add(val);
					}
					if (!list.isEmpty())
						clone.listeners.put(me.getKey(), list);
				}
			}

			//clone forwards (after children is cloned)
			if (forwards != null) {
				clone.forwards = null;
				for (Iterator it = forwards.entrySet().iterator(); it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					final String orgEvent = (String)me.getKey();

					final Object[] info = (Object[])me.getValue();
					final List fwds = (List)info[1];
					for (Iterator e = fwds.iterator(); e.hasNext();) {
						final Object[] fwd = (Object[])e.next();
						owner.addForward0(orgEvent, fwd[0], (String)fwd[1], fwd[2]);
					}
				}
			}

			//AuService
			if (ausvc instanceof ComponentCloneListener)
				clone.ausvc = (AuService)owner.willClone((ComponentCloneListener)ausvc);
		}
		private void render(ContentRenderer renderer)
		throws IOException {
			renderer.renderWidgetListeners(wgtlsns);
			renderer.renderWidgetOverrides(wgtovds);
			renderer.renderWidgetAttributes(wgtattrs);
		}
	}

	private final ChildInfo initChildInfo() {
		if (_chdinf == null)
			_chdinf = new ChildInfo();
		return _chdinf;
	}
	private static class ChildInfo implements Cloneable/* not java.io.Serializable*/ {
		/** The first child. */
		private transient AbstractComponent first;
		/** The last child. */
		private transient AbstractComponent last;
		/** # of children. */
		private transient int nChild;
		/** Set of components that are being added or removed.
		 * _aring[0]: add, _aring[1]: remove
		 * It is used to prevent dead-loop between {@link #removeChild}
		 * and {@link #setParent}.
		 */
		private transient Set[] _aring; //use an array to save memory
		/** The modification count used to avoid co-modification of _next, _prev..
		 */
		private transient int modCntChd;

		private ChildInfo() {
		}
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
		private ChildInfo clone(AbstractComponent owner) {
			final ChildInfo clone = (ChildInfo)clone();

			AbstractComponent q = null;
			for (AbstractComponent p = first; p != null; p = p._next) {
				AbstractComponent child = (AbstractComponent)p.clone();
				if (q != null) q._next = child;
				else clone.first = child;
				child._prev = q;
				q = child;

				child._parent = owner; //correct it
			}
			clone.last = q;
			return clone;
		}

		/** Returns whether the child is being removed.
		 */
		private boolean inRemoving(Component child) {
			return _aring != null && _aring[1] != null && _aring[1].contains(child);
		}
		/** Sets if the child is being removed.
		 */
		private void markRemoving(Component child, boolean set) {
			markARing(child, set, 1);
		}
		/** Returns whether the child is being added.
		 */
		private boolean inAdding(Component child) {
			return _aring != null && _aring[0] != null && _aring[0].contains(child);
		}
		/** Sets if the child is being added.
		 */
		private void markAdding(Component child, boolean set) {
			markARing(child, set, 0);
		}
		private void markARing(Component child, boolean set, int which) {
			if (set) {
				if (_aring == null) _aring = new Set[2];
				if (_aring[which] == null) _aring[which] = new HashSet(2);
				_aring[which].add(child);
			} else if (_aring != null && _aring[which] != null
			&& _aring[which].remove(child) && _aring[which].isEmpty())
				if (_aring[which == 0 ? 1: 0] == null) //both null
					_aring = null;
				else
					_aring[which] = null;
		}
	}
}
