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

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.metainfo.Annotation; 

import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.Iterator;

/**
 * <p>The DataBinder that reads ZUML annotations to create binding info. The ZUML page must declare the 
 * XML namespace, xmlns:a="http://www.zkoss.org/2005/zk/annotation", to use the ZUML annotations. 
 * The annotation is declared before each Component. For example, the following annotation associates the 
 * attibute "value" of the component "textbox" to the bean expression "person.address.city".</p>
 * <pre>
 * <a:bind value="person.address.city"/>
 * <textbox/>
 * </pre>
 * <p></p>
 *
 * <p>The AnnotateDataBinder knows "a:bind" annotation only. The complete format is like this:
 * <pre>
 * <a:bind attrY="bean-expression;[access-mode];[TypeConverter-class-name]"/>
 * <componentX/>
 * </pre>
 *
 * <p>This associates the componentX's attribute attrY to the bean-expression. The bean-expression is something
 * in the form of bean.field1.field2... You can either call {@link DataBinder#bindBean} to bind the bean id to a
 * real bean object or you can omit it and this DataBinder would try to find it from the variables map via
 * ({@link org.zkoss.zk.ui.Component#getVariable} method. That is, all those variables defined in zscript are 
 * accessible by this DataBinder.</p>
 *
 * <p>You can set the access-mode of the attrY of the componentX to be "rw"(Read/Write),  
 * "ro"(Read Only), or "wo"(Write Only). The access-mode would affect the behavior of the DataBinder's loadXxx
 * and saveXxx methods.
 * The {@link DataBinder#loadAll} and {@link DataBinder#loadComponent} would load only those attributes
 * with "rw" or "ro" access mode. The {@link DataBinder#saveAll} and 
 * {@link DataBinder#saveComponent} would save only those attributes with "rw" or "wo" access mode. If you
 * don't specify it, the default access mode depends on the natural characteristic of the component's attribute.
 * For example, Label.value is default to "ro" access mode while Textbox.value is default to "rw" access mode.</p>
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
				String[] expr = parseExpression(comp, attr, (String) me.getValue());
				addBinding(comp, attr, expr[0], expr[1], expr[2]);
			}
		}
		
		List children = comp.getChildren();
		for (final Iterator it = children.iterator(); it.hasNext(); ) {
			loadComponentAnnotation((Component) it.next()); //recursive back
		}
	}

	private String[] parseExpression(Component comp, String attr, String expr) {
		String[] results = myParseExpression(expr);
		for(int j=0; j <= 2; ++j) {
			if (results[j] != null) {
				results[j] = results[j].trim();
				if (results[j].length() == 0)
					results[j] = null;
			}
			if (j == 0 && results[0] == null) {
				throw new UiException("Cannot find any bean expression in the annotation <a:bind "+attr+"=\"\"/> for component "+comp+", id="+comp.getId());
			}
		}
		return results;
	}
	
	private String[] myParseExpression(String expr) {
		String[] results = new String[3]; //[0] bean expression, [1] access mode, [2] converter class name
		for (int k = 0; k < 2; ++k) {
			int j = expr.indexOf(";");
			if (j < 0) {
				results[k] = expr;
				return results;
			}
			results[k] = expr.substring(0, j);
			if (results[k].length() == 0) {
				results[k] = null;
			}
			if (expr.length() <= (j+1)) {
				return results;
			}
			expr = expr.substring(j+1);
		}
		results[2] = expr;
		return results;
	}
}
