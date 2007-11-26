/* Intbox.java

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

import org.zkoss.jsf.zul.impl.BaseIntbox;
import org.zkoss.zk.ui.Component;
/**
 * Intbox is a JSF component implementation for {@link org.zkoss.zul.Intbox}, 
 * This class also implements {@link javax.faces.component.EditableValueHolder}.
 * That means you can use bidirectional value binding, immediate, required, converter, validator, valueChangeListener features on this component.
 * <br/>
 * To use those features, you must declare a namespace of "http://java.sun.com/jsf/core" 
 * with a prefix (say 'f' in below example), add attribute of those feature with this namespace 
 * (for example f:required="true")in you jsf page. 
 * For more detail of EditableValueHolder features of JSF, you can refer to <a href="http://java.sun.com/products/jsp/">http://java.sun.com/products/jsp/</a>
 * 
 * <p/>
 * The default binding value of this component is {@link java.lang.Integer}
 * 
 * <p/>
 * Example of use bidirectional value binding:<br/>
 * <pre>
 * &lt;z:intbox f:value=&quot;#{yourBean.value}&quot; /&gt;
 * </pre>
 * 
 * 
 * <p/>
 * Example of using immediate:<br/>
 * <pre>
 * &lt;z:intbox f:immediate=&quot;true&quot; /&gt;
 * </pre>
 * 
 * <p/>
 * Example of using required:<br/>
 * <pre>
 * &lt;z:intbox f:required=&quot;true&quot; /&gt;
 * </pre>
 * <p/>
 * Example of using converter:<br/>
 * <pre>
 * &lt;z:intbox f:converter=&quot;yourBean.convertMethod&quot;/&gt;
 * or
 * &lt;z:intbox &gt;
 * 	&lt;f:converter converterId=&quot;yourConverterId&quot;/&gt;
 * &lt;/z:intbox&gt;
 * </pre>
 * <p/>
 * Example of using validator:<br/>
 * <pre>
 * &lt;z:intbox f:validator=&quot;yourBean.validateMethod&quot;/&gt;
 * or
 * &lt;z:intbox &gt;
 * 	&lt;f:validator validatorId=&quot;yourValidatorId&quot;/&gt;
 * &lt;/z:intbox&gt;
 *  </pre>
 * <p/>
 * Example of using converter:<br/>
 * <pre>
 * &lt;z:intbox &gt;
 * 	&lt;f:valueChangeListener type=&quot;your.ValueChangeListener&quot;/&gt;
 * &lt;/z:intbox&gt
 * </pre>
 * <p/>
 * In some application server which doesn't support attribute namespace you can use attribute prefix 'f_' to replace attribute namespace
 * <br/>
 * For example, 
 * <pre>
 * &lt;z:intbox f_value=&quot;#{yourBean.value}&quot; /&gt;
 * </pre>
 * 
 * <p/>
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * 
 * <p/>To know more ZK component features you can refer to <a href="http://www.zkoss.org/">http://www.zkoss.org/</a>
 *    
 * @author Dennis.Chen
 * @see org.zkoss.zul.Intbox
 * @see javax.faces.component.EditableValueHolder
 */
public class Intbox extends BaseIntbox{


}
