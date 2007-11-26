/* Button.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8, 2007 5:48:27 PM     2007, Created by Dennis.Chen
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.impl.XulElement;

/**
 * Button is a JSF component implementation for {@link org.zkoss.zul.Button}, 
 * 
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * This class also implements {@link javax.faces.component.ActionSource}.
 * That means you can use action and actionListener features on this component.
 * <br/>
 * To use those features, you must declare a namespace of "http://java.sun.com/jsf/core" 
 * with a prefix (say 'f' in below example), add attribute of those feature with this namespace 
 * (for example f:required="true")in you jsf page. 
 * For more detail of ActionSource features of JSF, you can refer to <a href="http://java.sun.com/products/jsp/">http://java.sun.com/products/jsp/</a>
 * 
 * <p/>
 * Example of action :<br/>
 * <pre><code>
 * &lt;z:button id=&quot;btn&quot; label=&quot;submit&quot; f:action=&quot;#{CommandBean.actionPerform}&quot;/&gt;
 * </code></pre>
 * 
 * <p/>
 * Example of actionListener :<br/>
 * <pre><code>
 * &lt;z:button id=&quot;btn&quot; label=&quot;submit&quot; f:actionListener=&quot;#{CommandBean.onActionPerform}&quot;/&gt;
 * </code></pre>
 * <p/>
 * In some application server which doesn't support attribute namespace you can use attribute prefix 'f_' to replace attribute namespace
 * <br/>
 * For example, 
 * <pre>
 * &lt;z:button f_action=&quot;#{CommandBean.actionPerform}&quot;/&gt;
 * </pre>
 * 
 * <p/>
 * <p/>To know more ZK component features you can refer to <a href="http://www.zkoss.org/">http://www.zkoss.org/</a>
 *   
 * @author Dennis.Chen
 *
 */
public class Button extends BaseCommand {

	
	protected void loadZULTree(org.zkoss.zk.ui.Page page,StringWriter writer) throws IOException {
		super.loadZULTree(page,writer);
		if(hasListener()){
			//add onclick action
			org.zkoss.zul.Button comp = (org.zkoss.zul.Button)getZULComponent();
			FacesContext context = FacesContext.getCurrentInstance();
			String submitmethod=getJSSubmitMethodName(context);
			String submitscript=getJSSubmitScript(context);
			writer.write(submitscript);
			String oa =comp.getAction();
			comp.setAction((oa==null?"":oa+";")+"onclick:"+submitmethod+"();");
		}
	}
}
