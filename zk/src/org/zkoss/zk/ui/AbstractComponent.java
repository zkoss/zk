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
import org.zkoss.lang.Strings;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.RawId;
import org.zkoss.zk.ui.ext.render.Transparent;
import org.zkoss.zk.ui.ext.render.ZidRequired;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.Variables;
import org.zkoss.zk.ui.impl.Serializables;
import org.zkoss.zk.ui.metainfo.Milieu;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.metainfo.AnnotationMapImpl;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinitionMap;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuClientInfo;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Method;
import org.zkoss.zk.scripting.util.AbstractNamespace;

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
    private static final long serialVersionUID = 20060622L;

	/* Note: if _page != null, then _desktop != null, vice versa. */
	private transient Desktop _desktop;
	private transient Page _page;
	private String _id;
	private String _uuid;
	private Milieu _mil;
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
	/** A map of annotations. Serializable since a component might have
	 * its own annotations.
	 */
	private AnnotationMapImpl _annots;
	/** The extra controls. */
	private transient Object _xtrl;
	/** A set of children being added. It is used only to speed up
	 * the performance when adding a new child. And, cleared after added.
	 * <p>To save footprint, we don't use Set (since it is rare to contain
	 * more than one)
	 */
	private transient List _newChildren;
	/** Used when user is modifying the children by Iterator.
	 */
	private transient boolean _modChildByIter;
	/** Whether this component is visible. */
	private boolean _visible = true;

	/** Constructs a component with auto-generated ID.
	 */
	protected AbstractComponent() {
		final Execution exec = Executions.getCurrent();

		_mil = Milieu.getCurrent();
		if (_mil != null) Milieu.setCurrent(null); //to avoid mis-use
		else {
			final ComponentDefinition compdef = getDefinition(exec, getClass());
			if (compdef != null) _mil = compdef.getMilieu();
			else _mil = Milieu.DUMMY;
		}

		init(false);

		_uuid = _id = exec != null ?
			nextUuid(exec.getDesktop()): ComponentsCtrl.AUTO_ID_PREFIX;
			//though it doesn't belong to any desktop yet, we autogen uuid
			//it is optional but it is slightly better (of course, subjective)

		_spaceInfo = this instanceof IdSpace ? new SpaceInfo(this): null;

		if (D.ON && log.debugable()) log.debug("Create comp: "+this);
	}
	/** Generates the next UUID for the specified desktop.
	 */
	private static final String nextUuid(Desktop desktop) {
		final StringBuffer sb = new StringBuffer(12)
			.append(ComponentsCtrl.AUTO_ID_PREFIX);
		Strings.encode(sb, ((DesktopCtrl)desktop).getNextId());
		return sb.toString();
	}
	private static final
	ComponentDefinition getDefinition(Execution exec, Class cls) {
		if (exec != null) {
			final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
			final PageDefinition pgdef = execCtrl.getCurrentPageDefinition();
			final Page page = execCtrl.getCurrentPage();

			final ComponentDefinition compdef =
				pgdef != null ? pgdef.getComponentDefinition(cls, true):
					page.getComponentDefinition(cls, true);
			if (compdef != null) return compdef;

			return getDefinitionByClientType(exec.getDesktop().getClientType(), cls);
		}

		for (Iterator it = LanguageDefinition.getClientTypes().iterator(); it.hasNext();) {
			final ComponentDefinition compdef =
				getDefinitionByClientType((String)it.next(), cls);
			if (compdef != null)
				return compdef;
		}
		return null;
	}
	private static final ComponentDefinition
	getDefinitionByClientType(String clientType, Class cls) {
		for (Iterator it = LanguageDefinition.getByClientType(clientType).iterator();
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
		final String compId = comp.getId();
		if (ComponentsCtrl.isAutoId(compId))
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
		if (Variables.isValid(compId))
			_spaceInfo.ns.setVariable(compId, comp, true);
	}
	/** Unbind comp from this ID space (owned by this component).
	 * Called only if IdSpace is implemented.
	 */
	private void unbindFromIdSpace(String compId) {
		_spaceInfo.fellows.remove(compId);
		if (Variables.isValid(compId))
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
		return _mil.getMoldURI(this, getMold());
	}
	/** Returns the initial parameter in int, or 0 if not found.
	 * An initial parameter is a parameter defined with the
	 * component's definition {@link ComponentDefinition}.
	 *
	 * <p>It evaluates before returning, if it is an EL expression.
	 *
	 * <p>Used usually for component implementation.
	 */
	protected int getIntInitParam(String name) {
		final Integer v;
		final Object o = _mil.getParameter(this, name);
		if (o instanceof Integer) {
			v = (Integer)o;
		} else if (o != null) {
			v = Integer.valueOf(Objects.toString(o));
		} else {
			v = new Integer(0);
		}
		return v.intValue();
	}
	/** Returns the initial parameter, or null if not found.
	 * An initial parameter is a parameter defined with the
	 * component's definition {@link ComponentDefinition}.
	 *
	 * <p>It evaluates before returning, if it is an EL expression.
	 *
	 * <p>Used usually for component implementation.
	 */
	protected String getInitParam(String name) {
		return Objects.toString(_mil.getParameter(this, name));
	}

	/** Returns the UI engine based on {@link #_desktop}.
	 * Don't call this method when _desktop is null.
	 */
	private final UiEngine getThisUiEngine() {
		return ((WebAppCtrl)_desktop.getWebApp()).getUiEngine();
	}

	//-- Component --//
	public final Page getPage() {
		return _page;
	}
	public final Desktop getDesktop() {
		return _desktop;
	}

	/** Sets the page that this component belongs to. */
	public void setPage(Page page) {
		if (page == _page) return;

		if (_parent != null)
			throw new UiException("Only the parent of a root component can be changed: "+this);
		if (page != null) {
			if (page.getDesktop() != _desktop && _desktop != null)
				throw new UiException("The new page must be in the same desktop: "+page);
				//Not allow developers to access two desktops simutaneously
			checkIdSpacesDown(this, (PageCtrl)page);
		}

		if (_page != null) removeFromIdSpacesDown(this);

		addMoved(this, _parent, _page, page); //Not depends on UUID
		setPage0(page); //UUID might be changed here

		if (_page != null) addToIdSpacesDown(this);

		checkRootEvents(null);
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
				((DesktopCtrl)_desktop).removeComponent(this);
				_desktop = null;
			}
		}

		final Page oldpage = _page;
		_page = page;

		//attach
		if (_page != null) {
			if (bRoot) ((PageCtrl)_page).addRoot(this); //Not depends on uuid
			if (oldpage == null) {
				_desktop = _page.getDesktop();

				final boolean anonymous =
					ComponentsCtrl.AUTO_ID_PREFIX.equals(_uuid);
				if (anonymous || _desktop.getComponentByUuidIfAny(_uuid) != null) {
					if (!anonymous)
						getThisUiEngine().addUuidChanged(this, true);
						//called before uuid is changed

					//stupid but no better way to find a correct UUID yet
					//also, it is rare so performance not an issue
					do {
						_uuid = nextUuid(_desktop);
					} while (_desktop.getComponentByUuidIfAny(_uuid) != null);

					if (D.ON && log.finerable()) log.finer("Uuid changed: "+this);
				}

				((DesktopCtrl)_desktop).addComponent(this); //depends on uuid
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

	public final String getId() {
		return _id;
	}
	public void setId(String id) {
		if (id == null || id.length() == 0)
			throw new UiException("ID cannot be empty");

		if (!_id.equals(id)) {
			if (Variables.isReserved(id) || ComponentsCtrl.isAutoId(id))
				throw new UiException("Invalid ID: "+id+". Cause: reserved words not allowed: "+Variables.getReservedNames());

			final boolean rawId = this instanceof RawId;
			if (rawId && _desktop != null
			&& _desktop.getComponentByUuidIfAny(id) != null)
				throw new UiException("Replicated ID is not allowed for "+getClass()+": "+id+"\nNote: HTML/WML tags, ID must be unique");

			checkIdSpaces(this, id);

			removeFromIdSpaces(this);
			if (rawId) { //we have to change UUID
				if (_desktop != null) {
					getThisUiEngine().addUuidChanged(this, false);
						//called before uuid is changed
					((DesktopCtrl)_desktop).removeComponent(this);
				} else {
					final Execution exec = Executions.getCurrent();
					if (exec != null)
						((WebAppCtrl)exec.getDesktop().getWebApp())
							.getUiEngine().addUuidChanged(this, true);
				}

				_uuid = _id = id;

				if (_desktop != null) {
					((DesktopCtrl)_desktop).addComponent(this);
					if (_parent != null && isTransparent(this)) _parent.invalidate();
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
	private static boolean isTransparent(Component comp) {
		final Object xc = ((ComponentCtrl)comp).getExtraCtrl();
		return (xc instanceof Transparent) && ((Transparent)xc).isTransparent();
	}

	public final String getUuid() {
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
			return _desktop != null ?
				_desktop.getAttributes(): Collections.EMPTY_MAP;
		case SESSION_SCOPE:
			return _desktop != null ?
				_desktop.getSession().getAttributes(): Collections.EMPTY_MAP;
		case APPLICATION_SCOPE:
			return _desktop != null ?
				_desktop.getWebApp().getAttributes(): Collections.EMPTY_MAP;
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
		return _desktop != null ? _desktop.getExecution():
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
			if (newpage != null && newpage.getDesktop() != _desktop && _desktop != null)
				throw new UiException("The new parent must be in the same desktop: "+parent);
				//Not allow developers to access two desktops simutaneously

			idSpaceChanged = parent.getSpaceOwner() !=
				(_parent != null ? _parent.getSpaceOwner(): _page);
			if (idSpaceChanged) checkIdSpacesDown(this, parent);
		} else {
			idSpaceChanged = _page != null;
		}

		if (_parent != null && isTransparent(this)) _parent.invalidate();
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

		checkRootEvents(null);
	}

	/** Default: return true (allows to have children).
	 */
	public boolean isChildable() {
		return true;
	}

	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild == null)
			throw new UiException("newChild is null");

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
			if (isTransparent(newChild)) invalidate();
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
			if (!isTransparent(this))
				smartUpdate("visibility", _visible);
		}
		return old;
	}

	public void invalidate() {
		if (_page != null) {
			getThisUiEngine().addInvalidate(this);
				//always add even though _parent.invalidate might be called
				//reason: Transparent relationship might be changed

			if (_parent != null && isTransparent(this)) _parent.invalidate();
			//Note: UiEngine will handle transparent, but we still
			//handle it here to simplify codes that handles transparent
			//in AbstractComponent
		}
	}
	public void response(String key, AuResponse response) {
		//if response not depend on this component, it must be generated
		if (_desktop != null) {
			getThisUiEngine().addResponse(key, response);
		} else if (response.getDepends() != this) {
			final Execution exec = Executions.getCurrent();
			if (exec != null)
				((WebAppCtrl)exec.getDesktop().getWebApp())
					.getUiEngine().addResponse(key, response);
		}
	}
	public void smartUpdate(String attr, String value) {
		if (_parent != null && isTransparent(this))
			throw new IllegalStateException("A transparent component cannot use smartUpdate: "+attr+'='+value);
		if (_page != null) getThisUiEngine().addSmartUpdate(this, attr, value);
	}
	/** A special smart-update that update a value in int.
	 */
	public void smartUpdate(String attr, int value) {
		smartUpdate(attr, Integer.toString(value));
	}
	/** A special smart-update that update a value in boolean.
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
			if (!_mil.hasMold(mold))
				throw new UiException("Unknown mold: "+mold
					+", while allowed include "+_mil.getMoldNames());
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
		if (D.ON && log.finerable()) log.finer("Redraw comp: "+this+" with "+mold);

		final Map attrs = new HashMap(3);
		attrs.put("self", this);
		_desktop.getExecution()
			.include(out, mold, attrs, Execution.PASS_THRU_ATTR);
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

		checkRootEvents(evtnm);
		return true;
	}
	/** Checks special events for root components.
	 *
	 * @param evtnm which event is changed. If null, it means all have to check.
	 */
	private void checkRootEvents(String evtnm) {
		if (_desktop != null && _parent == null) {
			if ((evtnm == null || Events.ON_CLIENT_INFO.equals(evtnm))
			&& Events.isListenerAvailable(this, Events.ON_CLIENT_INFO, true))
				response("clientInfo", new AuClientInfo());
		}
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

	public Class getZScriptClass(String clsnm) {
		try {
			return Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			if (_page != null) {
				for (Iterator it = _page.getLoadedInterpreters().iterator();
				it.hasNext();) {
					Class cls = ((Interpreter)it.next()).getClass(clsnm);
					if (cls != null)
						return cls;
				}
			}
			return null;
		}
	}
	public Method getZScriptMethod(String name, Class[] argTypes) {
		if (_page != null) {
			for (Iterator it = _page.getLoadedInterpreters().iterator();
			it.hasNext();) {
				Method mtd = ((Interpreter)it.next()).getMethod(name, argTypes);
				if (mtd != null)
					return mtd;
			}
		}
		return null;
	}
	public Object getZScriptVariable(String name) {
		Object val = getVariable(name, false);
		if (val != null)
			return val;

		if (_page != null) {
			for (Iterator it = _page.getLoadedInterpreters().iterator();
			it.hasNext();) {
				val = ((Interpreter)it.next()).getVariable(name, true);
				if (val != null)
					return val;
			}
		}
		return null;
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
					if (li.isAsap())
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
				return l.iterator();
		}
		return CollectionsX.EMPTY_ITERATOR;
	}

	public void applyProperties() {
		_mil.applyProperties(this);
	}

	//-- ComponentCtrl --//
	public Milieu getMilieu() {
		return _mil;
	}
	public void setMilieu(Milieu milieu) {
		if (milieu == null)
			throw new IllegalArgumentException("null");
		if (!milieu.isInstance(this))
			throw new IllegalArgumentException("Incompatible "+milieu+" for "+this);
		_mil = milieu;
	}

	public Annotation getAnnotation(String annotName) {
		return annotmap().getAnnotation(annotName);
	}
	public Annotation getAnnotation(String propName, String annotName) {
		return annotmap().getAnnotation(propName, annotName);
	}
	public Collection getAnnotations() {
		return annotmap().getAnnotations();
	}
	public Collection getAnnotations(String propName) {
		return annotmap().getAnnotations(propName);
	}
	public List getAnnotatedPropertiesBy(String annotName) {
		return annotmap().getAnnotatedPropertiesBy(annotName);
	}
	public List getAnnotatedProperties() {
		return annotmap().getAnnotatedProperties();
	}
	public void addAnnotation(String annotName, Map annotAttrs) {
		if (_annots == null)
			_annots = (AnnotationMapImpl)getMilieu().getAnnotationMap().clone();
		_annots.addAnnotation(annotName, annotAttrs);
	}
	public void addAnnotation(String propName, String annotName, Map annotAttrs) {
		if (_annots == null)
			_annots = (AnnotationMapImpl)getMilieu().getAnnotationMap().clone();
		_annots.addAnnotation(propName, annotName, annotAttrs);
	}
	private AnnotationMap annotmap() {
		return _annots != null ? _annots: getMilieu().getAnnotationMap();
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
		comp._desktop = page.getDesktop();

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
		private NS ns;
		/** A map of ((String id, Component fellow). */
		private Map fellows = new HashMap(41);

		private SpaceInfo(Component owner) {
			ns = new NS();
			init(owner);
		}
		private SpaceInfo(Component owner, NS from) {
			ns = new NS();
			ns._vars.putAll(from._vars);
			init(owner);
		}
		private void init(Component owner) {
			ns.setVariable("spaceScope", attrs, true);
			ns.setVariable("spaceOwner", owner, true);
		}
	}

	private static class NS extends AbstractNamespace {
		private Namespace _parent;
		private final Map _vars;

		private NS() {
			_vars = new HashMap();
		}

		//Namespace//
		public Set getVariableNames() {
			return _vars.keySet();
		}
		public boolean containsVariable(String name, boolean local) {
			return _vars.containsKey(name)
			|| (!local && _parent != null && _parent.containsVariable(name, true));
		}
		public Object getVariable(String name, boolean local) {
			Object val = _vars.get(name);
			if (local || _parent == null || val != null || _vars.containsKey(name))
				return val;
			return _parent.getVariable(name, false);
		}
		public void setVariable(String name, Object value, boolean local) {
			if (!local && _parent != null && !_vars.containsKey(name)) {
				for (Namespace p = _parent;;) {
					if (p.getVariableNames().contains(name)) {
						p.setVariable(name, value, true);
						return; //done;
					}
					if ((p = p.getParent()) == null)
						break;
				}
			}

			_vars.put(name, value);
			notifyAdd(name, value);
		}
		public void unsetVariable(String name, boolean local) {
			if (_vars.remove(name) != null || _vars.containsKey(name)) {
				notifyRemove(name);
			} else if (!local && _parent != null) {
				for (Namespace p = _parent; p != null; p = p.getParent()) {
					if (p.getVariableNames().contains(name)) {
						p.unsetVariable(name, true);
						break;
					}
					if ((p = p.getParent()) == null)
						break;
				}
			}
		}

		public Namespace getParent() {
			return _parent;
		}
		public void setParent(Namespace parent) {
			if (_parent != parent) {
				for (Namespace p = parent; p != null; p = p.getParent())
					if (p == this)
						throw new IllegalArgumentException("Recursive namespace: "+this+" with "+parent);
				_parent = parent;
				notifyParentChanged(parent);
			}
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
		clone._desktop = null;
		clone._page = null;
		clone._parent = null;
		clone._attrs = new HashMap(_attrs);
		if (_listeners != null)
			clone._listeners = new HashMap(_listeners);
		if (_annots != null)
			clone._annots = (AnnotationMapImpl)_annots.clone();

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
		to.attrs.putAll(from.attrs);

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
		s.defaultWriteObject();

		Serializables.smartWrite(s, _attrs);

		if (_listeners != null)
			for (Iterator it = _listeners.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				s.writeObject(me.getKey());
				Serializables.smartWrite(s, (Collection)me.getValue());
			}
		s.writeObject(null);

		//store _spaceInfo
		if (this instanceof IdSpace) {
			//write _spaceInfo.attrs
			Serializables.smartWrite(s, _spaceInfo.attrs);

			//write _spaceInfo.ns (only variables that are not fellows)
			for (Iterator it = _spaceInfo.ns._vars.entrySet().iterator();
			it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String nm = (String)me.getKey();
				final Object val = me.getValue();
				if (isVariableSerializable(nm, val)
				&& (val instanceof java.io.Serializable || val instanceof java.io.Externalizable)) {
					s.writeObject(nm);
					s.writeObject(val);
				}
			}
			s.writeObject(null); //denote end-of-namespace
		}
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init(false);

		Serializables.smartRead(s, _attrs);

		for (;;) {
			final String evtnm = (String)s.readObject();
			if (evtnm == null) break; //no more

			if (_listeners == null) _listeners = new HashMap();
			_listeners.put(evtnm, Serializables.smartRead(s, (Collection)null));
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

			//_spaceInfo.ns
			for (;;) {
				final String nm = (String)s.readObject();
				if (nm == null) break; //no more

				Object val = s.readObject();
				_spaceInfo.ns.setVariable(nm, val, true);
			}

			//restore ID space by binding itself and all children
			bindToIdSpace(this);
			for (Iterator it = getChildren().iterator(); it.hasNext();)
				addToIdSpacesDown((Component)it.next(), this);
		}
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
