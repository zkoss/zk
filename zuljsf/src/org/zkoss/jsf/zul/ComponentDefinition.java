/* ComponentDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/11/21    2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul;

import org.zkoss.jsf.zul.impl.BaseComponentDefinition;


/**
 * ComponentDefinition is a JSF component that register the custom component definition to ZUL page.
 * <p/>
 * Usage:<br/><code>
 * <br/>&lt;z:component name=&quot;mywindow&quot; extends=&quot;window&quot; useClass=&quot;org.zkoss.jsfdemo.test.ForwardWindow&quot; title=&quot;Forward Window&quot; /&gt;
 * <br/>&lt;z:component name=&quot;mybox&quot; macroURI=&quot;/test/macro-mybox.zul&quot; title=&quot;My Box&quot; /&gt;
 * <br/>&lt;z:page&gt;
 * <br/> ...
 * <br/>&lt;/z:page&gt;
 * </code>
 * <p/>
 * Note : ComponentDefinition must be declared before page component.  
 * 
 * <p/>To know more ZK component features you can refer to <a href="http://www.zkoss.org/">http://www.zkoss.org/</a>
 *   
 * @author Dennis.Chen
 *
 */
public class ComponentDefinition extends BaseComponentDefinition {
	
}
