/* EntityReference.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/m3/pxcommon/src/com/potix/idom/EntityReference.java,v 1.4 2006/05/11 07:16:22 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	2001/10/22 20:51:27, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.idom;

import com.potix.lang.Objects;
import com.potix.idom.impl.*;

/**
 * The iDOM entity reference.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/11 07:16:22 $
 */
public class EntityReference extends AbstractGroup
implements org.w3c.dom.EntityReference {
	/** The name. */
	protected String _name;

	/** Constructor.
	 */
	public EntityReference(String name) {
		setName(name);
	}	
	/** Constructor.
	 */
	protected EntityReference() {
	}

	//-- Item --//
	public final String getName() {
		return _name;
	}
	public final void setName(String name) {
		checkWritable();
		if (!Objects.equals(_name, name)) {
			Verifier.checkXMLName(name, getLocator());
			_name = name;
			setModified();
		}
	}

	//-- Node --//
	public final short getNodeType() {
		return ENTITY_REFERENCE_NODE;
	}

	//-- Object --//
	public String toString() {
		return "[EntityReference: &" + _name + ";]";
	}
}
