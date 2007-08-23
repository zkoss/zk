/* ComponentInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 10, 2007 11:37:02 AM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * This Class handle information for ZULJSF Components, for construct ZULJSF Component tree.
 * 
 * @author Dennis.Chen
 *
 */
/*package*/ class ComponentInfo {

	/**
	 * keep parent-children relation.
	 */
	private Map childrenMap = new HashMap();
	
	/**
	 * keep temporary id - component relation.
	 */
	private Map componentMap = new HashMap();
	
	/**
	 * Add component - child mapping for zuljsf component.
	 * @param parent a parent 
	 * @param child the children.
	 */
	/*package*/ void addChildInfo(AbstractComponent parent,AbstractComponent child){
		List children = (List)childrenMap.get(parent);
		if(children==null){
			children = new LinkedList();
			childrenMap.put(parent,children);
		}
		if(children.indexOf(child)==-1){
			children.add(child);
		}
	}
	
	/**
	 * get children of parent
	 * @param parent the parent
	 * @return
	 */
	/*package*/ List getChildrenInfo(AbstractComponent parent){
		return (List)childrenMap.get(parent);
	}
	
	 /**
	  * register ZULJSF Component to ComponentInfo, a temporary id will be associated to this componet
	  * @param comp a component
	  * @return a temporary id for this ComponentInfo.
	  */
	/*package*/ String registerComponent(AbstractComponent comp){
		String nid = nextId();
		componentMap.put(nid,comp);
		return nid;
	}
	
	/**
	 * get back the Component with temporary id
	 * @param a temporary id.
	 * @return
	 */
	/*package*/ AbstractComponent getRegistedComponent(String nid){
		return (AbstractComponent)componentMap.get(nid);
	}
	
	private int nextid = 1;
	synchronized private String nextId(){
		return "c_"+(nextid++);
		
	}
	
}
