/** ProxyNode.java.

	Purpose:
		
	Description:
		
	History:
 		Tue May 10 18:44:32 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

import java.io.Serializable;
import java.util.Set;

import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.util.Pair;
import org.zkoss.zk.ui.util.Callback;

/**
 * A proxy object node (For the relationship of proxies)
 *
 * @author jameschu
 * @since 8.0.2
 */
public interface ProxyNode extends Serializable {
	public String getProperty();

	public void setProperty(String property);

	public ProxyNode getParent();

	public void setParent(ProxyNode parent);

	public Set<Pair<String, SavePropertyBinding>> getCachedSavePropertyBinding();

	public Callback getOnDirtyChangeCallback();

	public void setOnDirtyChangeCallback(Callback onDirtyChangeCallback);

	public Callback getOnDataChangeCallback();

	public void setOnDataChangeCallback(Callback onDataChangeCallback);
}