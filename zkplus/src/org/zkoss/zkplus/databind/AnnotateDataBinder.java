/* AnnotateDataBinder.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 16 13:22:37     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.metainfo.Annotation; 

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Iterator;

/**
 * <p>The DataBinder that reads ZUML annotations to create binding info. The ZUML page must declare the 
 * XML namespace, xmlns:a="http://www.zkoss.org/2005/zk/annotation", to use the ZUML annotations. 
 * The annotation is declared before each Component. For example, the following annotation associates the 
 * attibute "value" of the component "textbox" to the bean expression "person.address.city".</p>
 * <pre>
 * &lt;a:bind value="person.address.city"/>
 * &lt;textbox/>
 * </pre>
 * <p></p>
 *
 * <p>The AnnotateDataBinder knows "a:bind" annotation only. The complete format is like this:
 * <pre>
 * &lt;a:bind attrY="bean-expression;[load-event-list];[access-mode];[TypeConverter-class-name]"/>
 * &lt;componentX/>
 * </pre>
 *
 * <p>This associates the componentX's attribute attrY to the bean-expression. The bean-expression is something
 * in the form of beanid.field1.field2... You can either call {@link DataBinder#bindBean} to bind the beanid to a
 * real bean object or you can neglect it and this DataBinder would try to find it from the variables map via
 * ({@link org.zkoss.zk.ui.Component#getVariable} method. That is, all those variables defined in zscript are 
 * accessible by this DataBinder.</p>
 *
 * <p>The load-event-list is used to register event listeners to call data binding methods loadXxx. Note 
 * that the events is triggered by other components. The following annotations example associates the 
 * "value" attribute of Label "fullname" to the "onChange" events of the Textbox "firstname" and "lastname"; 
 * therefore, whenever Textbox "firstname" or "lastname" changed, the onChange event would trigger the event
 * listener to load the attribute "value" of the Label "fullname" automatically.</p>
 * <pre>
 * &lt;a:bind value="person.firstName"/>
 * &lt;textbox id="firstname"/>
 *
 * &lt;a:bind value="person.lastName"/>
 * &lt;textbox id="lastname"/>
 *
 * &lt;a:bind value="person.fullName; firstname.onChange, lastname.onChange"/>
 * &lt;label id="fullname"/>
 * </pre>
 * 
 * <p>You can set the access-mode of the attrY of the componentX to be "both"(load/save),  
 * "load"(load Only), or "save"(save Only). The access-mode would affects the behavior of the DataBinder's loadXxx
 * and saveXxx methods.
 * The {@link DataBinder#loadAll} and {@link DataBinder#loadComponent} would load only those attributes
 * with "both" or "load" access mode. The {@link DataBinder#saveAll} and 
 * {@link DataBinder#saveComponent} would save only those attributes with "both" or "save" access mode. If you
 * don't specify it, the default access mode depends on the natural characteristic of the component's attribute.
 * For example, Label.value is default to "load" access mode while Textbox.value is default to "both" access mode.</p>
 *
 * <p>The TypeConverter-class-name is the class name of {@link TypeConverter} implementation. It is used to 
 * convert the value type between component attribute and bean field. Most of the time you don't have to specify
 * this since this DataBinder supports converting most commonly used types. However, if you specify the 
 * TypeConverter class name, this DataBinder will new an instance and use it to cast the class.</p>
 * 
 * @author Henri Chen
 * @see AnnotateDataBinderInit
 * @see DataBinder
 */
public class AnnotateDataBinder extends DataBinder {
	/**
	 * Constructor that read all binding annotations of the components inside the specified desktop.
	 * @param desktop the ZUML desktop.
	 */
	public AnnotateDataBinder(Desktop desktop) {
		for (final Iterator	it = desktop.getComponents().iterator(); it.hasNext(); ) {
			loadComponentAnnotation((Component) it.next());
		}			
	}
	
	/**
	 * Constructor that read all binding annotations of the components inside the specified page.
	 * @param page the ZUML page.
	 */
	public AnnotateDataBinder(Page page) {
		for (final Iterator it = page.getRoots().iterator(); it.hasNext(); ) {
			loadComponentAnnotation((Component) it.next());
		}
	}
	
	/**
	 * Constructor that read all binding annotations in the components inside the specified component (inclusive).
	 * @param comp the ZUML component.
	 */
	public AnnotateDataBinder(Component comp) {
		loadComponentAnnotation(comp);
	}
	
	private void loadComponentAnnotation(Component comp) {
		ComponentCtrl compCtrl = (ComponentCtrl) comp;
		Annotation ann = compCtrl.getAnnotation("bind");
		if (ann != null) {
			Map attrs = ann.getAttributes();
			for(final Iterator it = attrs.entrySet().iterator(); it.hasNext();) {
				Entry me = (Entry) it.next();
				String attr = (String) me.getKey();
				//[0] bean expression, [1] event list, [2] access mode, [3] converter class name, 
				String[] expr = parseExpression((String) me.getValue(), ";");
				if (expr == null) {
					throw new UiException("Cannot find any bean expression in the annotation <a:bind "+attr+"=\"\"/> for component "+comp+", id="+comp.getId());
				}
				String[] eventList = expr.length > 1 && expr[1] != null ? parseExpression(expr[1], ",") : null;
				addBinding(comp, attr, expr[0], eventList, expr.length > 2 ? expr[2] : null, expr.length > 3 ? expr[3] : null);
			}
		}
		
		List children = comp.getChildren();
		for (final Iterator it = children.iterator(); it.hasNext(); ) {
			loadComponentAnnotation((Component) it.next()); //recursive back
		}
	}
	
	private String[] parseExpression(String expr, String seperator) {
		List list = myParseExpression(expr, seperator);
		String[] results = new String[list.size()];
		int j = 0;
		for(final Iterator it = list.iterator(); it.hasNext(); ++j) {
			String result = (String) it.next();
			if (result != null) {
				result  = result.trim();
				if (result.length() == 0)
					result = null;
			}
			if (j == 0 && result == null) {
				return null;
			}
			results[j] = result;
		}
		return results;
	}
	
	private List myParseExpression(String expr, String separator) {
		List results = new ArrayList(5);
		while(true) {
			int j = expr.indexOf(separator);
			if (j < 0) {
				results.add(expr);
				return results;
			}
			results.add(expr.substring(0, j));

			if (expr.length() <= (j+1)) {
				return results;
			}
			expr = expr.substring(j+1);
		}
	}
}
