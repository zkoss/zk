/* AbstractComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:49:42     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.List;
import java.util.AbstractSequentialList;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.io.Serializables;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Deferrable;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.RawId;
import org.zkoss.zk.ui.ext.render.ZidRequired;
import org.zkoss.zk.ui.util.ComponentSerializationListener;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.util.DeferredValue;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.sys.Names;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.EventHandlerMap;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinitionMap;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.impl.ListenerIterator;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuClientInfo;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Method;
import org.zkoss.zk.scripting.util.SimpleNamespace;

/**
 * A skeletal implementation of {@link Component}. Though it is OK
 * to implement Component from scratch, this class simplifies some of
 * the chores.
 *
 * @author tomyeh
 */
public class AbstractComponent
implements Component, ComponentCtrl, java.io.Serializable {
//	private static final Log log = Log.lookup(AbstractComponent.class);
    private static final long serialVersionUID = 20070326L;

	private transient Page _page;
	private String _id;
	private String _uuid;
	private transient ComponentDefinition _def;
	private transient Component _parent;
	/** The mold (default: "default"). */
	private String _mold = "default";
	private List _children = new LinkedList();
	private transient List _modChildren;
	/** The info of the ID space, or null if IdSpace is NOT implemented. */
	private transient SpaceInfo _spaceInfo;
	private transient Map _attrs;
		//don't create it dynamically because _ip bind it at constructor
	/** A map of event listener: Map(evtnm, EventListener)). */
	private transient Map _listeners;
	/** The extra controls. */
	private transient Object _xtrl;
	/** A set of children being added. It is used only to speed up
	 * the performance when adding a new child. And, cleared after added.
	 * <p>To save footprint, we don't use Set (since it is rare to contain
	 * more than one)
	 */
	private transient List _newChildren;
	/** A map of annotations. Serializable since a component might have
	 * its own annotations.
	 */
	private AnnotationMap _annots;
	/** A Map of event handler to handle events. */
	private EventHandlerMap _evthds;
	/** Used when user is modifying the children by Iterator.
	 */
	private transient boolean _modChildByIter;
	/** Whether _annots is shared with other components. */
	private transient boolean _annotsShared;
	/** Whether _evthds is shared with other components. */
	private transient boolean _evthdsShared;
	/** Whether this component is visible. */
	private boolean _visible = true;

	/** Constructs a component with auto-generated ID.
	 */
	protected AbstractComponent() {
		final Execution exec = Executions.getCurrent();

		_def = ComponentsCtrl.getCurrentDefinition();
		if (_def != null) ComponentsCtrl.setCurrentDefinition(null); //to avoid mis-use
		else {
			_def = lookupDefinition(exec, getClass());
			if (_def == null)
				_def = ComponentsCtrl.DUMMY;
		}

		init(false);

		_spaceInfo = this instanceof IdSpace ? new SpaceInfo(this): null;

		addSharedAnnotationMap(_def.getAnnotationMap());

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
					page.getComponentDefinition(cls, true);
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
		_xtrl = newExtraCtrl();
		_modChildren = new AbstractSequentialList() {
			public int size() {
				return _children.size();
			}
			public ListIterator listIterator(int index) {
				return new ChildIter(index);
			}
		};
		_newChildren = new LinkedList();

		if (!cloning)
			_attrs = new HashMap(7);
	}

	/** Adds to the ID spaces, if any, when ID is changed.
	 * Caller has to make sure the uniqueness.
	 */
	private static void addToIdSpaces(final Component comp) {
		if (comp instanceof IdSpace)
			((AbstractComponent)comp).bindToIdSpace(comp);

		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component)
			((AbstractComponent)is).bindToIdSpace(comp);
		else if (is != null)
			((PageCtrl)is).addFellow(comp);
	}
	private static final IdSpace getSpaceOwnerOfParent(Component comp) {
		final Component parent = comp.getParent();
		if (parent != null) return parent.getSpaceOwner();
		else return comp.getPage();
	}
	/** Removes from the ID spaces, if any, when ID is changed. */
	private static void removeFromIdSpaces(final Component comp) {
		final String compId = ((AbstractComponent)comp)._id;
		if (compId == null || ComponentsCtrl.isAutoId(compId))
			return; //nothing to do

		if (comp instanceof IdSpace)
			((AbstractComponent)comp).unbindFromIdSpace(compId);

		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component)
			((AbstractComponent)is).unbindFromIdSpace(compId);
		else if (is != null)
			((PageCtrl)is).removeFellow(comp);
	}
	/** Checks the uniqueness in ID space when changing ID. */
	private static void checkIdSpaces(final Component comp, String newId) {
		if (comp instanceof IdSpace
		&& ((AbstractComponent)comp)._spaceInfo.fellows.containsKey(newId))
			throw new UiException("Not unique in the ID space of "+comp);

		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component) {
			if (((AbstractComponent)is)._spaceInfo.fellows.containsKey(newId))
				throw new UiException("Not unique in the ID space of "+is);
		} else if (is != null) {
			if (((PageCtrl)is).hasFellow(newId))
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
			addToIdSpacesDown(comp, (PageCtrl)is);
	}
	private static void addToIdSpacesDown(Component comp, Component owner) {
		if (!ComponentsCtrl.isAutoId(comp.getId()))
			((AbstractComponent)owner).bindToIdSpace(comp);
		if (!(comp instanceof IdSpace))
			for (Iterator it = comp.getChildren().iterator(); it.hasNext();)
				addToIdSpacesDown((Component)it.next(), owner); //recursive
	}
	private static void addToIdSpacesDown(Component comp, PageCtrl owner) {
		if (!ComponentsCtrl.isAutoId(comp.getId()))
			owner.addFellow(comp);
		if (!(comp instanceof IdSpace))
			for (Iterator it = comp.getChildren().iterator(); it.hasNext();)
				addToIdSpacesDown((Component)it.next(), owner); //recursive
	}

	/** Adds its descendants to the ID space when parent or page is changed,
	 * excluding comp.
	 */
	private static void removeFromIdSpacesDown(Component comp) {
		final IdSpace is = getSpaceOwnerOfParent(comp);
		if (is instanceof Component)
			removeFromIdSpacesDown(comp, (Component)is);
		else if (is != null)
			removeFromIdSpacesDown(comp, (PageCtrl)is);
	}
	private static void removeFromIdSpacesDown(Component comp, Component owner) {
		final String compId = comp.getId();
		if (!ComponentsCtrl.isAutoId(compId))
			((AbstractComponent)owner).unbindFromIdSpace(compId);
		if (!(comp instanceof IdSpace))
			for (Iterator it = comp.getChildren().iterator(); it.hasNext();)
				removeFromIdSpacesDown((Component)it.next(), owner); //recursive
	}
	private static void removeFromIdSpacesDown(Component comp, PageCtrl owner) {
		if (!ComponentsCtrl.isAutoId(comp.getId()))
			owner.removeFellow(comp);
		if (!(comp instanceof IdSpace))
			for (Iterator it = comp.getChildren().iterator(); it.hasNext();)
				removeFromIdSpacesDown((Component)it.next(), owner); //recursive
	}

	/** Checks the uniqueness in ID space when changing parent. */
	private static void checkIdSpacesDown(Component comp, Component newparent) {
		final IdSpace is = newparent.getSpaceOwner();
		if (is instanceof Component)
			checkIdSpacesDown(comp, ((AbstractComponent)is)._spaceInfo);
		else if (is != null)
			checkIdSpacesDown(comp, (PageCtrl)is);
	}
	/** Checks comp and its descendants for the specified SpaceInfo. */
	private static void checkIdSpacesDown(Component comp, SpaceInfo si) {
		final String compId = comp.getId();
		if (!ComponentsCtrl.isAutoId(compId) && si.fellows.containsKey(compId))
			throw new UiException("Not unique in the new ID space: "+compId);
		if (!(comp instanceof IdSpace))
			for (Iterator it = comp.getChildren().iterator(); it.hasNext();)
				checkIdSpacesDown((Component)it.next(), si); //recursive
	}
	/** Checks comp and its descendants for the specified page. */
	private static void checkIdSpacesDown(Component comp, PageCtrl pageCtrl) {
		final String compId = comp.getId();
		if (!ComponentsCtrl.isAutoId(compId) && pageCtrl.hasFellow(compId))
			throw new UiException("Not unique in the ID space of "+pageCtrl+": "+compId);
		if (!(comp instanceof IdSpace))
			for (Iterator it = comp.getChildren().iterator(); it.hasNext();)
				checkIdSpacesDown((Component)it.next(), pageCtrl); //recursive
	}

	/** Bind comp to this ID space (owned by this component).
	 * Called only if IdSpace is implemented.
	 */
	private void bindToIdSpace(Component comp) {
		final String compId = comp.getId();
		//assert D.OFF || !ComponentsCtrl.isAutoId(compId): "Auto ID shall be ignored: "+compId;
		_spaceInfo.fellows.put(compId, comp);
		if (Names.isValid(compId))
			_spaceInfo.ns.setVariable(compId, comp, true);
	}
	/** Unbind comp from this ID space (owned by this component).
	 * Called only if IdSpace is implemented.
	 */
	private void unbindFromIdSpace(String compId) {
		_spaceInfo.fellows.remove(compId);
		if (Names.isValid(compId))
			_spaceInfo.ns.unsetVariable(compId, true);
	}

	//-- Extra utlities --//
	/** Returns the mold URI based on {@link #getMold}
	 * and the molds defined in the component definition
	 * ({@link ComponentDefinition}).
	 *
	 * <p>Used usually for component implementation.
	 */
	protected String getMoldURI() {
		return _def.getMoldURI(this, getMold());
	}

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

	/** Sets the page that this component belongs to. */
	public void setPage(Page page) {
		if (page == _page) return;

		if (_parent != null)
			throw new UiException("Only the parent of a root component can be changed: "+this);
		if (page != null) {
			if (_page != null && _page.getDesktop() != page.getDesktop())
				throw new UiException("The new page must be in the same desktop: "+page);
				//Not allow developers to access two desktops simutaneously
			checkIdSpacesDown(this, (PageCtrl)page);

			//No need to check UUID since checkIdSpacesDown covers it
			//-- a page is an ID space
		} else { //detach from a page
			checkDetach(_page);
		}

		if (_page != null) removeFromIdSpacesDown(this);

		addMoved(this, _parent, _page, page); //Not depends on UUID
		setPage0(page); //UUID might be changed here

		if (_page != null) addToIdSpacesDown(this);
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
	/** Calling getUiEngine().addMoved().
	 */
	private static final
	void addMoved(Component comp, Component oldparent, Page oldpg, Page newpg) {
		final Desktop dt;
		if (oldpg != null) dt = oldpg.getDesktop();
		else if (newpg != null) dt = newpg.getDesktop();
		else return;

		((WebAppCtrl)dt.getWebApp())
			.getUiEngine().addMoved(comp, oldparent, oldpg, newpg);
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
			if (bRoot) ((PageCtrl)_page).removeRoot(this);
			if (page == null) {
				((DesktopCtrl)_page.getDesktop()).removeComponent(this);
			}
		}

		final Page oldpage = _page;
		_page = page;

		//attach
		if (_page != null) {
			if (bRoot) ((PageCtrl)_page).addRoot(this); //Not depends on uuid
			final Desktop desktop = _page.getDesktop();
			if (oldpage == null) {
				if (_uuid == null || _uuid == ComponentsCtrl.ANONYMOUS_ID) {
					_uuid = nextUuid(desktop);
				} else if (desktop.getComponentByUuidIfAny(_uuid) != null) {
				//re-assign uuid since it is moved from other desktop
					getThisUiEngine().addUuidChanged(this, true);

					//stupid but no better way to find a correct UUID yet
					//also, it is rare so performance not an issue
					do {
						_uuid = nextUuid(desktop);
					} while (desktop.getComponentByUuidIfAny(_uuid) != null);
//					if (D.ON && log.finerable()) log.finer("Uuid changed: "+this);
				}
				if (_id == null || _id == ComponentsCtrl.ANONYMOUS_ID)
					_id = _uuid;

				((DesktopCtrl)desktop).addComponent(this); //depends on uuid
			}
		}
		if (_spaceInfo != null && _parent == null)
			_spaceInfo.ns.setParent(page != null ? page.getNamespace(): null);

		//process all children recursively
		for (final Iterator it = _children.iterator(); it.hasNext();) {
			final Object child = it.next();
			((AbstractComponent)child).setPage0(page); //recursive
		}
	}

	private void initUuid() {
		if (_uuid == null) {
			final Execution exec = Executions.getCurrent();
			_uuid = exec == null ?
				ComponentsCtrl.ANONYMOUS_ID: nextUuid(exec.getDesktop());
			if (_id == null || _id == ComponentsCtrl.ANONYMOUS_ID)
				_id = _uuid;
		}
	}
	private String nextUuid(Desktop desktop) {
		final IdGenerator idgen =
			((WebAppCtrl)desktop.getWebApp()).getIdGenerator();
		final String uuid =
			idgen != null ? idgen.nextComponentUuid(desktop, this): null;
		return uuid != null ? uuid: ((DesktopCtrl)desktop).getNextUuid();
	}
	public final String getId() {
		initUuid();
		return _id;
	}
	public void setId(String id) {
		if (id == null || id.length() == 0)
			throw new UiException("ID cannot be empty");

		if (!Objects.equals(_id, id)) {
			if (Names.isReserved(id) || ComponentsCtrl.isAutoId(id))
				throw new UiException("Invalid ID: "+id+". Cause: reserved words not allowed: "+Names.getReservedNames());

			final boolean rawId = this instanceof RawId;
			if (rawId && _page != null
			&& _page.getDesktop().getComponentByUuidIfAny(id) != null)
				throw new UiException("Replicated ID is not allowed for "+getClass()+": "+id+"\nNote: HTML/WML tags, ID must be unique");

			checkIdSpaces(this, id);

			removeFromIdSpaces(this);
			if (rawId) { //we have to change UUID
				if (_page != null) {
					getThisUiEngine().addUuidChanged(this, false);
						//called before uuid is changed
					((DesktopCtrl)_page.getDesktop()).removeComponent(this);
				} else if (_uuid != null
				&& _uuid != ComponentsCtrl.ANONYMOUS_ID) {
					final Execution exec = Executions.getCurrent();
					if (exec != null)
						((WebAppCtrl)exec.getDesktop().getWebApp())
							.getUiEngine().addUuidChanged(this, true);
				}

				_uuid = _id = id;

				if (_page != null) {
					((DesktopCtrl)_page.getDesktop()).addComponent(this);
					addMoved(this, _parent, _page, _page);
				}
			} else {
				_id = id;
			}
			addToIdSpaces(this);

			final Object xc = getExtraCtrl();
			if ((xc instanceof ZidRequired) && ((ZidRequired)xc).isZidRequired())
				smartUpdate("z.zid", _id);
		}
	}

	public final String getUuid() {
		initUuid();
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

		final boolean idSpaceChanged;
		if (parent != null) {
			if (Components.isAncestor(this, parent))
				throw new UiException("A child cannot be a parent of its ancestor: "+this);
			if (!parent.isChildable())
				throw new UiException(parent+" doesn't allow any child");
			final Page newpage = parent.getPage();
			if (newpage != null && _page != null && newpage.getDesktop() != _page.getDesktop())
				throw new UiException("The new parent must be in the same desktop: "+parent);
					//Not allow developers to access two desktops simutaneously

			idSpaceChanged = parent.getSpaceOwner() !=
				(_parent != null ? _parent.getSpaceOwner(): _page);
			if (idSpaceChanged) checkIdSpacesDown(this, parent);

			//Note: No need to check UUID since checkIdSpacesDown covers it
			//-- a page is an ID space
		} else {
			idSpaceChanged = _page != null;
			if (idSpaceChanged)
				checkDetach(_page);
		}

		if (idSpaceChanged) removeFromIdSpacesDown(this);
		final Component oldparent = _parent;
		if (_parent != null) {
			_parent = null; //update first to avoid loop back
			oldparent.removeChild(this); //spec: removeChild must be called
		} else {
			if (_page != null)
				((PageCtrl)_page).removeRoot(this); //Not depends on uuid
		}

		if (parent != null) {
			_parent = parent; //update first to avoid loop back
			//We could use _parent.getChildren().contains instead, but
			//the following statement is much faster if a lot of children
			if (!((AbstractComponent)_parent)._newChildren.contains(this))
				parent.appendChild(this);
		} //if parent == null, assume no page at all (so no addRoot)

		final Page newpg = _parent != null ? _parent.getPage(): null;
		addMoved(this, oldparent, _page, newpg); //Not depends on UUID
		setPage0(newpg); //UUID might be changed here

		if (_spaceInfo != null) //ID space owner
			_spaceInfo.ns.setParent(
				_parent != null ? _parent.getNamespace(): null);
		if (idSpaceChanged) addToIdSpacesDown(this); //called after setPage
	}

	/** Default: return true (allows to have children).
	 */
	public boolean isChildable() {
		return true;
	}

	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild == null)
			throw new UiException("newChild is null");

		if (refChild != null && refChild.getParent() != this)
			refChild = null;

		if (newChild == refChild)
			return false; //nothing changed (Listbox and other assumes this)

		boolean found = false;
		if (_modChildByIter) {
			_modChildByIter = false; //avoid dead loop
		} else {
			boolean added = false;
			for (ListIterator it = _children.listIterator(); it.hasNext();) {
				final Object o = it.next();
				if (o == newChild) {
					if (!added) {
						if (!it.hasNext()) return false; //last
						if (it.next() == refChild) return false; //same position
						it.previous(); it.previous(); it.next(); //restore cursor
					}
					it.remove();
					found = true;
					if (added || refChild == null) break; //done
				} else if (o == refChild) {
					it.previous();
					it.add(newChild);
					it.next();
					added = true;
					if (found) break; //done
				}
			}

			if (!added) _children.add(newChild);
		}

		final AbstractComponent nc = (AbstractComponent)newChild;
		if (found) { //re-order
			addMoved(newChild, nc._parent, nc._page, _page);
				//Not to use getPage and getParent to avoid calling user's codes
				//if they override them
		} else { //new added
			if (nc._parent != this) { //avoid loop back
				_newChildren.add(newChild); //used by setParent to avoid loop back
				try {
					newChild.setParent(this); //call addMoved, setPage0...
				} finally {
					_newChildren.remove(newChild);
				}
			}
			onChildAdded(newChild);
		}
		return true;
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
		if (child == null)
			throw new UiException("child must be specified");

		if (_modChildByIter || _children.remove(child)) {
			_modChildByIter = false; //avoid dead loop

			if (child.getParent() != null) //avoid loop back
				child.setParent(null);
			onChildRemoved(child);
				//to invalidate itself if necessary
			return true;
		} else {
			return false;
		}
	}

	public List getChildren() {
		return _modChildren;
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
			smartUpdate("visibility", _visible);
		}
		return old;
	}

	public void invalidate() {
		if (_page != null)
			getThisUiEngine().addInvalidate(this);
	}
	public void response(String key, AuResponse response) {
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
	public void smartUpdate(String attr, String value) {
		if (_page != null) getThisUiEngine().addSmartUpdate(this, attr, value);
	}
	/** Smart-updates a property with a deferred value.
	 * A deferred value is used to encapsulate a value that shall be retrieved
	 * only in the rendering phase.
	 *
	 * @since 2.4.2
	 */
	public void smartUpdateDeferred(String attr, DeferredValue value) {
		if (_page != null) getThisUiEngine().addSmartUpdate(this, attr, value);
	}
	/** A special smart-update that update a value in int.
	 * <p>It will invoke {@link #smartUpdate(String,String)} to update
	 * the attribute eventually.
	 */
	public void smartUpdate(String attr, int value) {
		smartUpdate(attr, Integer.toString(value));
	}
	/** A special smart-update that update a value in boolean.
	 * <p>It will invoke {@link #smartUpdate(String,String)} to update
	 * the attribute eventually.
	 */
	public void smartUpdate(String attr, boolean value) {
		smartUpdate(attr, Boolean.toString(value));
	}

	public void detach() {
		if (getParent() != null) setParent(null);
		else setPage(null);
	}

	/** Default: does nothing.
	 */
	public void onChildAdded(Component child) {
	}
	/** Default: does nothing.
	 */
	public void onChildRemoved(Component child) {
	}

	/** Default: null (no propagation at all).
	 */
	public Component getPropagatee(String evtnm) {
		return null;
	}

	/**
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
			_mold = mold;
			invalidate();
		}
	}

	//-- in the redrawing phase --//
	/** Includes the page returned by {@link #getMoldURI} and
	 * set the self attribute to be this component.
	 */
	public void redraw(Writer out) throws IOException {
		final String mold = getMoldURI();
//		if (D.ON && log.finerable()) log.finer("Redraw comp: "+this+" with "+mold);

		final Map attrs = new HashMap(3);
		attrs.put("self", this);
		getExecution().include(out, mold, attrs, Execution.PASS_THRU_ATTR);
	}
	/* Default: does nothing.
	 */
	public void onDrawNewChild(Component child, StringBuffer out)
	throws IOException {
	}

	public boolean addEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new IllegalArgumentException("null");
		if (!Events.isValid(evtnm))
			throw new IllegalArgumentException("Invalid event name: "+evtnm);

		if (_listeners == null) _listeners = new HashMap();

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

		if (Events.ON_CLIENT_INFO.equals(evtnm))
			response("clientInfo", new AuClientInfo());
		return true;
	}
	public boolean removeEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new IllegalArgumentException("null");

		if (_listeners != null) {
			final List l = (List)_listeners.get(evtnm);
			if (l != null) {
				for (Iterator it = l.iterator(); it.hasNext();) {
					final EventListener li = (EventListener)it.next();
					if (listener.equals(li)) {
						if (l.size() == 1)
							_listeners.remove(evtnm);
						else
							it.remove();
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
		final EventHandler evthd = _evthds != null ? _evthds.get(evtnm): null;
		return evthd != null && evthd.isEffective(this) ? evthd.getZScript(): null;
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

			if (Events.isListened(this, Events.ON_CLIENT_INFO, false)) //asap+deferrable
				response("clientInfo", new AuClientInfo());
				//We always fire even not a root, since we don't like to
				//check when setParent or setPage is called
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

		for (Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			final AbstractComponent child = (AbstractComponent)it.next();
			sessionDidActivate0(page, child, pageLevelIdSpace); //recursive
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
		return _xtrl;
	}
	/** Used by {@link #getExtraCtrl} to create a client control.
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

	//-- Object --//
	public String toString() {
		final String clsnm = getClass().getName();
		final int j = clsnm.lastIndexOf('.');
		return "<"+clsnm.substring(j+1)+' '+_id+'>';
	}
	public final boolean equals(Object o) { //no more override
		return this == o;
	}

	/** Holds info shared of the same ID space. */
	private static class SpaceInfo {
		private Map attrs = new HashMap();
			//don't create it dynamically because _ip bind it at constructor
		private SimpleNamespace ns;
		/** A map of ((String id, Component fellow). */
		private Map fellows = new HashMap(41);

		private SpaceInfo(Component owner) {
			ns = new SimpleNamespace();
			init(owner);
		}
		private SpaceInfo(Component owner, SimpleNamespace from) {
			ns = new SimpleNamespace();
			ns.copy(from);
			init(owner);
		}
		private void init(Component owner) {
			ns.setVariable("spaceScope", attrs, true);
			ns.setVariable("spaceOwner", owner, true);
		}
	}

	private class ChildIter implements ListIterator  {
		private final ListIterator _it;
		private Object _last;
		private boolean _bNxt;
		private ChildIter(int index) {
			_it = _children.listIterator(index);
		}
		public void add(Object o) {
			final Component comp = (Component)o;
			if (comp.getParent() == AbstractComponent.this)
				throw new UnsupportedOperationException("Unable to add component with the same parent: "+o);
				//1. it is confusing to allow adding (with replace)
				//2. the code is complicated

			_it.add(o);

			//Note: we must go thru insertBefore because spec
			//(such that component need only to override removeChhild
			_modChildByIter = true;
			try {
				final Component ref;
				if (_bNxt) {
					if (_it.hasNext()) {
						ref = (Component)_it.next();
						_it.previous();
					} else
						ref = null;
				} else
					ref = (Component)_last;

				insertBefore(comp, ref);
			} finally {
				_modChildByIter = false;
			}
		}
		public boolean hasNext() {
			return _it.hasNext();
		}
		public boolean hasPrevious() {
			return _it.hasPrevious();
		}
		public Object next() {
			_bNxt = true;
			return _last = _it.next();
		}
		public Object previous() {
			_bNxt = false;
			return _last = _it.previous();
		}
		public int nextIndex() {
			return _it.nextIndex();
		}
		public int previousIndex() {
			return _it.previousIndex();
		}
		public void remove() {
			_it.remove();

			//Note: we must go thru removeChild because spec
			//(such that component need only to override removeChhild
			_modChildByIter = true;
			try {
				removeChild((Component)_last);
			} finally {
				_modChildByIter = false;
			}
		}
		public void set(Object o) {
			throw new UnsupportedOperationException("ChildIter's");
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

		clone._attrs = new HashMap();
		for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			Object val = me.getValue();
			if (val instanceof ComponentCloneListener) {
				val = ((ComponentCloneListener)val).clone(clone);
				if (val == null) continue; //don't use it in clone
			}
			clone._attrs.put(me.getKey(), val);
		}

		if (_listeners != null) {
			clone._listeners = new HashMap();
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

		//2. clone children (deep cloning)
		cloneChildren(clone);
		clone.init(true);

		//3. spaceinfo
		if (clone._spaceInfo != null) {
			clone._spaceInfo = new SpaceInfo(clone, _spaceInfo.ns);
			cloneSpaceInfo(clone, this._spaceInfo);
		}
		return clone;
	}
	private static final
	void cloneSpaceInfo(AbstractComponent clone, SpaceInfo from) {
		final SpaceInfo to = clone._spaceInfo;
		to.attrs = new HashMap();
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
		clone.bindToIdSpace(clone);
		for (Iterator it = clone.getChildren().iterator(); it.hasNext();)
			addToIdSpacesDown((Component)it.next(), clone);
	}
	private static final void cloneChildren(AbstractComponent comp) {
		final Iterator it = comp._children.iterator();
		comp._children = new LinkedList();
		while (it.hasNext()) {
			final AbstractComponent child = (AbstractComponent)
				((AbstractComponent)it.next()).clone(); //recursive
			child._parent = comp; //correct it
			if (child._spaceInfo != null)
				child._spaceInfo.ns.setParent(comp.getNamespace());
			comp._children.add(child);
		}
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		//No need to unshare since they are stored as an independent copy
		//unshareAnnotationMap(false);
		//unshareEventHandlerMap(false);

		s.defaultWriteObject();

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

		Object def = s.readObject();
		if (def == null) {
			_def = ComponentsCtrl.DUMMY;
		} else if (def instanceof String) {
			LanguageDefinition langdef = LanguageDefinition.lookup((String)def);
			_def = langdef.getComponentDefinition((String)s.readObject());
		} else {
			_def = (ComponentDefinition)def;
		}

		Serializables.smartRead(s, _attrs);
		didDeserialize(_attrs.values());

		for (;;) {
			final String evtnm = (String)s.readObject();
			if (evtnm == null) break; //no more

			if (_listeners == null) _listeners = new HashMap();
			final Collection ls = Serializables.smartRead(s, (Collection)null);
			_listeners.put(evtnm, ls);
			didDeserialize(ls);
		}

		//restore child's _parent
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final AbstractComponent child = (AbstractComponent)it.next();
			child._parent = this;
		}

		//restore _spaceInfo
		if (this instanceof IdSpace) {
			_spaceInfo = new SpaceInfo(this);

			//fix child's _spaceInfo's parent
			fixSpaceParentOneLevelDown(this, _spaceInfo.ns);

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
			bindToIdSpace(this);
			for (Iterator it = getChildren().iterator(); it.hasNext();)
				addToIdSpacesDown((Component)it.next(), this);
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
	/** Fixed Namespace's parent of children only one level.
	 */
	private static final
	void fixSpaceParentOneLevelDown(Component comp, Namespace nsparent) {
		for (Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			final AbstractComponent child = (AbstractComponent)it.next();
			//Others are handled by readObject
			if (child._spaceInfo != null)
				child._spaceInfo.ns.setParent(nsparent);
			else
				fixSpaceParentOneLevelDown(child, nsparent); //recursive
		}
	}
}
