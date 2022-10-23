/* VolatileComponent.java

	Purpose:
		
	Description:
		
	History:
		2:51 PM 2021/10/13, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.EventHandlerMap;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.EventListenerMap;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zk.ui.util.Callback;
import org.zkoss.zk.ui.util.Template;

/**
 * a volatile component for AU update
 * @author jumperchen
 */
public abstract class VolatileComponent implements Component, ComponentCtrl {
	public String getWidgetClass() {
		throw new UnsupportedOperationException();
	}

	public void setWidgetClass(String s) {
		throw new UnsupportedOperationException();
	}

	public ComponentDefinition getDefinition() {
		throw new UnsupportedOperationException();
	}

	public IdSpace getSpaceOwner() {
		throw new UnsupportedOperationException();
	}

	public String getId() {
		throw new UnsupportedOperationException();
	}

	public void setId(String s) {
		throw new UnsupportedOperationException();
	}


	public void setPage(Page page) {
		throw new UnsupportedOperationException();
	}

	public void setPageBefore(Page page, Component component) {
		throw new UnsupportedOperationException();
	}

	public Component getFellow(String s) {
		throw new UnsupportedOperationException();
	}

	public Component getFellow(String s, boolean b)
			throws ComponentNotFoundException {
		throw new UnsupportedOperationException();
	}

	public Component getFellowIfAny(String s) {
		throw new UnsupportedOperationException();
	}

	public Component getFellowIfAny(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public Collection<Component> getFellows() {
		throw new UnsupportedOperationException();
	}

	public boolean hasFellow(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public boolean hasFellow(String s) {
		throw new UnsupportedOperationException();
	}

	public Component getNextSibling() {
		throw new UnsupportedOperationException();
	}

	public Component getPreviousSibling() {
		throw new UnsupportedOperationException();
	}

	public Component getFirstChild() {
		throw new UnsupportedOperationException();
	}

	public Component getLastChild() {
		throw new UnsupportedOperationException();
	}

	public Map<String, Object> getAttributes(int i) {
		throw new UnsupportedOperationException();
	}

	public Map<String, Object> getAttributes() {
		throw new UnsupportedOperationException();
	}

	public Object getAttribute(String s, int i) {
		throw new UnsupportedOperationException();
	}

	public Object getAttribute(String s) {
		throw new UnsupportedOperationException();
	}

	public Object getAttribute(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public boolean hasAttribute(String s, int i) {
		throw new UnsupportedOperationException();
	}

	public boolean hasAttribute(String s) {
		throw new UnsupportedOperationException();
	}

	public boolean hasAttribute(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public Object setAttribute(String s, Object o, int i) {
		throw new UnsupportedOperationException();
	}

	public Object setAttribute(String s, Object o) {
		throw new UnsupportedOperationException();
	}

	public Object setAttribute(String s, Object o, boolean b) {
		throw new UnsupportedOperationException();
	}

	public Object removeAttribute(String s, int i) {
		throw new UnsupportedOperationException();
	}

	public Object removeAttribute(String s) {
		throw new UnsupportedOperationException();
	}

	public Object removeAttribute(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public boolean addScopeListener(ScopeListener scopeListener) {
		throw new UnsupportedOperationException();
	}

	public boolean removeScopeListener(ScopeListener scopeListener) {
		throw new UnsupportedOperationException();
	}

	public Object getAttributeOrFellow(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public Object getShadowVariable(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public Object getShadowVariable(Component component, String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public boolean hasAttributeOrFellow(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public String getStubonly() {
		throw new UnsupportedOperationException();
	}

	public void setStubonly(String s) {
		throw new UnsupportedOperationException();
	}

	public void setStubonly(boolean b) {
		throw new UnsupportedOperationException();
	}

	public Component getParent() {
		return null;
	}

	public void setParent(Component component) {
		throw new UnsupportedOperationException();
	}

	public <T extends Component> List<T> getChildren() {
		throw new UnsupportedOperationException();
	}

	public Component getRoot() {
		throw new UnsupportedOperationException();
	}

	public boolean isVisible() {
		throw new UnsupportedOperationException();
	}

	public boolean setVisible(boolean b) {
		throw new UnsupportedOperationException();
	}

	public boolean insertBefore(Component component, Component component1) {
		throw new UnsupportedOperationException();
	}

	public boolean appendChild(Component component) {
		throw new UnsupportedOperationException();
	}

	public boolean removeChild(Component component) {
		throw new UnsupportedOperationException();
	}

	public void detach() {
		throw new UnsupportedOperationException();
	}

	public String getMold() {
		throw new UnsupportedOperationException();
	}

	public void setMold(String s) {
		throw new UnsupportedOperationException();
	}

	public boolean addEventListener(int i, String s,
			EventListener<? extends Event> eventListener) {
		throw new UnsupportedOperationException();
	}

	public boolean addEventListener(String s,
			EventListener<? extends Event> eventListener) {
		throw new UnsupportedOperationException();
	}

	public boolean removeEventListener(String s,
			EventListener<? extends Event> eventListener) {
		throw new UnsupportedOperationException();
	}

	public boolean isListenerAvailable(String s, boolean b) {
		throw new UnsupportedOperationException();
	}

	public Iterator<EventListener<? extends Event>> getListenerIterator(
			String s) {
		throw new UnsupportedOperationException();
	}

	public Iterable<EventListener<? extends Event>> getEventListeners(
			String s) {
		throw new UnsupportedOperationException();
	}

	public boolean addForward(String s, Component component, String s1) {
		throw new UnsupportedOperationException();
	}

	public boolean addForward(String s, String s1, String s2) {
		throw new UnsupportedOperationException();
	}

	public boolean addForward(String s, Component component, String s1,
			Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean addForward(String s, String s1, String s2, Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean removeForward(String s, Component component, String s1) {
		throw new UnsupportedOperationException();
	}

	public boolean removeForward(String s, String s1, String s2) {
		throw new UnsupportedOperationException();
	}

	public boolean isInvalidated() {
		throw new UnsupportedOperationException();
	}

	public void invalidate() {
		throw new UnsupportedOperationException();
	}

	public void applyProperties() {
		throw new UnsupportedOperationException();
	}

	public String setWidgetListener(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	public String getWidgetListener(String s) {
		throw new UnsupportedOperationException();
	}

	public Set<String> getWidgetListenerNames() {
		throw new UnsupportedOperationException();
	}

	public String setWidgetOverride(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	public String getWidgetOverride(String s) {
		throw new UnsupportedOperationException();
	}

	public Set<String> getWidgetOverrideNames() {
		throw new UnsupportedOperationException();
	}

	public String setWidgetAttribute(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	public String getWidgetAttribute(String s) {
		throw new UnsupportedOperationException();
	}

	public String setClientAttribute(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	public String getClientAttribute(String s) {
		throw new UnsupportedOperationException();
	}

	public String setClientDataAttribute(String s, String s1) {
		throw new UnsupportedOperationException();
	}

	public String getClientDataAttribute(String s) {
		throw new UnsupportedOperationException();
	}

	public Set<String> getWidgetAttributeNames() {
		throw new UnsupportedOperationException();
	}

	public Template getTemplate(String s) {
		throw new UnsupportedOperationException();
	}

	public Template setTemplate(String s, Template template) {
		throw new UnsupportedOperationException();
	}

	public Set<String> getTemplateNames() {
		throw new UnsupportedOperationException();
	}

	public void setAuService(AuService auService) {
		throw new UnsupportedOperationException();
	}

	public AuService getAuService() {
		throw new UnsupportedOperationException();
	}

	public String getAutag() {
		throw new UnsupportedOperationException();
	}

	public void setAutag(String s) {
		throw new UnsupportedOperationException();
	}

	public Object clone() {
		throw new UnsupportedOperationException();
	}

	public Component query(String s) {
		throw new UnsupportedOperationException();
	}

	public Iterable<Component> queryAll(String s) {
		throw new UnsupportedOperationException();
	}

	public void setDefinition(ComponentDefinition compdef) {
		throw new UnsupportedOperationException();
	}

	public void setDefinition(String defname) {
		throw new UnsupportedOperationException();
	}

	public void beforeChildAdded(Component child, Component insertBefore) {
		throw new UnsupportedOperationException();
	}

	public void beforeChildRemoved(Component child) {
		throw new UnsupportedOperationException();
	}

	public void beforeParentChanged(Component parent) {
		throw new UnsupportedOperationException();
	}

	public void onChildAdded(Component child) {
		throw new UnsupportedOperationException();
	}

	public void onChildRemoved(Component child) {
		throw new UnsupportedOperationException();
	}

	public void onPageAttached(Page newpage, Page oldpage) {
		throw new UnsupportedOperationException();
	}

	public void onPageDetached(Page page) {
		throw new UnsupportedOperationException();
	}

	public ZScript getEventHandler(String evtnm) {
		throw new UnsupportedOperationException();
	}

	public void addEventHandler(String name, EventHandler evthd) {
		throw new UnsupportedOperationException();
	}

	public void addSharedEventHandlerMap(EventHandlerMap evthds) {
		throw new UnsupportedOperationException();
	}

	public Set<String> getEventHandlerNames() {
		throw new UnsupportedOperationException();
	}

	public Map<String, Integer> getClientEvents() {
		throw new UnsupportedOperationException();
	}

	public Annotation getAnnotation(String annotName) {
		throw new UnsupportedOperationException();
	}

	public Annotation getAnnotation(String propName, String annotName) {
		throw new UnsupportedOperationException();
	}

	public Collection<Annotation> getAnnotations(String propName,
			String annotName) {
		throw new UnsupportedOperationException();
	}

	public Collection<Annotation> getAnnotations() {
		throw new UnsupportedOperationException();
	}

	public Collection<Annotation> getAnnotations(String propName) {
		throw new UnsupportedOperationException();
	}

	public List<String> getAnnotatedPropertiesBy(String annotName) {
		throw new UnsupportedOperationException();
	}

	public List<String> getAnnotatedProperties() {
		throw new UnsupportedOperationException();
	}

	public void addAnnotation(String annotName,
			Map<String, String[]> annotAttrs) {
		throw new UnsupportedOperationException();
	}

	public void addAnnotation(String propName, String annotName,
			Map<String, String[]> annotAttrs) {
		throw new UnsupportedOperationException();
	}

	public void sessionWillPassivate(Page page) {
		throw new UnsupportedOperationException();
	}

	public void sessionDidActivate(Page page) {
		throw new UnsupportedOperationException();
	}

	public Object getExtraCtrl() {
		throw new UnsupportedOperationException();
	}

	public PropertyAccess getPropertyAccess(String prop) {
		throw new UnsupportedOperationException();
	}

	public WrongValueException onWrongValue(WrongValueException ex) {
		throw new UnsupportedOperationException();
	}

	public void redraw(Writer out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void service(AuRequest request, boolean everError) {
		throw new UnsupportedOperationException();
	}

	public void service(Event event, Scope scope) throws Exception {
		throw new UnsupportedOperationException();
	}

	public boolean disableClientUpdate(boolean disable) {
		throw new UnsupportedOperationException();
	}

	public EventListenerMap getEventListenerMap() {
		throw new UnsupportedOperationException();
	}

	public <T extends ShadowElement> List<T> getShadowRoots() {
		throw new UnsupportedOperationException();
	}

	public boolean removeShadowRoot(ShadowElement shadow) {
		throw new UnsupportedOperationException();
	}

	public boolean addShadowRoot(ShadowElement shadow) {
		throw new UnsupportedOperationException();
	}

	public boolean addShadowRootBefore(ShadowElement shadow,
			ShadowElement insertBefore) {
		throw new UnsupportedOperationException();
	}

	public void enableBindingAnnotation() {
		throw new UnsupportedOperationException();
	}

	public void disableBindingAnnotation() {
		throw new UnsupportedOperationException();
	}

	public boolean hasBindingAnnotation() {
		throw new UnsupportedOperationException();
	}

	public boolean hasSubBindingAnnotation() {
		throw new UnsupportedOperationException();
	}

	public int getSubBindingAnnotationCount() {
		throw new UnsupportedOperationException();
	}

	public boolean addRedrawCallback(Callback<ContentRenderer> callback) {
		throw new UnsupportedOperationException();
	}

	public boolean removeRedrawCallback(Callback<ContentRenderer> callback) {
		throw new UnsupportedOperationException();
	}

	public Collection<Callback<ContentRenderer>> getRedrawCallback() {
		throw new UnsupportedOperationException();
	}

	public boolean addCallback(String name, Callback callback) {
		throw new UnsupportedOperationException();
	}

	public boolean removeCallback(String name, Callback callback) {
		throw new UnsupportedOperationException();
	}

	public Collection<Callback> getCallback(String name) {
		throw new UnsupportedOperationException();
	}

	public ShadowElement getShadowFellowIfAny(String id) {
		throw new UnsupportedOperationException();
	}

	public void invalidatePartial(String subId) {
		throw new UnsupportedOperationException();
	}

	public void invalidatePartial() {
		throw new UnsupportedOperationException();
	}

	public int hashCode() {
		String uuid = getUuid();
		if (uuid != null) {
			return uuid.hashCode();
		}
		return super.hashCode();
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof VolatileComponent) {
			return Objects.equals(getUuid(), ((VolatileComponent) o).getUuid());
		}
		return false;
	}
}