/* AbstractComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:49:42     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.AbstractSequentialList;
import java.util.LinkedList;
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
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.io.PrintWriterX;
import org.zkoss.io.Serializables;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Deferrable;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Macro;
import org.zkoss.zk.ui.ext.RawId;
import org.zkoss.zk.ui.ext.NonFellow;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.util.ComponentSerializationListener;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.sys.Names;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.JsContentRenderer;
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
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.fn.ZkFns;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.out.AuClientInfo;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.util.SimpleNamespace;
import org.zkoss.zk.device.marshal.*;

/**
 * A skeletal implementation of {@link Component}. Though it is OK
 * to implement Component from scratch, this class simplifies some of
 * the chores.
 *
 * @author tomyeh
 */
public class AbstractComponent
implements Component, ComponentCtrl, java.io.Serializable {
	private static final Log log = Log.lookup(AbstractComponent.class);
    private static final long serialVersionUID = 20070920L;

	/** Map(Class, Map(String name, Integer flags)). */
	private static final Map _clientEvents = new HashMap(128);

	/*package*/ transient Page _page;
	private String _id;
	private String _uuid;
	private transient ComponentDefinition _def;
	/** The mold (default: "default"). */
	private String _mold = "default";
	/** The info of the ID space, or null if IdSpace is NOT implemented. */
	private transient SpaceInfo _spaceInfo;
	private transient Map _attrs;
		//don't create it dynamically because _ip bind it at constructor
	/** A map of event listener: Map(evtnm, EventListener)). */
	private transient Map _listeners;
	/** The extra controls. */
	private transient Object _xtrl;

	/** The list used for {@link #getChildren} only. */
	private transient List _apiChildren;
	private transient AbstractComponent _parent;
	/** The next sibling. */
	/*package*/ transient AbstractComponent _next;
	/** The previous sibling. */
	/*package*/ transient AbstractComponent _prev;
	/** The first child. */
	/*package*/ transient AbstractComponent _first;
	/** The last child. */
	/*package*/ transient AbstractComponent _last;
	/** # of children. */
	private int _nChild;
	/** The modification count used to avoid co-modification of _next, _prev..
	 */
	private transient int _modCntChd;
	/** A set of components that are being removed.
	 * It is used to prevent dead-loop between {@link #removeChild}
	 * and {@link #setParent}.
	 */
	private transient Set _rming;
	/** A set of components that are being added.
	 * It is used to prevent dead-loop between {@link #insertBefore}
	 * and {@link #setParent}.
	 */
	private transient Set _adding;

	/** A map of annotations. Serializable since a component might have
	 * its own annotations.
	 */
	private AnnotationMap _annots;
	/** A map of event handler to handle events. */
	private EventHandlerMap _evthds;
	/** A map of client event hanlders, Map(String evtnm, String script). */
	private Map _wgtlsns;
	/** A map of client methods, Map(String mtdnm, String script). */
	private Map _wgtmtds;
	/** A map of forward conditions:
	 * Map(String orgEvt, [listener, List([target or targetPath,targetEvent])]).
	 */
	private transient Map _forwards;
	/** Whether _annots is shared with other components. */
	private transient boolean _annotsShared;
	/** Whether _evthds is shared with other components. */
	private transient boolean _evthdsShared;
	/** Whether this component is visible.
	 * @since 3.5.0 (becomes protected)
	 */
	protected boolean _visible = true;

	/** Constructs a component with auto-generated ID.
	 * @since 3.0.7 (becomes public)
	 */
	public AbstractComponent() {
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
			_def = lookupDefinition(exec, getClass());
			if (_def != null)
				addSharedAnnotationMap(_def.getAnnotationMap());
			else {
				if (this instanceof Macro)
					log.warning(
						"Component definition not found for the macro "+this.getClass()
						+". Current page definition: "
						+(exec != null ? ""+((ExecutionCtrl)exec).getCurrentPageDefinition(): "n/a")
						+". Current page: "
						+(exec != null ? ""+((ExecutionCtrl)exec).getCurrentPage(): "n/a"));
				_def = ComponentsCtrl.DUMMY;
			}
		}

		init(false);

		_spaceInfo = this instanceof IdSpace ? new SpaceInfo(this): null;

//		if (D.ON && log.debugable()) log.debug("Create comp: "+this);
	}
	private static final
	ComponentDefinition lookupDefinition(Execution exec, Class cls) {
		if (exec != null) {
			final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
			final PageDefinition pgdef = execCtrl.getCurrentPageDefinition();
			final Page page = execCtrl.getCurrentPage();

			final ComponentDefinition compdef =
				pgdef != null ? pgdef.getComponentDefinition(cls, true):
				page != null ? 	page.getComponentDefinition(cls, true): null;
			if (compdef != null) return compdef;

			return lookupDefinitionByDeviceType(exec.getDesktop().getDeviceType(), cls);
		}

		for (Iterator it = LanguageDefinition.getDeviceTypes().iterator(); it.hasNext();) {
			final ComponentDefinition compdef =
				lookupDefinitionByDeviceType((String)it.next(), cls);
			if (compdef != null)
				return compdef;
		}
		return null;
	}
	private static final ComponentDefinition
	lookupDefinitionByDeviceType(String deviceType, Class cls) {
		for (Iterator it = LanguageDefinition.getByDeviceType(deviceType).iterator();
		it.hasNext();) {
			final LanguageDefinition ld = (LanguageDefinition)it.next();
			try {
				return ld.getComponentDefinition(cls);
			} catch (DefinitionNotFoundException ex) { //ignore
			}
		}
		return null;
	}
	/** Initialize for contructor and serialization.
	 * @param cloning whether this method is called by clone()
	 */
	private void init(boolean cloning) {
		_apiChildren = newChildren(); 

		if (!cloning)
			_attrs = new HashMap(4);
	}
	/**
	 * Creates and returns the instance for storing child components.
	 * <p>Default: it instantiates {@link AbstractComponent.Children}.
	 * @since 3.5.1
	 */
	protected List newChildren() {
		return new Children();
	}
	/** The default implementation for {@link #newChildren}.
	 * It is suggested to extend this class if you want to override
	 * {@link #newChildren} to instantiate your own instance.
	 * @since 3.5.1
	 */
	protected class Children extends AbstractSequentialList {
		public int size() {
			return _nChild;
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
	private static final IdSpace getSpaceOwnerOfParent(Component comp) {
		final Component parent = comp.getParent();
		if (parent != null) return parent.getSpaceOwner();
		else return comp.getPage();
	}
	/** Removes from the ID spaces, if any, when ID is changed. */
	private static void removeFromIdSpaces(final Component comp) {
		final String compId = getIdDirectly(comp);
		if (comp instanceof NonFellow || ComponentsCtrl.isAutoId(compId))
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
		&& comp._spaceInfo.fellows.containsKey(newId))
			throw new UiException("Not unique in the ID space of "+comp);

		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component) {
			if (((AbstractComponent)is)._spaceInfo.fellows.containsKey(newId))
				throw new UiException("Not unique in the ID space of "+is);
		} else if (is != null) {
			if (is.hasFellow(newId))
				throw new UiException("Not unique in the ID space of "+is);
		}
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
	/** comp's ID might be auto id. */
	private static void addToIdSpacesDown(Component comp, Component owner) {
		if (!(comp instanceof NonFellow)
		&& !ComponentsCtrl.isAutoId(getIdDirectly(comp)))
			((AbstractComponent)owner).bindToIdSpace(comp);
		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = ((AbstractComponent)comp)._first;
			ac != null; ac = ac._next)
				addToIdSpacesDown(ac, owner); //recursive
	}
	/** comp's ID might be auto id. */
	private static void addToIdSpacesDown(Component comp, AbstractPage owner) {
		if (!(comp instanceof NonFellow)
		&& !ComponentsCtrl.isAutoId(getIdDirectly(comp)))
			owner.addFellow(comp);
		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = ((AbstractComponent)comp)._first;
			ac != null; ac = ac._next)
				addToIdSpacesDown(ac, owner); //recursive
	}
	/** Similar to {@link #getId} except it won't generate one if not
	 * available.
	 */
	private static String getIdDirectly(Component comp) {
		return ((AbstractComponent)comp)._id;
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
		final String compId = getIdDirectly(comp);
		if (!(comp instanceof NonFellow)
		&& !ComponentsCtrl.isAutoId(compId))
			((AbstractComponent)owner).unbindFromIdSpace(compId);
		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = ((AbstractComponent)comp)._first;
			ac != null; ac = ac._next)
				removeFromIdSpacesDown(ac, owner); //recursive
	}
	private static void removeFromIdSpacesDown(Component comp, AbstractPage owner) {
		if (!(comp instanceof NonFellow)
		&& !ComponentsCtrl.isAutoId(getIdDirectly(comp)))
			owner.removeFellow(comp);
		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = ((AbstractComponent)comp)._first;
			ac != null; ac = ac._next)
				removeFromIdSpacesDown(ac, owner); //recursive
	}

	/** Checks the uniqueness in ID space when changing parent. */
	private static void checkIdSpacesDown(Component comp, Component newparent) {
		final IdSpace is = newparent.getSpaceOwner();
		if (is instanceof Component)
			checkIdSpacesDown(comp, ((AbstractComponent)is)._spaceInfo);
		else if (is != null)
			checkIdSpacesDown(comp, (AbstractPage)is);
	}
	/** Checks comp and its descendants for the specified SpaceInfo. */
	private static void checkIdSpacesDown(Component comp, SpaceInfo si) {
		final String compId = getIdDirectly(comp);
		if (!(comp instanceof NonFellow)
		&& !ComponentsCtrl.isAutoId(compId) && si.fellows.containsKey(compId))
			throw new UiException("Not unique in the new ID space: "+compId);
		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = ((AbstractComponent)comp)._first;
			ac != null; ac = ac._next)
				checkIdSpacesDown(ac, si); //recursive
	}
	/** Checks comp and its descendants for the specified page. */
	private static void checkIdSpacesDown(Component comp, AbstractPage page) {
		final String compId = getIdDirectly(comp);
		if (!(comp instanceof NonFellow)
		&& !ComponentsCtrl.isAutoId(compId) && page.hasFellow(compId))
			throw new UiException("Not unique in the ID space of "+page+": "+compId);
		if (!(comp instanceof IdSpace))
			for (AbstractComponent ac = ((AbstractComponent)comp)._first;
			ac != null; ac = ac._next)
				checkIdSpacesDown(ac, page); //recursive
	}

	/** Bind comp to this ID space (owned by this component).
	 * Called only if IdSpace is implemented.
	 * comp's ID must be unquie (and not auto id)
	 */
	private void bindToIdSpace(Component comp) {
		_spaceInfo.fellows.put(getIdDirectly(comp), comp);
	}
	/** Unbind comp from this ID space (owned by this component).
	 * Called only if IdSpace is implemented.
	 */
	private void unbindFromIdSpace(String compId) {
		_spaceInfo.fellows.remove(compId);
	}

	//-- Extra utlities --//
	/** Returns the UI engine based on {@link #_page}'s getDesktop().
	 * Don't call this method when _page is null.
	 */
	private final UiEngine getThisUiEngine() {
		return ((WebAppCtrl)_page.getDesktop().getWebApp()).getUiEngine();
	}

	//-- Component --//
	public final Page getPage() {
		return _page;
	}
	public final Desktop getDesktop() {
		return _page != null ? _page.getDesktop(): null;
	}

	public void setPage(Page page) {
		if (page != _page)
			setPageBefore(page, null); //append
	}
	public void setPageBefore(Page page, Component refRoot) {
		if (refRoot != null && (page == null || refRoot == this
		|| refRoot.getParent() != null || refRoot.getPage() != page))
			refRoot = null;

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
			if (page == null) {
				((DesktopCtrl)_page.getDesktop()).removeComponent(this);
			}
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
				if (_id == null || (this instanceof RawId))
					_id = _uuid;
					//no need to handle ID space since it is either
					//anonymous or uuid is not changed

				((DesktopCtrl)desktop).addComponent(this); //depends on uuid
			}

			onPageAttached(_page, oldpage);
		} else {
			onPageDetached(oldpage);
		}

		if (_spaceInfo != null && _parent == null)
			_spaceInfo.ns.setParent(page != null ? page.getNamespace(): null);

		//process all children recursively
		for (AbstractComponent p = _first; p != null; p = p._next)
			p.setPage0(page); //recursive
	}

	private String nextUuid(Desktop desktop) {
		final IdGenerator idgen =
			((WebAppCtrl)desktop.getWebApp()).getIdGenerator();
		String uuid;
		do {
			uuid = idgen != null ? idgen.nextComponentUuid(desktop, this): null;
			if (uuid == null)
				uuid = ((DesktopCtrl)desktop).getNextUuid();
		} while (desktop.getComponentByUuidIfAny(uuid) != null);
		return uuid;
	}
	public String getId() {
		if (_id == null)
			_id = getUuid();
		return _id;
	}
	public void setId(String id) {
		if (id != null && id.length() == 0)
			throw new UiException("ID cannot be empty"); //null means reset

		if (!Objects.equals(_id, id)) {
			boolean rawId = this instanceof RawId;
			final String newUuid;
			if (rawId) newUuid = id;
			else if ((newUuid = id2Uuid(id)) != null)
				rawId = true;

			if (id != null) {
				if (Names.isReserved(id)
				|| (!(this instanceof NonFellow) && ComponentsCtrl.isAutoId(id)))
					throw new UiException("Invalid ID: "+id+". Cause: reserved words not allowed: "+Names.getReservedNames());

				if (rawId && _page != null
				&& _page.getDesktop().getComponentByUuidIfAny(newUuid) != null)
					throw new UiException("Replicated UUID is not allowed for "+getClass()+": "+newUuid);

				checkIdSpaces(this, id);
			}

			removeFromIdSpaces(this);
			if (rawId) { //we have to change UUID
				if (_page != null) {
					getThisUiEngine().addUuidChanged(this, false);
						//called before uuid is changed
					((DesktopCtrl)_page.getDesktop()).removeComponent(this);
				}

				_id = id;
				_uuid = newUuid;

				if (_page != null) {
					((DesktopCtrl)_page.getDesktop()).addComponent(this);
					addMoved(_parent, _page, _page);
				}
			} else {
				_id = id;
			}

			if (_id != null)
				addToIdSpaces(this);

			smartUpdate("id", ComponentsCtrl.isAutoId(_id) ? null: _id);
		}
	}

	public final String getUuid() {
		if (_uuid == null) {
			final Execution exec = Executions.getCurrent();
			_uuid = exec == null ?
				ComponentsCtrl.ANONYMOUS_ID: nextUuid(exec.getDesktop());
			if (_id == null || (this instanceof RawId))
				_id = _uuid;
		}
		return _uuid;
	}

	public final IdSpace getSpaceOwner() {
		Component p = this;
		do {
			if (p instanceof IdSpace)
				return (IdSpace)p;
		} while ((p = p.getParent()) != null);
		return _page;
	}
	public boolean hasFellow(String compId) {
		if (this instanceof IdSpace)
			return _spaceInfo.fellows.containsKey(compId);

		final IdSpace idspace = getSpaceOwner();
		return idspace != null && idspace.hasFellow(compId);
	}
	public Component getFellow(String compId) {
		if (this instanceof IdSpace) {
			final Component comp = (Component)_spaceInfo.fellows.get(compId);
			if (comp == null)
				if (compId != null && ComponentsCtrl.isAutoId(compId))
					throw new ComponentNotFoundException(MZk.AUTO_ID_NOT_LOCATABLE, compId);
				else
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
			return (Component)_spaceInfo.fellows.get(compId);

		final IdSpace idspace = getSpaceOwner();
		return idspace == null ? null: idspace.getFellowIfAny(compId);
	}
	public Collection getFellows() {
		if (this instanceof IdSpace)
			return Collections.unmodifiableCollection(_spaceInfo.fellows.values());

		final IdSpace idspace = getSpaceOwner();
		return idspace == null ? Collections.EMPTY_LIST: idspace.getFellows();
	}

	public Component getNextSibling() {
		return _next;
	}
	public Component getPreviousSibling() {
		return _prev;
	}
	public Component getFirstChild() {
		return _first;
	}
	public Component getLastChild() {
		return _last;
	}

	public String setWidgetListener(String evtnm, String script) {
		if (evtnm == null)
			throw new IllegalArgumentException();

		final String old;
		if (script != null) {
			if (_wgtlsns == null) _wgtlsns = new LinkedHashMap();
			old = (String)_wgtlsns.put(evtnm, script);
		} else
			old = _wgtlsns != null ? (String)_wgtlsns.remove(evtnm): null;
		if (!Objects.equals(script, old))
			smartUpdateWidgetListener(evtnm, script);
		return old;
	}
	public String getWidgetListener(String evtnm) {
		return _wgtlsns != null ? (String)_wgtlsns.get(evtnm): null;
	}
	public Set getWidgetListenerNames() {
		return _wgtlsns != null ? _wgtlsns.keySet(): Collections.EMPTY_SET;
	}

	public String setWidgetMethod(String mtdnm, String script) {
		if (mtdnm == null)
			throw new IllegalArgumentException();

		final String old;
		if (script != null) {
			if (_wgtmtds == null) _wgtmtds = new LinkedHashMap();
			old = (String)_wgtmtds.put(mtdnm, script);
		} else
			old = _wgtmtds != null ? (String)_wgtmtds.remove(mtdnm): null;
		if (!Objects.equals(script, old))
			smartUpdateWidgetMethod(mtdnm, script);
		return old;
	}
	public String getWidgetMethod(String mtdnm) {
		return _wgtmtds != null ? (String)_wgtmtds.get(mtdnm): null;
	}
	public Set getWidgetMethodNames() {
		return _wgtmtds != null ? _wgtmtds.keySet(): Collections.EMPTY_SET;
	}

	public Map getAttributes(int scope) {
		switch (scope) {
		case SPACE_SCOPE:
			if (this instanceof IdSpace)
				return _spaceInfo.attrs;
			final IdSpace idspace = getSpaceOwner();
			return idspace instanceof Page ? ((Page)idspace).getAttributes():
				idspace == null ? Collections.EMPTY_MAP:
					((Component)idspace).getAttributes(SPACE_SCOPE);
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
			return _attrs;
		case REQUEST_SCOPE:
			final Execution exec = getExecution();
			if (exec != null) return exec.getAttributes();
			//fall thru
		default:
			return Collections.EMPTY_MAP;
		}
	}
	private final Execution getExecution() {
		return _page != null ? _page.getDesktop().getExecution():
			Executions.getCurrent();
	}
	public Object getAttribute(String name, int scope) {
		return getAttributes(scope).get(name);
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

	public final Map getAttributes() {
		return _attrs;
	}
	public final Object getAttribute(String name) {
		return _attrs.get(name);
	}
	public final Object setAttribute(String name, Object value) {
		return value != null ? _attrs.put(name, value): _attrs.remove(name);
	}
	public final Object removeAttribute(String name) {
		return _attrs.remove(name);
	}

	public void setVariable(String name, Object val, boolean local) {
		getNamespace().setVariable(name, val, local);
	}
	public boolean containsVariable(String name, boolean local) {
		return getNamespace().containsVariable(name, local);
	}
	public Object getVariable(String name, boolean local) {
		return getNamespace().getVariable(name, local);
	}
	public void unsetVariable(String name, boolean local) {
		getNamespace().unsetVariable(name, local);
	}

	public Component getParent() {
		return _parent;
	}
	public void setParent(Component parent) {
		if (_parent == parent)
			return; //nothing changed

		checkParentChild(parent, this);

		final boolean idSpaceChanged =
			parent != null ?
				parent.getSpaceOwner() !=
					(_parent != null ? _parent.getSpaceOwner(): _page):
				_page != null || _parent.getSpaceOwner() != null;

		if (idSpaceChanged) removeFromIdSpacesDown(this);

		//call removeChild and clear _parent
		final AbstractComponent op = _parent;
		if (op != null) {
			if (!op.inRemoving(this)) {
				op.markRemoving(this, true);
				try {
					op.removeChild(this); //spec: call back removeChild
				} finally {
					op.markRemoving(this, false);
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
			if (!np.inAdding(this)) {
				np.markAdding(this, true);
				try {
					np.insertBefore(this, null); //spec: call back inserBefore
				} finally {
					np.markAdding(this, false);
				}
			}
			_parent = np; //np.insertBefore assumes _parent not changed yet
		} //if parent == null, assume no page at all (so no addRoot)

		//correct _page
		final Page newpg = _parent != null ? _parent.getPage(): null,
			oldpg = _page;
		addMoved(op, _page, newpg); //Not depends on UUID
		setPage0(newpg); //UUID might be changed here

		fixSpaceParentDown(this, _parent != null ? _parent.getNamespace(): null);
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
	private static void fixSpaceParentDown(AbstractComponent comp, Namespace ns) {
		if (comp._spaceInfo != null) //ID space owner
			comp._spaceInfo.ns.setParent(ns);
		else //Bug 2468048: we have to check all children
			for (comp = comp._first; comp != null; comp = comp._next)
				fixSpaceParentDown(comp, ns);
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

	/** Returns whether the child is being removed.
	 */
	private boolean inRemoving(Component child) {
		return _rming != null && _rming.contains(child);
	}
	/** Sets if the child is being removed.
	 */
	private void markRemoving(Component child, boolean set) {
		if (set) {
			if (_rming == null) _rming = new HashSet(2);
			_rming.add(child);
		} else {
			if (_rming != null && _rming.remove(child) && _rming.isEmpty())
				_rming = null;
		}
	}
	/** Returns whether the child is being added.
	 */
	private boolean inAdding(Component child) {
		return _adding != null && _adding.contains(child);
	}
	/** Sets if the child is being added.
	 */
	private void markAdding(Component child, boolean set) {
		if (set) {
			if (_adding == null) _adding = new HashSet(2);
			_adding.add(child);
		} else {
			if (_adding != null && _adding.remove(child) && _adding.isEmpty())
				_adding = null;
		}
	}

	/**
	 * @param parent the parent (will-be). It may be null.
	 * @param child the child (will-be). It cannot be null.
	 */
	private static void checkParentChild(Component parent, Component child)
	throws UiException {
		if (parent != null) {
			final AbstractComponent acp = (AbstractComponent)parent;
			if (acp.inAdding(child))
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
		checkParentChild(this, newChild);

		if (refChild != null && refChild.getParent() != this)
			refChild = null;

		if (newChild == refChild)
			return false; //nothing changed (Listbox and other assumes this)

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
			if (!inAdding(nc)) {
				markAdding(nc, true);
				try {
					nc.setParent(this); //spec: callback setParent
				} finally {
					markAdding(nc, false);
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
			if (_last == null) {
				_first = _last = nc;
				nc._next = nc._prev = null;
			} else {
				_last._next = nc;
				nc._prev = _last;
				nc._next = null;
				_last = nc;
			}
		}

		++_modCntChd;
		if (!moved) { //new added
			++_nChild;
			onChildAdded(nc);
		}
		return true;
	}
	private final
	void setNext(AbstractComponent comp, AbstractComponent next) {
		if (comp != null) comp._next = next;
		else _first = next;
	}
	private final
	void setPrev(AbstractComponent comp, AbstractComponent prev) {
		if (comp != null) comp._prev = prev;
		else _last = prev;
	}

	/** Appends a child to the end of all children.
	 * It calls {@link #insertBefore} with refChild to be null.
	 * Derives cannot override this method, and they shall override
	 * {@link #insertBefore} instead.
	 */
	public final boolean appendChild(Component child) { //Yes, final; see below
		return insertBefore(child, null); //NOTE: we must go thru insertBefore
			//such that deriving is easy to override
	}
	public boolean removeChild(Component child) {
		final AbstractComponent oc = (AbstractComponent)child;
		if (oc._parent != this)
			return false; //nothing to do

		setNext(oc._prev, oc._next);
		setPrev(oc._next, oc._prev);
		oc._next = oc._prev = null;

		if (!inRemoving(oc)) {
			markRemoving(oc, true);
			try {
				oc.setParent(null); //spec: call back setParent
			} finally {
				markRemoving(oc, false);
			}
		} else {
			oc._parent = null;
				//Correct it since deriving class might assume parent is
				//correct after insertBefore() returns.
				//refer to insertBefore for more info.
		}

		++_modCntChd;
		--_nChild;
		onChildRemoved(child);
		return true;
	}

	/** Returns whether this component can have a child.
	 * <p>Default: return true (means it can have children).
	 */
	protected boolean isChildable() {
		return true;
	}
	public List getChildren() {
		return _apiChildren;
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
		return _visible;
	}
	public boolean setVisible(boolean visible) {
		final boolean old = _visible;
		if (old != visible) {
			_visible = visible;
			smartUpdate("visible", _visible);
		}
		return old;
	}

	public boolean isInvalidated() {
		return _page == null || getThisUiEngine().isInvalidated(this);
	}
	public void invalidate() {
		if (_page != null)
			getThisUiEngine().addInvalidate(this);
	}

	/** Causes a response to be sent to the client.
	 *
	 * <p>If {@link AuResponse#getDepends} is not null, the response
	 * depends on the existence of the returned componet.
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
	 * in the same execution with the same key will override the previous one.
	 * However, if key is null, it won't override any other. All responses
	 * with key == null will be sent.
	 * @since 5.0.0 (become protected)
	 */
	protected void response(String key, AuResponse response) {
		//if response not depend on this component, it must be generated
		if (_page != null) {
			getThisUiEngine().addResponse(key, response);
		} else if (response.getDepends() != this) {
			final Execution exec = Executions.getCurrent();
			if (exec != null)
				((WebAppCtrl)exec.getDesktop().getWebApp())
					.getUiEngine().addResponse(key, response);
		}
	}

	/** Smart-updates a property of the widget at the client
	 * with the specified value.
	 *
	 * <p>The second invocation with the same property will replace the previous
	 * call. In other words, the same property will be set only once in
	 * each execution.
	 *
	 * <p>This method has no effect if {@link #invalidate()} is ever invoked
	 * (in the same execution), since {@link #invalidate()} assumes
	 * the whole content shall be redrawn and all smart updates to
	 * this components can be ignored,
	 *
	 * <p>Once {@link #invalidate} is called, all invocations to {@link #smartUpdate}
	 * will then be ignored, and {@link #redraw} will be invoked later.
	 *
	 * <p>It can be called only in the request-processing and event-processing
	 * phases; excluding the redrawing phase.
	 *
	 * <p>There are two ways to draw a component, one is to invoke
	 * {@link Component#invalidate()}, and the other is {@link #smartUpdate}.
	 * While {@link Component#invalidate()} causes the whole content to redraw,
	 * {@link #smartUpdate} let component developer control which part
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
	 * <p>In addition, the value can be any kind of objects that
	 * the client accepts. For Ajax devices, it could be String,
	 * Date, the wrapper of primitives (such as Boolean), primitives
	 * (wrapped with {@link $boolean} and so on), and an array of above types.
	 * @since 5.0.0 (become protected)
	 */
	protected void smartUpdate(String attr, Object value) {
		if (_page != null) getThisUiEngine().addSmartUpdate(this, attr, value);
	}
	/** A special smart update to update a value in int.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, int value) {
		smartUpdate(attr, new $int(value));
	}
	/** A special smart update to update a value in long.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, long value) {
		smartUpdate(attr, new $long(value));
	}
	/** A special smart update to update a value in byte.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, byte value) {
		smartUpdate(attr, new $byte(value));
	}
	/** A special smart update to update a value in character.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, char value) {
		smartUpdate(attr, new $char(value));
	}
	/** A special smart update to update a value in boolean.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, boolean value) {
		smartUpdate(attr, $boolean.valueOf(value));
	}
	/** A special smart update to update a value in float.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, float value) {
		smartUpdate(attr, new $float(value));
	}
	/** A special smart update to update a value in double.
	 * <p>It will invoke {@link #smartUpdate(String,Object)} to update
	 * the attribute eventually.
	 * @since 5.0.0
	 */
	protected void smartUpdate(String attr, double value) {
		smartUpdate(attr, new $double(value));
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
		smartUpdate("listener", new String[] {evtnm, script});
	}
	/** A special smart update to update a method of the peer widget.
	 * By default, it assumes the peer widget has a method called
	 * <code>setMethod<code> and it will be invoked as follows.
	 *
	 * <pre><code>wgt.setMethod([evtnm, script]);</code></pre>
	 *
	 * <p>Devices that supports it in another way have to override this
	 * method. Devices that don't support it have to override this method
	 * to throw UnsupportedOperationException.
	 *
	 * @param mtdnm the method name, such as setValue
	 * @param script the script. If null, the previous method override
	 * will be remove. And, the method defined in original widget will
	 * be restored.
	 * @since 5.0.0
	 */
	protected void smartUpdateWidgetMethod(String mtdnm, String script) {
		smartUpdate("method", new String[] {mtdnm, script});
	}

	public void detach() {
		if (getParent() != null) setParent(null);
		else setPage(null);
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
	 * @since 5.0.0
	 */
	public String getWidgetClass() {
		final String widgetClass = _def.getWidgetClass(getMold());
		return widgetClass != null ? widgetClass: _def.getDefaultWidgetClass();
	}

	/** Returns the mold used to render this component.
	 * Default: "default"
	 */
	public final String getMold() {
		return _mold;
	}
	public void setMold(String mold) {
		if (mold == null || mold.length() == 0)
			mold = "default";

		if (!Objects.equals(_mold, mold)) {
			if (!_def.hasMold(mold))
				throw new UiException("Unknown mold: "+mold
					+", while allowed include "+_def.getMoldNames());
			final String oldtype = getWidgetClass();
			_mold = mold;
			if (Objects.equals(oldtype, getWidgetClass()))
				smartUpdate("mold", _mold);
			else
				invalidate();
		}
	}

	//-- in the redrawing phase --//
	/** Redraws this component and all its decendants.
	 * <p>Default: It uses {@link JsContentRenderer} to render all information
	 * in JavaScript codes. For devices that don't support JavaScript,
	 * it must override this method.
	 *
	 * To generate all information, it first invokes
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
		final JsContentRenderer renderer = new JsContentRenderer();
		renderProperties(renderer);
		out.write("\nzkbg('");

		final String wgtcls = getWidgetClass();
		if (wgtcls == null)
			throw new UiException("Widget class required for "+this+" with the "+getMold()+" mold");

		out.write(wgtcls);
		out.write("','");
		out.write(getUuid());
		out.write("','");
		final String mold = getMold();
		if (!"default".equals(mold))
			out.write(mold);
		out.write("',{\n");
		out.write(renderer.getBuffer().toString());
		out.write("});\n");

		redrawChildren(out);

		out.write("zke();\n");
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
				for (Iterator it = crop.iterator(); it.hasNext();)
					((ComponentCtrl)it.next()).redraw(out);
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
		if (!ComponentsCtrl.isAutoId(_id)) //not getId() to avoid gen ID
			render(renderer, "id", _id);
		if (!_visible) renderer.render("visible", false);

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
					renderer.render("$$" + evtnm, true);
			}
			if (Events.isListened(this, evtnm, false))
				renderer.render('$' + evtnm, Events.isListened(this, evtnm, true));
					//$onClick and so on
		}

		renderer.renderWidgetListeners(_wgtlsns);
		renderer.renderWidgetMethods(_wgtmtds);
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
	 * (a combination of {@link #CE_IMPORTANT}, {@link #CE_BUSY_IGNORE},
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
	 * @param flags a combination of {@link #CE_IMPORTANT},
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

		if (_listeners == null) _listeners = new HashMap(8);

		List l = (List)_listeners.get(evtnm);
		if (l != null) {
			for (Iterator it = l.iterator(); it.hasNext();) {
				final EventListener li = (EventListener)it.next();
				if (listener.equals(li))
					return false;
			}
		} else {
			_listeners.put(evtnm, l = new LinkedList());
		}
		l.add(listener);

		final Desktop desktop = getDesktop();
		if (desktop != null) {
			if (Events.ON_CLIENT_INFO.equals(evtnm)) {
				response("clientInfo", new AuClientInfo(desktop));
			} else if (Events.ON_PIGGYBACK.equals(evtnm)) {
				((DesktopCtrl)desktop).onPiggybackListened(this, true);
			} else if (getClientEvents().containsKey(evtnm)) {
				final boolean asap = Events.isListened(this, evtnm, true);
				if (l.size() == 1 || oldasap != asap)
					smartUpdate(evtnm, asap);
			}
		}
		return true;
	}
	public boolean removeEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new IllegalArgumentException("null");

		if (_listeners != null) {
			final boolean oldasap = Events.isListened(this, evtnm, true);
			final List l = (List)_listeners.get(evtnm);
			if (l != null) {
				for (Iterator it = l.iterator(); it.hasNext();) {
					final EventListener li = (EventListener)it.next();
					if (listener.equals(li)) {
						if (l.size() == 1)
							_listeners.remove(evtnm);
						else
							it.remove();

						final Desktop desktop = getDesktop();
						if (desktop != null) {
							onListenerChange(desktop, false);

							if (getClientEvents().containsKey(evtnm)) {
								if (l.isEmpty() && !Events.isListened(this, evtnm, false))
									smartUpdate(evtnm, (Object)null); //no listener at all
								else if (oldasap != Events.isListened(this, evtnm, true))
									smartUpdate(evtnm, !oldasap);
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

		if (_forwards == null)
			_forwards = new HashMap(4);

		Object[] info = (Object[])_forwards.get(orgEvent);
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
			_forwards.put(orgEvent, info);
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
		if (_forwards != null) {
			final Object[] info = (Object[])_forwards.get(orgEvent);
			if (info != null) {
				final List fwds = (List)info[1];
				for (Iterator it = fwds.iterator(); it.hasNext();) {
					final Object[] fwd = (Object[])it.next();
					if (Objects.equals(fwd[0], target)
					&& Objects.equals(fwd[1], targetEvent)) { //found
						it.remove(); //remove it

						if (fwds.isEmpty()) { //no more event
							_forwards.remove(orgEvent);
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

	public Namespace getNamespace() {
		if (this instanceof IdSpace)
			return _spaceInfo.ns;

		final IdSpace idspace = getSpaceOwner();
		return idspace instanceof Page ? ((Page)idspace).getNamespace():
			idspace == null ? null: ((Component)idspace).getNamespace();
	}

	public boolean isListenerAvailable(String evtnm, boolean asap) {
		if (_listeners != null) {
			final List l = (List)_listeners.get(evtnm);
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
		if (_listeners != null) {
			final List l = (List)_listeners.get(evtnm);
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
	public void setComponentDefinition(ComponentDefinition compdef) {
		if (compdef == null)
			throw new IllegalArgumentException("null");
		if (!compdef.isInstance(this))
			throw new IllegalArgumentException("Incompatible "+compdef+" for "+this);
		_def = compdef;
	}

	public ZScript getEventHandler(String evtnm) {
		final EventHandler evthd =
			_evthds != null ? _evthds.get(this, evtnm): null;
		return evthd != null ? evthd.getZScript(): null;
	}
	public void addSharedEventHandlerMap(EventHandlerMap evthds) {
		if (evthds != null && !evthds.isEmpty()) {
			unshareEventHandlerMap(false);
			if (_evthds == null) {
				_evthds = evthds;
				_evthdsShared = true;
			} else {
				_evthds.addAll(evthds);
			}

			final Desktop desktop = getDesktop();
			if (desktop != null)
				onListenerChange(desktop, true);
		}
	}
	public Set getEventHandlerNames() {
		return _evthds != null ? _evthds.getEventNames(): Collections.EMPTY_SET;
	}
	private void onListenerChange(Desktop desktop, boolean listen) {
		if (listen) {
			if (Events.isListened(this, Events.ON_CLIENT_INFO, false)) //asap+deferrable
				response("clientInfo", new AuClientInfo(desktop));
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
		_evthds.add(name, evthd);
	}
	/** Clones the shared event handlers, if shared.
	 * @param autocreate whether to create an event handler map if not available.
	 */
	private void unshareEventHandlerMap(boolean autocreate) {
		if (_evthdsShared) {
			_evthds = (EventHandlerMap)_evthds.clone();
			_evthdsShared = false;
		} else if (autocreate && _evthds == null) {
			_evthds = new EventHandlerMap();
		}
	}

	public Annotation getAnnotation(String annotName) {
		return _annots != null ? _annots.getAnnotation(annotName): null;
	}
	public Annotation getAnnotation(String propName, String annotName) {
		return _annots != null ?
			_annots.getAnnotation(propName, annotName): null;
	}
	public Collection getAnnotations() {
		return _annots != null ?
			_annots.getAnnotations(): Collections.EMPTY_LIST;
	}
	public Collection getAnnotations(String propName) {
		return _annots != null ?
			_annots.getAnnotations(propName): Collections.EMPTY_LIST;
	}
	public List getAnnotatedPropertiesBy(String annotName) {
		return _annots != null ?
			_annots.getAnnotatedPropertiesBy(annotName): Collections.EMPTY_LIST;
	}
	public List getAnnotatedProperties() {
		return _annots != null ?
			_annots.getAnnotatedProperties(): Collections.EMPTY_LIST;
	}
	public void addSharedAnnotationMap(AnnotationMap annots) {
		if (annots != null && !annots.isEmpty()) {
			unshareAnnotationMap(false);
			if (_annots == null) {
				_annots = annots;
				_annotsShared = true;
			} else {
				_annots.addAll(annots);
			}
		}
	}
	public void addAnnotation(String annotName, Map annotAttrs) {
		unshareAnnotationMap(true);
		_annots.addAnnotation(annotName, annotAttrs);
	}
	public void addAnnotation(String propName, String annotName, Map annotAttrs) {
		unshareAnnotationMap(true);
		_annots.addAnnotation(propName, annotName, annotAttrs);
	}
	/** Clones the shared annotations, if shared.
	 * @param autocreate whether to create an annotation map if not available.
	 */
	private void unshareAnnotationMap(boolean autocreate) {
		if (_annotsShared) {
			_annots = (AnnotationMap)_annots.clone();
			_annotsShared = false;
		} else if (autocreate && _annots == null) {
			_annots = new AnnotationMap();
		}
	}

	public void sessionWillPassivate(Page page) {
		//nothing to do
	}
	public void sessionDidActivate(Page page) {
		sessionDidActivate0(page, this, true);
	}
	/** 
	 * @param pageLevelIdSpace whether this component's ID space is
	 * at the page level.
	 */
	private static void sessionDidActivate0(Page page,
	AbstractComponent comp, boolean pageLevelIdSpace) {
		comp._page = page;

		//Note: we need only to fix the first-level spaceInfo.
		//Others are handled by readObject
		if (pageLevelIdSpace && comp._spaceInfo != null) {
			pageLevelIdSpace = false;
			comp._spaceInfo.ns.setParent(page.getNamespace());
		}

		for (AbstractComponent p = comp._first; p != null; p = p._next) {
			sessionDidActivate0(page, p, pageLevelIdSpace); //recursive
		}
	}

	/** Returns the extra controls that tell ZK how to handle this component
	 * specially.
	 * It is used only by component developers.
	 *
	 * <p>It is simpler to override {@link #newExtraCtrl} instead of this.
	 * By use of {@link #newExtraCtrl}, you don't need to care of
	 * cloning and serialization.
	 *
	 * <p>Default: return the object being created by {@link #newExtraCtrl},
	 * if any.
	 *
	 * @see ComponentCtrl#getExtraCtrl
	 */
	public Object getExtraCtrl() {
		if (_xtrl == null)
			_xtrl = newExtraCtrl();
				//3.0.3: create as late as possible so component has a chance
				//to customize which object to instantiate
		return _xtrl;
	}
	/** Used by {@link #getExtraCtrl} to create extra controls.
	 * It is used only by component developers.
	 *
	 * <p>Default: return null.
	 *
	 * <p>To provide extra controls, it is simpler to override this method
	 * instead of {@link #getExtraCtrl}.
	 * By use of {@link #newExtraCtrl}, you don't need to care of
	 * cloning and serialization.
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

	/** Processes an AU request.
	 *
	 * <p>Default: it convests the request to an event (by
	 * {@link Event#getEvent}) and then posts the event
	 * (by {@link Events#postEvent}).
	 * @since 5.0.0
	 */
	public void process(AuRequest request, boolean everError) {
		final String name = request.getName();
		if ("echo".equals(name)) {
			final String[] data = request.getData();
			if (data == null || (data.length != 1 && data.length != 2))
				throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
					new Object[] {Objects.toString(data), this});
			Events.postEvent(data.length == 1 ?
				new Event(data[0], this): new Event(data[0], this, data[1]));
		} else
			Events.postEvent(Event.getEvent(request));
	}

	//-- Object --//
	public String toString() {
		final String clsnm = getClass().getName();
		final int j = clsnm.lastIndexOf('.');
		return "<"+clsnm.substring(j+1)+' '
			+(ComponentsCtrl.isAutoId(_id) ? _uuid: _id)+'>';
	}
	public final boolean equals(Object o) { //no more override
		return this == o;
	}

	/** Holds info shared of the same ID space. */
	private static class SpaceInfo {
		private Map attrs = new HashMap(8);
			//don't create it dynamically because _ip bind it at constructor
		private SimpleNamespace ns;
		/** A map of ((String id, Component fellow). */
		private Map fellows = new HashMap(32);

		private SpaceInfo(Component owner) {
			ns = new SimpleNamespace(owner);
			init(owner);
		}
		private SpaceInfo(Component owner, SimpleNamespace from) {
			ns = new SimpleNamespace(owner);
			ns.copy(from);
			init(owner);
		}
		private void init(Component owner) {
			ns.setVariable("spaceScope", attrs, true);
			ns.setVariable("spaceOwner", owner, true);
		}
	}

	private class ChildIter implements ListIterator  {
		private AbstractComponent _p, _lastRet;
		private int _j;
		private int _modCntSnap;

		private ChildIter(int index) {
			if (index < 0 || index > _nChild)
				throw new IndexOutOfBoundsException("Index: "+index+", Size: "+_nChild);

			if (index < (_nChild >> 1)) {
				_p = _first;
				for (_j = 0; _j < index; _j++)
					_p = _p._next;
			} else {
				_p = null; //means the end of the list
				for (_j = _nChild; _j > index; _j--)
					_p = _p != null ? _p._prev: _last;
			}

			_modCntSnap = _modCntChd;
		}
		public boolean hasNext() {
			checkComodification();
			return _j < _nChild;
		}
		public Object next() {
			if (_j >= _nChild)
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

		    _lastRet = _p = _p != null ? _p._prev: _last;
		    _j--;
		    return _lastRet;
		}
		private void checkComodification() {
			if (_modCntChd != _modCntSnap)
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
				//don't assign _modCntChd directly since deriving class
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
		clone._xtrl = null; //Bug 1892396: _xtrl is an inner object so recreation is required

		//1a. clone attributes
		clone._attrs = new HashMap(4);
		for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			Object val = me.getValue();
			if (val instanceof ComponentCloneListener) {
				val = ((ComponentCloneListener)val).clone(clone);
				if (val == null) continue; //don't use it in clone
			}
			clone._attrs.put(me.getKey(), val);
		}

		//1b. clone listeners
		if (_listeners != null) {
			clone._listeners = new HashMap(4);
			for (Iterator it = _listeners.entrySet().iterator();
			it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final List list = new LinkedList();
				for (Iterator it2 = ((List)me.getValue()).iterator();
				it2.hasNext();) {
					Object val = it2.next();
					if (val instanceof ComponentCloneListener) {
						val = ((ComponentCloneListener)val).clone(clone);
						if (val == null) continue; //don't use it in clone
					}
					list.add(val);
				}
				if (!list.isEmpty())
					clone._listeners.put(me.getKey(), list);
			}
		}

		if (!_annotsShared && _annots != null)
			clone._annots = (AnnotationMap)_annots.clone();
		if (!_evthdsShared && _evthds != null)
			clone._evthds = (EventHandlerMap)_evthds.clone();
		if (_wgtlsns != null)
			clone._wgtlsns = new LinkedHashMap(_wgtlsns);
		if (_wgtmtds != null)
			clone._wgtmtds = new LinkedHashMap(_wgtmtds);

		//2. clone children (deep cloning)
		cloneChildren(clone);
		clone.init(true);

		//3. spaceinfo
		if (clone._spaceInfo != null) {
			clone._spaceInfo = new SpaceInfo(clone, _spaceInfo.ns);
			cloneSpaceInfo(clone, this._spaceInfo);
		}

		//4. clone _forwards
		if (clone._forwards != null) {
			clone._forwards = null;
			for (Iterator it = _forwards.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String orgEvent = (String)me.getKey();

				final Object[] info = (Object[])me.getValue();
				final List fwds = (List)info[1];
				for (Iterator e = fwds.iterator(); e.hasNext();) {
					final Object[] fwd = (Object[])e.next();
					clone.addForward0(orgEvent, fwd[0], (String)fwd[1], fwd[2]);
				}
			}
		}
		return clone;
	}
	private static final
	void cloneSpaceInfo(AbstractComponent clone, SpaceInfo from) {
		final SpaceInfo to = clone._spaceInfo;
		to.attrs = new HashMap(8);
		for (Iterator it = from.attrs.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			Object val = me.getValue();
			if (val instanceof ComponentCloneListener) {
				val = ((ComponentCloneListener)val).clone(clone);
				if (val == null) continue; //don't use it in clone
			}
			to.attrs.put(me.getKey(), val);
		}

		//rebuild ID space by binding itself and all children
		if (!ComponentsCtrl.isAutoId(getIdDirectly(clone)))
			clone.bindToIdSpace(clone);
		for (AbstractComponent p = clone._first; p != null; p = p._next)
			addToIdSpacesDown(p, clone);
	}
	private static final void cloneChildren(final AbstractComponent comp) {
		AbstractComponent q = null;
		for (AbstractComponent p = comp._first; p != null; p = p._next) {
			AbstractComponent child = (AbstractComponent)p.clone();
			if (q != null) q._next = child;
			else comp._first = child;
			child._prev = q;
			q = child;

			child._parent = comp; //correct it
			if (child._spaceInfo != null)
				child._spaceInfo.ns.setParent(comp.getNamespace());
		}
		comp._last = q;
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		//No need to unshare since they are stored as an independent copy
		//unshareAnnotationMap(false);
		//unshareEventHandlerMap(false);

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
		for (AbstractComponent p = _first; p != null; p = p._next)
			s.writeObject(p);
		s.writeObject(null);

		//write attrs
		willSerialize(_attrs.values());
		Serializables.smartWrite(s, _attrs);

		if (_listeners != null)
			for (Iterator it = _listeners.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				s.writeObject(me.getKey());

				final Collection ls = (Collection)me.getValue();
				willSerialize(ls);
				Serializables.smartWrite(s, ls);
			}
		s.writeObject(null);

		//store _spaceInfo
		if (this instanceof IdSpace) {
			//write _spaceInfo.attrs
			willSerialize(_spaceInfo.attrs.values());
			Serializables.smartWrite(s, _spaceInfo.attrs);

			//write _spaceInfo.ns (only variables that are not fellows)
			for (Iterator it = _spaceInfo.ns.getVariableNames().iterator();
			it.hasNext();) {
				final String nm = (String)it.next();
				final Object val = _spaceInfo.ns.getVariable(nm, true);
				willSerialize(val); //always called even if not serializable

				if (isVariableSerializable(nm, val)
				&& (val instanceof java.io.Serializable || val instanceof java.io.Externalizable)) {
					s.writeObject(nm);
					s.writeObject(val);
				}
			}
			s.writeObject(null); //denote end-of-namespace
		}

		//write _forwards
		if (_forwards != null) {
			for (Iterator it = _forwards.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				s.writeObject(me.getKey()); //original event

				final Object[] info = (Object[])me.getValue();
				final List fwds = (List)info[1];
				s.writeInt(fwds.size());
				for (Iterator e = fwds.iterator(); e.hasNext();) {
					final Object[] fwd = (Object[])e.next();
					s.writeObject( //store target as string
						fwd[0] instanceof Component ?
							Components.componentToPath((Component)fwd[0], this):
							fwd[0]);
					s.writeObject(fwd[1]); //target event
					s.writeObject(fwd[2]); //forward data
				}
			}
		}
		s.writeObject(null);
	}
	private void willSerialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willSerialize(it.next());
	}
	private void willSerialize(Object o) {
		if (o instanceof ComponentSerializationListener)
			((ComponentSerializationListener)o).willSerialize(this);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init(false);

		//read definition
		Object def = s.readObject();
		if (def == null) {
			_def = ComponentsCtrl.DUMMY;
		} else if (def instanceof String) {
			LanguageDefinition langdef = LanguageDefinition.lookup((String)def);
			_def = langdef.getComponentDefinition((String)s.readObject());
		} else {
			_def = (ComponentDefinition)def;
		}

		//read children
		for (AbstractComponent q = null;;) {
			final AbstractComponent child = (AbstractComponent)s.readObject();
			if (child == null) {
				_last = q;
				break; //no more
			}
			if (q != null) q._next = child;
			else _first = child;
			child._prev = q;
			child._parent = this;
			q = child;
		}

		//read attrs
		Serializables.smartRead(s, _attrs);
		didDeserialize(_attrs.values());

		for (;;) {
			final String evtnm = (String)s.readObject();
			if (evtnm == null) break; //no more

			if (_listeners == null) _listeners = new HashMap(4);
			final Collection ls = Serializables.smartRead(s, (Collection)null);
			_listeners.put(evtnm, ls);
			didDeserialize(ls);
		}

		//restore _spaceInfo
		if (this instanceof IdSpace) {
			_spaceInfo = new SpaceInfo(this);

			//fix children's _spaceInfo's parent
			for (AbstractComponent child = _first; child != null; child = child._next)
				fixSpaceParentDown(child,  _spaceInfo.ns);

			//read _spaceInfo.attrs
			Serializables.smartRead(s, _spaceInfo.attrs);
			didDeserialize(_spaceInfo.attrs.values());

			//_spaceInfo.ns
			for (;;) {
				final String nm = (String)s.readObject();
				if (nm == null) break; //no more

				Object val = s.readObject();
				_spaceInfo.ns.setVariable(nm, val, true);
				didDeserialize(val);
			}

			//restore ID space by binding itself and all children
			if (!ComponentsCtrl.isAutoId(getIdDirectly(this)))
				bindToIdSpace(this);
			for (AbstractComponent ac = _first; ac != null; ac = ac._next)
				addToIdSpacesDown(ac, this);
		}

		//restore _forwards
		for (;;) {
			final String orgEvent = (String)s.readObject();
			if (orgEvent == null)
				break;

			int sz = s.readInt();
			while (--sz >= 0)
				addForward0(orgEvent, s.readObject(),
					(String)s.readObject(), s.readObject());
					//Note: we don't call Components.pathToComponent here
					//since the parent doesn't deserialized completely
					//Rather, we handle it until the event is received
		}
	}
	private void didDeserialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didDeserialize(it.next());
	}
	private void didDeserialize(Object o) {
		if (o instanceof ComponentSerializationListener)
			((ComponentSerializationListener)o).didDeserialize(this);
	}
	private static boolean isVariableSerializable(String name, Object value) {
		return !"spaceScope".equals(name) && !"spaceOwner".equals(name)
			&& !(value instanceof Component);
	}

	/** Used to forward events (for the forward conditions).
	 */
	private class ForwardListener
	implements EventListener, ComponentCloneListener {
	//Note: it is not serializable since it is handled by
	//AbstractComponent.writeObject

		private final String _orgEvent;
		private ForwardListener(String orgEvent) {
			_orgEvent = orgEvent;
		}

		public void onEvent(Event event) {
			final Object[] info = (Object[])_forwards.get(_orgEvent);
			if (info != null)
				for (Iterator it = ((List)info[1]).iterator(); it.hasNext();) {
					final Object[] fwd = (Object[])it.next();
					Component target =
						fwd[0] instanceof String ?
							Components.pathToComponent(
								(String)fwd[0], AbstractComponent.this):
							(Component)fwd[0];

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

					Events.postEvent(
						new ForwardEvent((String)fwd[1], target, event, fwd[2]));
				}
		}

		//ComponentCloneListener//
		public Object clone(Component comp) {
			return null; //handle by AbstractComponent.clone
		}
	}

	private static final String NONE = "";
	private static String _id2uuidPrefix = NONE, _id2uuidPrefix2;
	private static int _id2uuidPageOfs;
	private static String id2Uuid(String id) {
		if (id != null) {
			if (_id2uuidPrefix == NONE) {
				_id2uuidPrefix = Library.getProperty(Attributes.ID_TO_UUID_PREFIX);
				if (_id2uuidPrefix != null) {
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
							return _id2uuidPrefix + page.getUuid() + _id2uuidPrefix2 + id;
					}
				}
				return _id2uuidPrefix + id;
			}
		}
		return null;
	}
}
