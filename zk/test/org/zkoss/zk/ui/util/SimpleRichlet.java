/* SimpleRichlet.java

    Purpose:
        
    Description:
        
Copyright (C) 2014 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
    This program is distributed under LGPL Version 2.1 in the hope that
    it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.RichletConfig;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

/**
 * A simple {@link Richlet} implementation which do nothing.
 * 
 * <p>It should be used only to test instancing new richlet based on its class name.
 */
public class SimpleRichlet implements Richlet {
	public SimpleRichlet() {}
	public void init(RichletConfig config) {}
	public void destroy() {}
	public void service(Page page) throws Exception {}
	public LanguageDefinition getLanguageDefinition() { return null; }
}
