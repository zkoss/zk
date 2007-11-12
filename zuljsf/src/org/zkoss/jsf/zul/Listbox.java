/* Listbox.java

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

import org.zkoss.jsf.zul.impl.BaseListbox;
import org.zkoss.zk.ui.Component;

/**
 * Listbox is a JSF component implementation for {@link org.zkoss.zul.Listbox}, 
 * This class also implements {@link javax.faces.component.EditableValueHolder}.
 * That means you can use bidirection value binding, immediate, required, converter, validator, valueChangeListener features on this component.
 * <br/>
 * To use those features, you must decleare a namespace of "http://java.sun.com/jsf/core" 
 * with a prefix (say 'f' in below example), add attribute of those feature with this namespace 
 * (for example f:required="true")in you jsf page. 
 * For more detail of EditableValueHolder features of JSF, you can refer to <a href="http://java.sun.com/products/jsp/">http://java.sun.com/products/jsp/</a>
 * 
 * <p/>
 * You must assign a value on each listitem, so that, after user select listitem and submitting,
 * listbox will decode the request parameter and set back the submitted value to your bean.
 * <p/>
 * When listbox set multiple to false(single selection), then 
 * the default binding value of this component is {@link java.lang.String}.
 * When listbox set multiple ot true(multiple selection, then
 * the default binding value of this component is {@link java.lang.String} array.
 * 
 * <p/>
 * Example of use bidirection value binding:<br/>
 * <pre>
 &lt;z:listbox id=&quot;role1&quot; f:value=&quot;#{yourBean.value}&quot;&gt;
	&lt;z:listitem value=&quot;1&quot; label=&quot;Determine need&quot; /&gt;
	&lt;z:listitem value=&quot;2&quot; label=&quot;Evaluate products/sesrvices&quot; /&gt;
	&lt;z:listitem value=&quot;3&quot; label=&quot;Recommend products/sesrvices&quot; /&gt;
	&lt;z:listitem value=&quot;4&quot; label=&quot;Implement products/sesrvices&quot; /&gt;
	&lt;z:listitem value=&quot;5&quot; label=&quot;Techinical decision maker&quot; /&gt;
	&lt;z:listitem value=&quot;6&quot; label=&quot;Financial decision maker&quot; /&gt;
&lt;/z:listbox&gt;
 * </pre>
 * 
 * 
 * <p/>
 * Example of using immediate:<br/>
 * <pre>
 * &lt;z:listbox f:immediate=&quot;true&quot; /&gt;
 * </pre>
 * 
 * <p/>
 * Example of using required:<br/>
 * <pre>
 * &lt;z:listbox f:required=&quot;true&quot; /&gt;
 * </pre>
 * <p/>
 * Example of using converter:<br/>
 * <pre>
 * &lt;z:listbox f:converter=&quot;yourBean.convertMethod&quot;/&gt;
 * or
 * &lt;z:listbox &gt;
 * 	&lt;f:converter converterId=&quot;yourConverterId&quot;/&gt;
 * &lt;/z:listbox&gt;
 * </pre>
 * <p/>
 * Example of using validator:<br/>
 * <pre>
 * &lt;z:listbox f:validator=&quot;yourBean.validateMethod&quot;/&gt;
 * or
 * &lt;z:listbox &gt;
 * 	&lt;f:validator validatorId=&quot;yourValidatorId&quot;/&gt;
 * &lt;/z:listbox&gt;
 *  </pre>
 * <p/>
 * Example of using converter:<br/>
 * <pre>
 * &lt;z:listbox &gt;
 * 	&lt;f:valueChangeListener type=&quot;your.ValueChangeListener&quot;/&gt;
 * &lt;/z:listbox&gt
 * </pre>
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * 
 * <p/>To know more ZK component features you can refer to <a href="http://www.zkoss.org/">http://www.zkoss.org/</a>
 *    
 * @author Dennis.Chen
 * @see org.zkoss.zul.Listbox
 * @see javax.faces.component.EditableValueHolder
 */
public class Listbox extends BaseListbox {

	protected Component newComponent(Class use) throws Exception {
		return (Component) (use == null ? new org.zkoss.zul.Listbox() : use.newInstance());
	}
}
