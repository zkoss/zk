/* Radiogroup.java

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

import org.zkoss.jsf.zul.impl.BaseRadiogroup;
import org.zkoss.zk.ui.Component;

/**
 * Radiogroup is a JSF component implementation for {@link org.zkoss.zul.Radiogroup}, 
 * This class also implements {@link javax.faces.component.EditableValueHolder}.
 * That means you can use bidirectional value binding, immediate, required, converter, validator, valueChangeListener features on this component.
 * <br/>
 * To use those features, you must declare a namespace of "http://java.sun.com/jsf/core" 
 * with a prefix (say 'f' in below example), add attribute of those feature with this namespace 
 * (for example f:required="true")in you jsf page. 
 * For more detail of EditableValueHolder features of JSF, you can refer to <a href="http://java.sun.com/products/jsp/">http://java.sun.com/products/jsp/</a>
 * 
 * <p/>
 * You must assign a value on each radio, so that, after user click radio and submitting,
 * radiogroup will decode the request parameter and set back the submitted value to your bean.
 * <p/>
 * The default binding value of this component is {@link java.lang.String}
 * 
 * <p/>
 * Example of use bidirectional value binding:<br/>
 * <pre>
 * &lt;z:radiogroup id=&quot;r2&quot; f:value=&quot;#{ConverterTestBean.value}&quot; &gt;
 * 	 &lt;z:radio value=&quot;A&quot; /&gt;
 * 	 &lt;z:radio value=&quot;B&quot; /&gt;
 * 	 &lt;z:radio value=&quot;C&quot; /&gt;
 * &lt;/z:radiogroup&gt;
 * </pre>
 * 
 * 
 * <p/>
 * Example of using immediate:<br/>
 * <pre>
 * &lt;z:radiogroup f:immediate=&quot;true&quot; /&gt;
 * </pre>
 * 
 * <p/>
 * Example of using required:<br/>
 * <pre>
 * &lt;z:radiogroup f:required=&quot;true&quot; /&gt;
 * </pre>
 * <p/>
 * Example of using converter:<br/>
 * <pre>
 * &lt;z:radiogroup f:converter=&quot;yourBean.convertMethod&quot;/&gt;
 * or
 * &lt;z:radiogroup &gt;
 * 	&lt;f:converter converterId=&quot;yourConverterId&quot;/&gt;
 * &lt;/z:radiogroup&gt;
 * </pre>
 * <p/>
 * Example of using validator:<br/>
 * <pre>
 * &lt;z:radiogroup f:validator=&quot;yourBean.validateMethod&quot;/&gt;
 * or
 * &lt;z:radiogroup &gt;
 * 	&lt;f:validator validatorId=&quot;yourValidatorId&quot;/&gt;
 * &lt;/z:radiogroup&gt;
 *  </pre>
 * <p/>
 * Example of using converter:<br/>
 * <pre>
 * &lt;z:radiogroup &gt;
 * 	&lt;f:valueChangeListener type=&quot;your.ValueChangeListener&quot;/&gt;
 * &lt;/z:radiogroup&gt
 * </pre>
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * 
 * <p/>To know more ZK component features you can refer to <a href="http://www.zkoss.org/">http://www.zkoss.org/</a>
 *    
 * @author Dennis.Chen
 * @see org.zkoss.zul.Radiogroup
 * @see javax.faces.component.EditableValueHolder
 */
public class Radiogroup extends BaseRadiogroup {



}
