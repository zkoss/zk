/* Ui.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/11/21, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul;

import org.zkoss.jsf.zul.impl.BaseUi;

/**
 * MacroUi is a JSF component implementation for macro component 
 * 
 * Usage:<br/>
 * <code>
 * <br/>&lt;z:component name=&quot;mywindow&quot; extends=&quot;window&quot; useClass=&quot;org.zkoss.jsfdemo.test.ForwardWindow&quot; title=&quot;Forward Window&quot; /&gt;
 * <br/>&lt;z:component name=&quot;mybox&quot; macroURI=&quot;/test/macro-mybox.zul&quot; title=&quot;My Box&quot; /&gt;
 * <br/>&lt;z:page&gt;
 * <br/>  &lt;z:ui tag=&quot;mywindow&quot; border=&quot;normal&quot; /&gt;
 * <br/>  &lt;z:ui tag=&quot;mybox&quot; message=&quot;a box&quot; /&gt;
 * <br/>&lt;/z:page&gt;
 * </code>
 * <p/>
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * <p/>
 * Note:  
 * Ui doesn't support features such as ActionSource, ValueHolder, EditableValueHolder etc.
 * You should not binding any JSF attribute(such as f:value, f:actionPerform etc.) on a Ui because it is only a template. 
 * 
 * <p/>To know more ZK component features you can refer to <a href="http://www.zkoss.org/">http://www.zkoss.org/</a>
 *   
 * @author Dennis.Chen
 *
 */
public class Ui extends BaseUi {
	
}
