/* AnnotateDataBinder.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 16 13:22:37     2006, Created by Henri@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.metainfo.Annotation; 

import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.Iterator;

/**
 * DataBinder that read ZUML annotations to create binding info.
 *
 * @author <a href="mailto:Henri@potix.com">Henri@potix.com</a>
 */
public class AnnotateDataBinder extends DataBinder {
	/**
	 * Constructor that read all binding annotations of the components inside the specified desktop.
	 * @param Desktop the ZUML desktop.
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
				String[] expr = parseExpression((String) me.getValue());
				addBinding(comp, attr, expr[0], expr[1], expr[2]);
			}
		}
		
		List children = comp.getChildren();
		for (final Iterator it = children.iterator(); it.hasNext(); ) {
			loadComponentAnnotation((Component) it.next()); //recursive back
		}
	}
	
	private String[] parseExpression(String expr) {
		String[] results = new String[3]; //[0] expression, [1] access, [2] converter class
		for (int k = 0; k < 2; ++k) {	
			int j = expr.indexOf(";");
			if (j < 0) {
				results[k] = expr.trim();
				return results;
			}
			results[k] = expr.substring(0, j).trim();
			if (expr.length() <= (j+1)) {
				return results;
			}
			expr = expr.substring(j+1);
		}
		results[2] = expr.trim();
		return results;
	}
}
