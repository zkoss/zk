/* Toolbarbutton.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/08/16  18:10:17 , Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.context.FacesContext;

import org.zkoss.jsf.zul.impl.BaseCommand;
import org.zkoss.jsf.zul.impl.BranchComponent;
import org.zkoss.zk.ui.Component;

/**
 * Toolbarbutton is a JSF component implementation for {@link org.zkoss.zul.Toolbarbutton}, 
 * 
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * This class also implements {@link javax.faces.component.ActionSource}.
 * That means you can use action and actionListener features on this component.
 * <br/>
 * To use those features, you must declare a namespace of "http://java.sun.com/jsf/core" 
 * with a prefix (say 'f' in below example), add attribute of those feature with this namespace 
 * (for example f:required="true")in the jsf page. 
 * For more detail of ActionSource features of JSF, you can refer to <a href="http://java.sun.com/products/jsp/">http://java.sun.com/products/jsp/</a>
 * 
 * <p/>
 * Example of action :<br/>
 * <pre><code>
 * &lt;z:toolbarbutton id=&quot;btn&quot; label=&quot;submit&quot; f:action=&quot;#{CommandBean.actionPerform}&quot;/&gt;
 * </code></pre>
 * 
 * <p/>
 * Example of actionListener :<br/>
 * <pre><code>
 * &lt;z:toolbarbutton id=&quot;btn&quot; label=&quot;submit&quot; f:actionListener=&quot;#{CommandBean.onActionPerform}&quot;/&gt;
 * </code></pre>
 * 
 * 
 * <p/>To know more ZK component features you can refer to <a href="http://www.zkoss.org/">http://www.zkoss.org/</a>
 *   
 * @author Dennis.Chen
 *
 */
public class Toolbarbutton extends BaseCommand {


	protected Component newComponent(Class use) throws Exception {
		return (Component) (use==null?new org.zkoss.zul.Toolbarbutton():use.newInstance());
	}
	
	protected void loadZULTree(StringWriter writer) throws IOException {
		super.loadZULTree(writer);
		if(hasListener()){
			org.zkoss.zul.Toolbarbutton comp = (org.zkoss.zul.Toolbarbutton)getZULComponent();
			FacesContext context = FacesContext.getCurrentInstance();
			String submitmethod=getJSSubmitMethodName(context);
			String submitscript=getJSSubmitScript(context);
			writer.write(submitscript);
			String href =comp.getHref();
			if(href==null){
				comp.setHref("javascript:onclick:"+submitmethod+"();");
			}
		}
	}
}
