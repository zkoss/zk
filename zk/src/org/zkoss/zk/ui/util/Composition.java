/* Composistion.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 03, 2008 12:59:01 PM , Created by henrichen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;


/**
 * <p>Composition manager that compose the given components into a whole page per 
 * the "insert" components and "define" components annotations. The "define" 
 * components will attach itself on to the "insert" component with the same annotated
 * joinId. That is, the "insert" component is the parent component of the "define" components
 * with the same annotated joinId. Note that "insert" components can NOT have duplicate 
 * joinId in a page while "define" components can because you can have multiple child components 
 * but you cannot have multiple parent components. Also note that a "define" components must be
 * a root component of the page.</p>
 * 
 * <p>This Composition manager is useful when you need to do layout injection or you want to 
 * design a common page template across multiple pages.<p>
 * <ul>
 * <li>You first design a template with "insert" components telling where the insert points are.
 * Each insert component has to be given a distinguish joinId in a page(e.g. &lt;window self="@{insert(content)}"/>; 
 * here the "content" is the joinId).</li>
 * <li>Then in the real page, you have root "define" components telling which "define" 
 *  components are to be attach onto the "insert" component with the same joinId 
 *  (e.g. &lt;label self="@{define(content)}"; here the "content" is the joinId).</li>
 * <li>This Composition class is designed as a page {@link Initiator} and a {@link InitiatorExt}, so 
 * you have to specify in the real page as following to use it.  
 * <pre> 
 * &lt;?init class="org.zkoss.zk.ui.util.Composition" [arg0="TEMPLATE1"[, arg1="TEMPLATE2"]...]?>
 * ...
 * </pre>
 * Where the arg0 ~ argx you can give zul template uri. This implementation use Excecutions.createComponents()
 * to create them and then do the real composition in the {@link InitiatorExt#doAfterCompose(Page, Component[])}.</li>
 * <li>If more than one "define" components have the same joinId, they are attached onto the "insert" component
 * in the sequence of the definition.</li>
 * <li>If a "define" component cannot find the corresponding "insert" component, it will be simply detached from the page.</li>
 * <li>Also you can prepare a parent component and pass it in via 
 * Execution.getCurrent().setAttribute(Composition.PARENT, parent) then
 * this implementation will attach finally composed root components as the children of the provided parent.</li>
 * <li>If you did not provide the parent component, the finally composed root components are attached directly to the current page.</li>  
 * </ul>
 * 
 * @author henrichen
 * @since 3.5.2
 */
public class Composition implements Initiator, InitiatorExt {
	private static final String RESOLVE_COMPOSITION = "zk.ui.util.RESOLVE_COMPOSITION";
	public static final String PARENT = "zk.ui.util.PARENT";
	
	public void doAfterCompose(Page page) throws Exception {
		//never called here
	}

	public boolean doCatch(Throwable ex) throws Exception {
		// do nothing
		return false;
	}

	public void doFinally() throws Exception {
		// do nothing
	}

	public void doInit(Page page, Object[] args) throws Exception {
		//first called doInit, last called doAfterCompose
		final Execution exec = Executions.getCurrent();
		if (exec.getAttribute(RESOLVE_COMPOSITION) == null) {
			exec.setAttribute(RESOLVE_COMPOSITION, this);
		}
		final Component parent = (Component) exec.getAttribute(PARENT);
		for (int j=0; j < args.length; ++j) {
			exec.createComponents((String)args[j], parent, null);
		}
	}

	public void doAfterCompose(Page page, Component[] comps) throws Exception {
		final Execution exec = Executions.getCurrent();

		//resolve only once in the last page
		if (exec.getAttribute(RESOLVE_COMPOSITION) != this) {
			return;
		}
		exec.removeAttribute(RESOLVE_COMPOSITION);
		
		// resolve insert components
		final Map insertMap = new HashMap(); //(insert name, insert component)
		final Component parent = (Component) exec.getAttribute(PARENT);
		final Collection roots = parent == null ? page.getRoots() : parent.getChildren();
		resolveInsertComponents(roots, insertMap);

		if (!roots.isEmpty()) {
			Component comp = (Component) roots.iterator().next();
			
			// join "define" components as children of "insert" component
			do {
				final Component nextRoot = comp.getNextSibling();
				final Annotation annt = ((ComponentCtrl)comp).getAnnotation("define");
				if (annt != null) {
					final String joinId = annt.getAttribute("value");
					final Component insertComp = (Component) insertMap.get(joinId);
					if (insertComp != null) {
						comp.setParent(insertComp);
					} else {
						comp.detach(); //no where to insert
					}
				}
				comp = nextRoot;
			} while (comp != null);
		}
	}
	
	private void resolveInsertComponents(Collection comps, Map map) {
		for (final Iterator it = comps.iterator(); it.hasNext(); ) {
			final Component comp = (Component) it.next();
			final Annotation annt = ((ComponentCtrl)comp).getAnnotation("insert");
			if (annt != null) {
				final String insertName = annt.getAttribute("value");
				if (map.containsKey(insertName)) {
					throw new UiException("Duplicate insert name: "+insertName+" at Component "+comp);
				}
				map.put(insertName, comp);
			}
			resolveInsertComponents(comp.getChildren(), map); //recursive
		}
	}
}
