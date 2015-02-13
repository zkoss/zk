/* AbstractForEachStatus.java

	Purpose:
		
	Description:
		
	History:
		2012/1/5 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.annotation.Immutable;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ForEachStatus;

/**
 * The Class AbstractForEachStatus.
 *
 * @author dennis
 * @since 6.0.0
 */
//it is immutable
@Immutable
public abstract class AbstractForEachStatus implements ForEachStatus, Serializable{
	private static final long serialVersionUID = 1L;
	//not supported
	public ForEachStatus getPrevious(){
		return null;
	}
	
	//default 0
	public Integer getBegin(){
		return 0;
	}
	
	// default 1
	public Integer getStep() {
		return null;
	}
	// ZK-2552: for internal use only    
	public Integer getCurrentIndex(Component comp) {
		return comp.getParent().getChildren().indexOf(comp);
	}
	public boolean isFirst() {
		return getCount() == 1;
	}
	public boolean isLast() {
		return (getIndex() + 1) == getEnd();
	}
	public Object getEach() {
		return getCurrent();
	}
	public int getCount() {
		return getIndex() + 1;
	}
}
