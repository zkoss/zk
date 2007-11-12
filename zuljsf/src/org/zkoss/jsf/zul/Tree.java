/* Tree.java

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

import org.zkoss.jsf.zul.impl.BaseTree;
import org.zkoss.zk.ui.Component;

/**
 * Tree is a JSF component implementation for {@link org.zkoss.zul.Tree}, 
 * This class also implements {@link javax.faces.component.EditableValueHolder}.
 * That means you can use bidirection value binding, immediate, required, converter, validator, valueChangeListener features on this component.
 * <br/>
 * To use those features, you must decleare a namespace of "http://java.sun.com/jsf/core" 
 * with a prefix (say 'f' in below example), add attribute of those feature with this namespace 
 * (for example f:required="true")in you jsf page. 
 * For more detail of EditableValueHolder features of JSF, you can refer to <a href="http://java.sun.com/products/jsp/">http://java.sun.com/products/jsp/</a>
 * 
 * <p/>
 * You must assign a value on each treeitem, so that, when page submit those value,
 * tree will decode the request parameter and set back to your bean.
 * <p/>
 * You must assign a value on each treeitem, so that, after user select treeitem and submitting,
 * tree will decode the request parameter and set back the submitted value to your bean.
 * 
 * <p/>
 * When listbox set multiple to false(single selection), then 
 * the default binding value of this component is {@link java.lang.String}.
 * When listbox set multiple ot true(multiple selection, then
 * the default binding value of this component is {@link java.lang.String} array.
 * 
 * <p/>
 * Example of use bidirection value binding:<br/>
 * <pre>
 * &lt;z:tree name=&quot;role1&quot; id=&quot;tree&quot; f:value=&quot;#{SelectionTestBean.selection}&quot;&gt;
 * 	&lt;z:treechildren&gt;
 * 		&lt;z:treeitem value=&quot;1&quot;&gt;
 * 			&lt;z:treerow&gt;&lt;z:treecell label=&quot;Item 1&quot; /&gt;&lt;/z:treerow&gt;
 * 		&lt;/z:treeitem&gt;
 * 		&lt;z:treeitem value=&quot;2&quot;&gt;
 * 			&lt;z:treerow&gt;
 * 				&lt;z:treecell label=&quot;Item 2&quot; /&gt;
 * 			&lt;/z:treerow&gt;
 * 			&lt;z:treechildren&gt;
 * 				&lt;z:treeitem value=&quot;3&quot;&gt;
 * 					&lt;z:treerow&gt;
 * 						&lt;z:treecell label=&quot;Item 2.1&quot; /&gt;
 * 					&lt;/z:treerow&gt;
 * 					&lt;z:treechildren&gt;
 * 						&lt;z:treeitem value=&quot;4&quot;&gt;
 * 							&lt;z:treerow&gt;
 * 								&lt;z:treecell label=&quot;Item 2.1.1&quot; /&gt;
 * 							&lt;/z:treerow&gt;
 * 						&lt;/z:treeitem&gt;
 * 					&lt;/z:treechildren&gt;
 * 				&lt;/z:treeitem&gt;
 * 			&lt;/z:treechildren&gt;
 * 		&lt;/z:treeitem&gt;
 * 	&lt;/z:treechildren&gt;
 * &lt;/z:tree&gt;	
 * </pre>
 * 
 * 
 * <p/>
 * Example of using immediate:<br/>
 * <pre>
 * &lt;z:tree f:immediate=&quot;true&quot; /&gt;
 * </pre>
 * 
 * <p/>
 * Example of using required:<br/>
 * <pre>
 * &lt;z:tree f:required=&quot;true&quot; /&gt;
 * </pre>
 * <p/>
 * Example of using converter:<br/>
 * <pre>
 * &lt;z:tree f:converter=&quot;yourBean.convertMethod&quot;/&gt;
 * or
 * &lt;z:tree &gt;
 * 	&lt;f:converter converterId=&quot;yourConverterId&quot;/&gt;
 * &lt;/z:tree&gt;
 * </pre>
 * <p/>
 * Example of using validator:<br/>
 * <pre>
 * &lt;z:tree f:validator=&quot;yourBean.validateMethod&quot;/&gt;
 * or
 * &lt;z:tree &gt;
 * 	&lt;f:validator validatorId=&quot;yourValidatorId&quot;/&gt;
 * &lt;/z:tree&gt;
 *  </pre>
 * <p/>
 * Example of using converter:<br/>
 * <pre>
 * &lt;z:tree &gt;
 * 	&lt;f:valueChangeListener type=&quot;your.ValueChangeListener&quot;/&gt;
 * &lt;/z:tree&gt
 * </pre>
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * 
 * <p/>To know more ZK component features you can refer to <a href="http://www.zkoss.org/">http://www.zkoss.org/</a>
 *    
 * @author Dennis.Chen
 * @see org.zkoss.zul.Tree
 * @see javax.faces.component.EditableValueHolder
 */
public class Tree extends BaseTree {


	protected Component newComponent(Class use) throws Exception {
		return (Component) (use==null?new org.zkoss.zul.Tree():use.newInstance());
	}
	
	
}
