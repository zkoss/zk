/* BranchTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8 10:00:10     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.tag.impl;

/**
 * The skeletal class for implementing the generic JSF tags.
 * @author Dennis.Chen
 */
abstract public class BranchTag extends LeafTag {
	/**
	 * constructor.
	 * @param typeName a type name with will be concatenate a {@link #COMP_TYPE_PREFIX},return at {@link #getComponentType}, 
	 */
	protected BranchTag(String typeName) {
		super(typeName);
	}

	
}
