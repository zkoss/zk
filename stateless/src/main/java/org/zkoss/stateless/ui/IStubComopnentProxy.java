/* IStubComopnentProxy.java

	Purpose:
		
	Description:
		
	History:
		2:15 PM 2021/10/14, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import static org.zkoss.zk.ui.sys.ComponentsCtrl.DUMMY;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.zkoss.stateless.ui.util.VolatileIPage;
import org.zkoss.stateless.ui.util.StatelessContentRenderer;
import org.zkoss.stateless.ui.util.StatelessEventListenerMap;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.zpr.IChildable;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IComposite;
import org.zkoss.stateless.zpr.ISingleChildable;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.EventListenerMap;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.xel.ExValue;

/**
 * A proxy of IStubComponent for {@link IComponent}
 * @author jumperchen
 */
/*package*/ class IStubComponentProxy extends IStubComponent {
	private IComponent _root;
	private StatelessEventListenerMap _zevts;

	/*package*/ IStubComponentProxy(IComponent root) {
		_root = root;
		_zevts = VolatileIPage.removeVolatileEventListenerMap(((ExecutionCtrl) Executions.getCurrent()).getCurrentPage(), root);

		genChildren(this);
	}

	public IComponent getOwner() {
		return _root;
	}

	private static void genChildren(IStubComponentProxy root) {

		// IComposition includes IChildable and ISingleChildable
		if (root._root instanceof IComposite) {
			List<IComponent> children = ((IComposite) root._root).getAllComponents();
			if (!children.isEmpty()) {
				for (int i = 0, j = children.size(); i < j; i++) {
					root.appendChild(new IStubComponentProxy(children.get(i)));
				}
			}
		} else {
			if (root._root instanceof IChildable) {
				List<IComponent> children = ((IChildable) root._root).getChildren();
				if (!children.isEmpty()) {
					for (int i = 0, j = children.size(); i < j; i++) {
						root.appendChild(new IStubComponentProxy(children.get(i)));
					}
				}
			}
			if (root._root instanceof ISingleChildable) {
				IComponent children = ((ISingleChildable) root._root).getChild();
				if (children != null) {
					root.appendChild(new IStubComponentProxy(children));
				}
			}
		}
	}

	public String getMold() {
		return _root.getMold();
	}

	public String getWidgetClass() {
		return _root.getWidgetClass();
	}

	public EventListenerMap getEventListenerMap() {
		return _zevts;
	}

	public String getId() {
		return _root.getId();
	}

	public ComponentDefinition getDefinition() {
		// patch to work with Selectors.find() with widget name.
		// i.e. Button.class -> button
		return new DummyComponentDefinition(_root.getZKType());
	}

	protected void renderProperties(ContentRenderer renderer)
			throws IOException {
		final StatelessContentRenderer statelessContentRenderer = new StatelessContentRenderer(
				renderer, _root, this);
		_root.renderProperties(statelessContentRenderer);
	}

	public void addAction(String name, ActionHandler handler) {
		// lazy init
		if (_zevts == null) {
			_zevts = new StatelessEventListenerMap(_root.getEventListenerMap());
		}
		_zevts.addAction(name, handler);
	}

	private static class DummyComponentDefinition implements ComponentDefinition {
		final private ComponentDefinition delegator = DUMMY;
		final private Class zkComp;
		DummyComponentDefinition(Class comp) {
			this.zkComp = comp;
		}

		public LanguageDefinition getLanguageDefinition() {
			return delegator.getLanguageDefinition();
		}

		public String getName() {
			return zkComp.getSimpleName().toLowerCase();
		}

		public String getTextAs() {
			return delegator.getTextAs();
		}

		public boolean isChildAllowedInTextAs() {
			return delegator.isChildAllowedInTextAs();
		}

		public boolean isBlankPreserved() {
			return delegator.isBlankPreserved();
		}

		public boolean isMacro() {
			return delegator.isMacro();
		}

		public String getMacroURI() {
			return delegator.getMacroURI();
		}

		public boolean isInlineMacro() {
			return delegator.isInlineMacro();
		}

		public boolean isNative() {
			return delegator.isNative();
		}

		public boolean isShadowElement() {
			return delegator.isShadowElement();
		}

		public Object getImplementationClass() {
			return delegator.getImplementationClass();
		}

		public void setImplementationClass(Class<? extends Component> cls) {
			delegator.setImplementationClass(cls);
		}

		public void setImplementationClass(String clsnm) {
			delegator.setImplementationClass(clsnm);
		}

		public Class<?> resolveImplementationClass(Page page, String clsnm)
				throws ClassNotFoundException {
			return delegator.resolveImplementationClass(page, clsnm);
		}

		public boolean isInstance(Component comp) {
			return delegator.isInstance(comp);
		}

		public Component newInstance(Page page, String clsnm) {
			return delegator.newInstance(page, clsnm);
		}

		public Component newInstance(Class<? extends Component> cls) {
			return delegator.newInstance(cls);
		}

		public void addMold(String name, String widgetClass) {
			delegator.addMold(name, widgetClass);
		}

		public String getWidgetClass(Component comp, String moldName) {
			return delegator.getWidgetClass(comp, moldName);
		}

		public String getDefaultWidgetClass(Component comp) {
			return delegator.getDefaultWidgetClass(comp);
		}

		public void setDefaultWidgetClass(String widgetClass) {
			delegator.setDefaultWidgetClass(widgetClass);
		}

		public boolean hasMold(String name) {
			return delegator.hasMold(name);
		}

		public Collection<String> getMoldNames() {
			return delegator.getMoldNames();
		}

		public void addProperty(String name, String value) {
			delegator.addProperty(name, value);
		}

		public void applyProperties(Component comp) {
			delegator.applyProperties(comp);
		}

		public void applyAttributes(Component comp) {
			delegator.applyAttributes(comp);
		}

		public Map<String, Object> evalProperties(Map<String, Object> propmap,
				Page owner, Component parent) {
			return delegator.evalProperties(propmap, owner, parent);
		}

		public AnnotationMap getAnnotationMap() {
			return delegator.getAnnotationMap();
		}

		public String getApply() {
			return delegator.getApply();
		}

		public ExValue[] getParsedApply() {
			return delegator.getParsedApply();
		}

		public void setApply(String apply) {
			delegator.setApply(apply);
		}

		public URL getDeclarationURL() {
			return delegator.getDeclarationURL();
		}

		public ComponentDefinition clone(LanguageDefinition langdef,
				String name) {
			return delegator.clone(langdef, name);
		}

		public Object clone() {
			return delegator.clone();
		}
	}
}
